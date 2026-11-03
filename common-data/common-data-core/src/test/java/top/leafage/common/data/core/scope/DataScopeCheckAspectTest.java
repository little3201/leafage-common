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

import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.beans.factory.support.StaticListableBeanFactory;
import top.leafage.common.data.core.scope.annotation.DataScopeCheck;
import top.leafage.common.data.core.scope.enums.DataScopeType;
import top.leafage.common.data.core.scope.guard.DataScopeAccessChecker;
import top.leafage.common.data.core.scope.guard.DataScopeCheckAspect;
import top.leafage.common.data.core.scope.guard.DataScopeDeniedException;
import top.leafage.common.data.core.scope.guard.DataScopeGuard;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataScopeCheckAspectTest {

    private final DataSubjectResolver resolver = scope -> Set.of(new DataSubject("USER", "alice"));

    private final DataScopeAccessChecker checker =
            (resource, resourceId, owners) -> Long.valueOf(1L).equals(resourceId);

    @Test
    void proceedsWhenTheOwnerMatches() {
        ReportService service = proxy();

        boolean called = assertDoesNotThrow(() -> service.remove(1L));
        assertTrue(called);
    }

    @Test
    void deniesOtherRows() {
        ReportService service = proxy();

        assertThrows(DataScopeDeniedException.class, () -> service.remove(2L));
    }

    @Test
    void rejectsAnOutOfBoundsParameterIndex() {
        ReportService service = proxy();

        assertThrows(IllegalArgumentException.class, () -> service.removeWithoutId());
    }

    private ReportService proxy() {
        DataScopeEngine engine = new DataScopeEngine(
                (resource, action) -> List.of(DataScopeGrant.of(DataScopeType.SELF)), resolver);
        StaticListableBeanFactory beanFactory = new StaticListableBeanFactory();
        beanFactory.addBean("checker", checker);
        DataScopeGuard guard = new DataScopeGuard(engine,
                beanFactory.getBeanProvider(DataScopeAccessChecker.class));

        AspectJProxyFactory factory = new AspectJProxyFactory(new ReportService());
        factory.addAspect(new DataScopeCheckAspect(guard));
        return factory.getProxy();
    }

    static class ReportService {

        @DataScopeCheck("posts")
        public boolean remove(Long id) {
            return true;
        }

        @DataScopeCheck("posts")
        public void removeWithoutId() {
        }
    }
}
