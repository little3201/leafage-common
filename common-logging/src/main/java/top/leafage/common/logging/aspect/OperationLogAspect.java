/*
 * Copyright (c) 2026.  little3201.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.leafage.common.logging.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.multipart.MultipartFile;
import top.leafage.common.logging.annotation.OperationLog;
import top.leafage.common.logging.event.OperationLogEvent;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Operation log aspect
 *
 * @author wq li
 */
@Aspect
public class OperationLogAspect {

    private static final Set<String> QUERY_ACTIONS = Set.of("retrieve", "tree", "subset", "fetch");

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final ApplicationEventPublisher eventPublisher;

    /**
     * Constructor for OperationLogAspect.
     *
     * @param eventPublisher a {@link ApplicationEventPublisher} object
     */
    public OperationLogAspect(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * around.
     *
     * @param pjp          a {@link ProceedingJoinPoint} object
     * @param operationLog a {@link OperationLog} object
     * @return a {@link Object} object
     * @throws Throwable if any.
     */
    @Around("@within(operationLog)")
    public Object around(ProceedingJoinPoint pjp, OperationLog operationLog) throws Throwable {
        long start = System.currentTimeMillis();

        Object response = null;
        Throwable error = null;

        try {
            response = pjp.proceed();
            return response;
        } catch (Throwable e) {
            error = e;
            throw e;
        } finally {
            long duration = System.currentTimeMillis() - start;

            OperationLogEvent event = buildEvent(pjp, operationLog, response, error, duration);
            eventPublisher.publishEvent(event);
        }
    }

    private OperationLogEvent buildEvent(ProceedingJoinPoint pjp, OperationLog operationLog,
                                         Object response, Throwable error, long duration) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        String[] parameterNames = signature.getParameterNames();
        String action = signature.getMethod().getName();
        boolean isQuery = isQueryAction(action);

        Object[] args = pjp.getArgs();
        Map<String, Object> params = new LinkedHashMap<>();
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];

            if (shouldIgnore(arg)) {
                continue;
            }

            String parameterName = parameterNames != null && parameterNames.length > i
                    ? parameterNames[i]
                    : "arg" + i;
            params.put(parameterName, arg);
        }

        return OperationLogEvent.builder()
                .module(operationLog.value())
                .action(action)
                .targetId(targetId(parameterNames, args))
                .params(params)
                .response(isQuery ? null : toJsonSafe(response))
                .status(error == null ? OperationLogEvent.Status.SUCCEED : OperationLogEvent.Status.FAILED)
                .message(error != null ? error.getMessage() : null)
                .duration(duration)
                .build();
    }

    private boolean isQueryAction(String action) {
        if (action == null) return false;
        return QUERY_ACTIONS.contains(action.toLowerCase());
    }

    private boolean shouldIgnore(Object arg) {
        if (arg == null) {
            return false;
        }

        return arg instanceof File
                || arg instanceof MultipartFile;
    }

    /**
     * targetId.
     *
     * @param names an array of {@link java.lang.String} objects
     * @param args  an array of {@link java.lang.Object} objects
     * @return a {@link java.lang.Long} object
     */
    public Long targetId(String[] names, Object[] args) {
        if (names == null || args == null) {
            return null;
        }

        for (int i = 0; i < names.length; i++) {
            String name = names[i];

            if (isTargetId(name)) {
                Object val = args[i];

                return toLong(val);
            }
        }
        return null;
    }

    private boolean isTargetId(String name) {
        if (name == null) return false;

        return "id".equals(name)
                || name.endsWith("Id");
    }

    private Long toLong(Object val) {
        switch (val) {
            case null -> {
                return null;
            }
            case Long l -> {
                return l;
            }
            case Number n -> {
                return n.longValue();
            }
            default -> {
            }
        }

        try {
            return Long.parseLong(val.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isSkip(Object arg) {

        if (arg == null) return true;

        return arg instanceof File
                || arg instanceof MultipartFile;
    }

    private String toJsonSafe(Object obj) {
        if (obj == null) {
            return null;
        }

        try {
            String json = MAPPER.writeValueAsString(obj);

            // 防止日志过大
            if (json.length() > 2000) {
                return json.substring(0, 2000);
            }

            return json;
        } catch (Exception e) {
            return "\"[unserializable: " + e.getClass().getSimpleName() + "]\"";
        }
    }
}
