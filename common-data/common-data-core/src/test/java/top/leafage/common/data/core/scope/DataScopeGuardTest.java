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
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.support.StaticListableBeanFactory;
import top.leafage.common.data.core.scope.enums.DataScopeType;
import top.leafage.common.data.core.scope.guard.DataScopeAccessChecker;
import top.leafage.common.data.core.scope.guard.DataScopeDeniedException;
import top.leafage.common.data.core.scope.guard.DataScopeGuard;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DataScopeGuardTest {

    private final DataSubjectResolver resolver = scope -> Set.of(new DataSubject("USER", "alice"));

    @Test
    void allowsUnrestrictedResources() {
        DataScopeGuard guard = guard(DataScopeType.ALL, (resource, id, owners) -> false);

        assertDoesNotThrow(() -> guard.check("posts", "remove", 1L));
    }

    @Test
    void deniesWhenNoPolicyMatches() {
        DataScopeGuard guard = guard(null, (resource, id, owners) -> true);

        assertThrows(DataScopeDeniedException.class,
                () -> guard.check("posts", "remove", 1L));
    }

    @Test
    void refusesRestrictedAccessWhenNoCheckerIsConfigured() {
        DataScopeEngine engine = engine(DataScopeType.SELF);
        DataScopeGuard guard = new DataScopeGuard(engine, provider(null));

        assertThrows(DataScopeDeniedException.class,
                () -> guard.check("posts", "remove", 1L));
    }

    @Test
    void checksOwnersForRestrictedAccess() {
        DataScopeGuard allowed = guard(DataScopeType.SELF, (resource, id, owners) -> true);
        DataScopeGuard denied = guard(DataScopeType.SELF, (resource, id, owners) -> false);

        assertDoesNotThrow(() -> allowed.check("posts", "modify", 1L));
        assertThrows(DataScopeDeniedException.class,
                () -> denied.check("posts", "modify", 1L));
    }

    private DataScopeGuard guard(DataScopeType scope, DataScopeAccessChecker checker) {
        return new DataScopeGuard(engine(scope), provider(checker));
    }

    private DataScopeEngine engine(DataScopeType scope) {
        DataScopePolicyProvider provider = (resource, action) -> scope == null
                ? List.of()
                : List.of(DataScopeGrant.of(scope));
        return new DataScopeEngine(provider, resolver);
    }

    private ObjectProvider<DataScopeAccessChecker> provider(DataScopeAccessChecker checker) {
        StaticListableBeanFactory beanFactory = new StaticListableBeanFactory();
        if (checker != null) {
            beanFactory.addBean("accessChecker", checker);
        }
        return beanFactory.getBeanProvider(DataScopeAccessChecker.class);
    }
}
