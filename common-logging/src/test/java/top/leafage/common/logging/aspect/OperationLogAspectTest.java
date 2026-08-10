/*
 * Copyright(c) 2019-present the original author or authors.
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

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import top.leafage.common.logging.annotation.OperationLog;
import top.leafage.common.logging.event.OperationLogEvent;

import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OperationLogAspectTest {

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void publishesSuccessfulQueryEventWithoutResponseOrFileParameter() throws Throwable {
        List<Object> events = new ArrayList<>();
        OperationLogAspect aspect = new OperationLogAspect(events::add);
        SecurityContextHolder.getContext().setAuthentication(
                UsernamePasswordAuthenticationToken.authenticated("alice", "n/a", AuthorityUtils.NO_AUTHORITIES));
        Method method = LoggedService.class.getDeclaredMethod("retrieve", Long.class, File.class);

        Object result = aspect.around(joinPoint(method, new String[] {"id", "file"},
                new Object[] {7L, new File("upload.tmp")}, Map.of("name", "item")), annotation());

        assertEquals(Map.of("name", "item"), result);
        OperationLogEvent event = (OperationLogEvent) events.getFirst();
        assertEquals("inventory", event.getModule());
        assertEquals("retrieve", event.getAction());
        assertEquals(7L, event.getTargetId());
        assertEquals(Map.of("id", 7L), event.getParams());
        assertNull(event.getResponse());
        assertEquals(OperationLogEvent.Status.SUCCEED, event.getStatus());
        assertEquals("alice", event.getOperator());
        assertTrue(event.getDuration() >= 0);
    }

    @Test
    void publishesFailureEventAndRethrowsBusinessException() throws Throwable {
        List<Object> events = new ArrayList<>();
        OperationLogAspect aspect = new OperationLogAspect(events::add);
        Method method = LoggedService.class.getDeclaredMethod("update", String.class);
        IllegalStateException failure = new IllegalStateException("not allowed");

        IllegalStateException thrown = assertThrows(IllegalStateException.class,
                () -> aspect.around(joinPoint(method, new String[] {"name"}, new Object[] {"item"}, failure), annotation()));

        assertEquals(failure, thrown);
        OperationLogEvent event = (OperationLogEvent) events.getFirst();
        assertEquals(OperationLogEvent.Status.FAILED, event.getStatus());
        assertEquals("not allowed", event.getMessage());
        assertNull(event.getResponse());
        assertNull(event.getOperator());
    }

    @Test
    void extractsTargetIdsSafelyAndValidatesPublisher() {
        OperationLogAspect aspect = new OperationLogAspect(event -> { });

        assertEquals(8L, aspect.targetId(new String[] {"userID"}, new Object[] {"8"}));
        assertNull(aspect.targetId(new String[] {"id"}, new Object[0]));
        assertNull(aspect.targetId(null, new Object[] {1L}));
        assertThrows(NullPointerException.class, () -> new OperationLogAspect(null));
    }

    private static OperationLog annotation() {
        return LoggedService.class.getAnnotation(OperationLog.class);
    }

    private static ProceedingJoinPoint joinPoint(Method method, String[] parameterNames, Object[] args, Object result) {
        MethodSignature signature = proxy(MethodSignature.class, (proxy, invoked, ignored) -> switch (invoked.getName()) {
            case "getMethod" -> method;
            case "getParameterNames" -> parameterNames;
            case "getName" -> method.getName();
            default -> defaultValue(invoked.getReturnType());
        });
        return proxy(ProceedingJoinPoint.class, (proxy, invoked, ignored) -> {
            return switch (invoked.getName()) {
                case "getSignature" -> signature;
                case "getArgs" -> args;
                case "proceed" -> {
                    if (result instanceof Throwable throwable) {
                        throw throwable;
                    }
                    yield result;
                }
                default -> defaultValue(invoked.getReturnType());
            };
        });
    }

    @SuppressWarnings("unchecked")
    private static <T> T proxy(Class<T> type, InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type}, handler);
    }

    private static Object defaultValue(Class<?> type) {
        if (!type.isPrimitive()) {
            return null;
        }
        if (type == boolean.class) {
            return false;
        }
        return 0;
    }

    @OperationLog("inventory")
    static class LoggedService {
        void retrieve(Long id, File file) {
        }

        void update(String name) {
        }
    }
}
