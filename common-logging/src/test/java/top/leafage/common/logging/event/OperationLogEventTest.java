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

package top.leafage.common.logging.event;

import org.junit.jupiter.api.Test;
import top.leafage.common.logging.autoconfigure.LoggingAutoConfiguration;
import top.leafage.common.logging.aspect.OperationLogAspect;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OperationLogEventTest {

    @Test
    void buildsAndUpdatesEventProperties() {
        OperationLogEvent event = OperationLogEvent.builder()
                .module("orders")
                .action("create")
                .targetId(3L)
                .params(Map.of("name", "item"))
                .response("{\"id\":3}")
                .status(OperationLogEvent.Status.SUCCEED)
                .duration(5L)
                .message("done")
                .operator("alice")
                .build();

        assertEquals("orders", event.getModule());
        assertEquals("create", event.getAction());
        assertEquals(3L, event.getTargetId());
        assertEquals(Map.of("name", "item"), event.getParams());
        assertEquals("{\"id\":3}", event.getResponse());
        assertEquals(OperationLogEvent.Status.SUCCEED, event.getStatus());
        assertEquals(5L, event.getDuration());
        assertEquals("done", event.getMessage());
        assertEquals("alice", event.getOperator());

        event.setModule("users");
        event.setAction("delete");
        event.setTargetId(4L);
        event.setParams(Map.of());
        event.setResponse(null);
        event.setStatus(OperationLogEvent.Status.FAILED);
        event.setDuration(6L);
        event.setMessage("failed");
        event.setOperator("bob");

        assertEquals("users", event.getModule());
        assertEquals("delete", event.getAction());
        assertEquals(4L, event.getTargetId());
        assertEquals(Map.of(), event.getParams());
        assertEquals(OperationLogEvent.Status.FAILED, event.getStatus());
        assertEquals(6L, event.getDuration());
        assertEquals("failed", event.getMessage());
        assertEquals("bob", event.getOperator());
        assertEquals(OperationLogEvent.Status.SUCCEED, OperationLogEvent.Status.of("succeed"));
    }

    @Test
    void createsOperationLogAspectFromAutoConfiguration() {
        OperationLogAspect aspect = new LoggingAutoConfiguration().operationLogAspect(event -> { });

        assertNotNull(aspect);
    }
}
