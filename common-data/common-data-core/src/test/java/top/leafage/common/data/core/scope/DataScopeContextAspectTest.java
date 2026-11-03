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
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import top.leafage.common.data.core.scope.annotation.DataScope;
import top.leafage.common.data.core.scope.annotation.DataScopeIgnore;
import top.leafage.common.data.core.scope.context.DataScopeContext;
import top.leafage.common.data.core.scope.context.DataScopeContextAspect;
import top.leafage.common.data.core.scope.context.DataScopeContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

class DataScopeContextAspectTest {

    @AfterEach
    void clear() {
        DataScopeContextHolder.clear();
    }

    @Test
    void usesMethodNameAsAction() {
        Reports reports = proxy(new Reports());

        assertEquals("retrieve", reports.retrieve());
        assertNull(DataScopeContextHolder.currentAction());
    }

    @Test
    void honorsExplicitAction() {
        Reports reports = proxy(new Reports());

        assertEquals("read", reports.fetch());
    }

    @Test
    void exposesTheResourceRestriction() {
        Reports reports = proxy(new Reports());

        assertEquals("reports", reports.scoped());
    }

    @Test
    void ignoresAnnotatedMethods() {
        Reports reports = proxy(new Reports());

        assertFalse(reports.internal());
    }

    @Test
    void restoresTheOuterContext() {
        Reports reports = proxy(new Reports());

        assertEquals("inner", reports.outer());
        assertNull(DataScopeContextHolder.currentAction());
    }

    @Test
    void fallsBackToTheClassLevelResource() {
        ScopedReports reports = proxy(new ScopedReports());

        assertEquals("reports", reports.fetch());
    }

    private <T> T proxy(T target) {
        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        factory.addAspect(new DataScopeContextAspect());
        return factory.getProxy();
    }

    @DataScope
    static class Reports {

        String retrieve() {
            return DataScopeContextHolder.currentAction();
        }

        @DataScope(action = "read")
        String fetch() {
            return DataScopeContextHolder.currentAction();
        }

        @DataScope("reports")
        String scoped() {
            return DataScopeContextHolder.currentResource();
        }

        @DataScopeIgnore
        boolean internal() {
            return DataScopeContextHolder.isActive();
        }

        String outer() {
            DataScopeContextHolder.push(DataScopeContext.of("inner"));
            try {
                return DataScopeContextHolder.currentAction();
            } finally {
                DataScopeContextHolder.pop();
            }
        }
    }

    @DataScope("reports")
    static class ScopedReports {

        @DataScope(action = "read")
        String fetch() {
            return DataScopeContextHolder.currentResource();
        }
    }
}
