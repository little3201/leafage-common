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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import top.leafage.common.logging.annotation.OperationLog;
import top.leafage.common.logging.event.OperationLogEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Operation log aspect
 *
 * @author wq li
 */
@Aspect
@Component
public class OperationLogAspect {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final ApplicationEventPublisher eventPublisher;

    public OperationLogAspect(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Around("@annotation(operationLog)")
    public Object around(ProceedingJoinPoint pjp, OperationLog operationLog) throws Throwable {
        long start = System.currentTimeMillis();

        Object result = null;
        Throwable error = null;

        try {
            result = pjp.proceed();
            return result;
        } catch (Throwable e) {
            error = e;
            throw e;
        } finally {
            long duration = System.currentTimeMillis() - start;

            OperationLogEvent event = buildEvent(pjp, operationLog, result, error, duration);
            eventPublisher.publishEvent(event);
        }
    }

    private OperationLogEvent buildEvent(ProceedingJoinPoint pjp, OperationLog operationLog,
                                         Object result, Throwable error, long duration) {
        Object[] args = pjp.getArgs();
        List<Object> serializableArgs = new ArrayList<>();
        for (Object arg : args) {
            if (arg instanceof File ||
                    arg instanceof MultipartFile) {
                continue; // 跳过不可序列化的对象
            }
            serializableArgs.add(arg);
        }

        return OperationLogEvent.builder()
                .module(operationLog.module())
                .action(operationLog.action())
                .params(toJsonSafe(serializableArgs))
                .result(toJsonSafe(result))
                .status(error == null ? 1 : 0)
                .message(error != null ? error.getMessage() : null)
                .duration(duration)
                .build();
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
