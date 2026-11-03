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
import top.leafage.common.data.core.scope.context.DataScopeContext;
import top.leafage.common.data.core.scope.context.DataScopeContextHolder;
import top.leafage.common.data.core.scope.enums.DataScopeType;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class DataScopeEngineTest {

    private static final DataSubject ALICE = new DataSubject("USER", "alice");
    private static final DataSubject GROUP = new DataSubject("GROUP", "g1");

    private final DataSubjectResolver resolver = scope -> switch (scope) {
        case SELF -> Set.of(ALICE);
        case GROUP -> Set.of(GROUP);
        default -> Set.of();
    };

    @Test
    void deniesWhenNoGrantExists() {
        DataScopeEngine engine = engine((resource, action) -> List.of());

        assertInstanceOf(DataScopeDecision.Denied.class, engine.decide("posts", "retrieve"));
    }

    @Test
    void unrestrictedWinsOverOtherGrants() {
        DataScopeEngine engine = engine((resource, action) -> List.of(
                DataScopeGrant.of(DataScopeType.SELF),
                DataScopeGrant.of(DataScopeType.ALL)));

        assertInstanceOf(DataScopeDecision.Unrestricted.class,
                engine.decide("posts", "retrieve"));
    }

    @Test
    void unionsResolvedAndCustomOwners() {
        DataSubject custom = new DataSubject("USER", "bob");
        DataScopeEngine engine = engine((resource, action) -> List.of(
                DataScopeGrant.of(DataScopeType.SELF),
                new DataScopeGrant(DataScopeType.CUSTOM, Set.of(custom))));

        DataScopeDecision decision = engine.decide("posts", "retrieve");

        DataScopeDecision.Restricted restricted =
                assertInstanceOf(DataScopeDecision.Restricted.class, decision);
        assertEquals(Set.of(ALICE, custom), restricted.owners());
    }

    @Test
    void resolvesGroupScopeThroughResolver() {
        DataScopeEngine engine = engine((resource, action) ->
                List.of(DataScopeGrant.of(DataScopeType.GROUP)));

        DataScopeDecision.Restricted restricted = assertInstanceOf(
                DataScopeDecision.Restricted.class, engine.decide("posts", "retrieve"));
        assertEquals(Set.of(GROUP), restricted.owners());
    }

    @Test
    void deniesWhenOnlyNoneGrantsExist() {
        DataScopeEngine engine = engine((resource, action) ->
                List.of(DataScopeGrant.of(DataScopeType.NONE)));

        assertInstanceOf(DataScopeDecision.Denied.class, engine.decide("posts", "retrieve"));
    }

    @Test
    void cachesDecisionWithinOneScope() {
        AtomicInteger calls = new AtomicInteger();
        DataScopeEngine engine = engine((resource, action) -> {
            calls.incrementAndGet();
            return List.of(DataScopeGrant.of(DataScopeType.SELF));
        });

        engine.decide("posts", "retrieve");
        engine.decide("posts", "retrieve");

        assertEquals(1, calls.get());
    }

    @Test
    void invalidatesCacheForANewScope() {
        AtomicInteger calls = new AtomicInteger();
        DataScopeEngine engine = engine((resource, action) -> {
            calls.incrementAndGet();
            return List.of(DataScopeGrant.of(DataScopeType.SELF));
        });

        try (Scope ignored = new Scope()) {
            engine.decide("posts", "retrieve");
        }
        try (Scope ignored = new Scope()) {
            engine.decide("posts", "retrieve");
        }

        assertEquals(2, calls.get());
    }

    private DataScopeEngine engine(DataScopePolicyProvider provider) {
        return new DataScopeEngine(provider, resolver);
    }

    private static final class Scope implements AutoCloseable {

        private Scope() {
            DataScopeContextHolder.push(DataScopeContext.of("retrieve"));
        }

        @Override
        public void close() {
            DataScopeContextHolder.pop();
        }
    }
}
