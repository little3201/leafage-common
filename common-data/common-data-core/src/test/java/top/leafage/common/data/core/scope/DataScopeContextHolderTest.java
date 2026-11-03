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

package top.leafage.common.data.core.scope;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import top.leafage.common.data.core.scope.context.DataScopeContext;
import top.leafage.common.data.core.scope.context.DataScopeContextHolder;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataScopeContextHolderTest {

    @AfterEach
    void clear() {
        DataScopeContextHolder.clear();
    }

    @Test
    void pushesAndPopsAnAction() {
        DataScopeContextHolder.push(DataScopeContext.of("retrieve"));

        assertTrue(DataScopeContextHolder.isActive());
        assertEquals("retrieve", DataScopeContextHolder.currentAction());
        assertTrue(DataScopeContextHolder.pop());
        assertFalse(DataScopeContextHolder.isActive());
    }

    @Test
    void keepsNestedActions() {
        DataScopeContextHolder.push(DataScopeContext.of("retrieve"));
        DataScopeContextHolder.push(DataScopeContext.of("modify"));

        assertEquals("modify", DataScopeContextHolder.currentAction());
        assertFalse(DataScopeContextHolder.pop());
        assertEquals("retrieve", DataScopeContextHolder.currentAction());
        assertTrue(DataScopeContextHolder.pop());
    }

    @Test
    void exposesTheOptionalResourceRestriction() {
        DataScopeContextHolder.push(DataScopeContext.of("retrieve", "reports"));

        assertEquals("reports", DataScopeContextHolder.currentResource());

        DataScopeContextHolder.pop();
        assertNull(DataScopeContextHolder.currentResource());
    }

    @Test
    void ignoredContextBypassesFiltering() {
        DataScopeContextHolder.push(DataScopeContext.ignoredContext());

        assertFalse(DataScopeContextHolder.isActive());
        assertNull(DataScopeContextHolder.currentAction());
    }

    @Test
    void propagatesContextToAnotherThread() throws Exception {
        DataScopeContextHolder.push(DataScopeContext.of("retrieve"));
        AtomicReference<String> action = new AtomicReference<>();

        Thread thread = new Thread(DataScopeContextHolder.wrap(
                () -> action.set(DataScopeContextHolder.currentAction())));
        thread.start();
        thread.join();

        assertEquals("retrieve", action.get());
    }

    @Test
    void generationChangesForEachOutermostScope() {
        long initial = DataScopeContextHolder.generation();
        DataScopeContextHolder.push(DataScopeContext.of("retrieve"));
        long active = DataScopeContextHolder.generation();
        DataScopeContextHolder.pop();
        long finished = DataScopeContextHolder.generation();

        assertTrue(active > initial);
        assertTrue(finished > active);
    }
}
