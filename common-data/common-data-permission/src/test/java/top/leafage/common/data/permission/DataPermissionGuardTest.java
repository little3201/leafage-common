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

package top.leafage.common.data.permission;

import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataPermissionGuardTest {

    private static final DataPermissionRequest REQUEST = new DataPermissionRequest("POSTS", "posts:modify");

    @Test
    void allowsAnyResourceForAllScope() {
        DataPermissionGuard guard = new DataPermissionGuard(
                () -> Optional.of(new DataPermissionContext("alice")),
                (context, request) -> DataAccessScope.allAccess(),
                (resource, resourceId, scope) -> false);

        assertDoesNotThrow(() -> guard.check(REQUEST, 1L));
    }

    @Test
    void deniesAccessForEmptyScope() {
        DataPermissionGuard guard = new DataPermissionGuard(
                () -> Optional.of(new DataPermissionContext("alice")),
                (context, request) -> DataAccessScope.none(),
                (resource, resourceId, scope) -> true);

        assertThrows(DataPermissionDeniedException.class, () -> guard.check(REQUEST, 1L));
    }

    @Test
    void deniesAccessWithoutAuthenticatedPrincipal() {
        DataPermissionGuard guard = new DataPermissionGuard(
                Optional::<DataPermissionContext>empty,
                (context, request) -> DataAccessScope.allAccess(),
                (resource, resourceId, scope) -> true);

        assertThrows(DataPermissionDeniedException.class, () -> guard.check(REQUEST, 1L));
    }

    @Test
    void resolvesScopeUsingCurrentContextAndRequest() {
        DataPermissionContext context = new DataPermissionContext("alice");
        DataAccessScope expected = DataAccessScope.allAccess();
        AtomicBoolean invoked = new AtomicBoolean();
        DataPermissionGuard guard = new DataPermissionGuard(
                () -> Optional.of(context),
                (actualContext, actualRequest) -> {
                    assertSame(context, actualContext);
                    assertSame(REQUEST, actualRequest);
                    invoked.set(true);
                    return expected;
                },
                (resource, resourceId, scope) -> false);

        assertSame(expected, guard.resolve(REQUEST));
        assertTrue(invoked.get());
    }

    @Test
    void delegatesNonEmptyScopeToSubjectStore() {
        DataAccessScope scope = new DataAccessScope(
                java.util.Set.of(DataScopeType.SELF),
                java.util.Set.of(new DataSubject(DataSubjectType.USER, "alice")), false);
        AtomicBoolean invoked = new AtomicBoolean();
        DataPermissionGuard guard = new DataPermissionGuard(
                () -> Optional.of(new DataPermissionContext("alice")),
                (context, request) -> scope,
                (resource, resourceId, actualScope) -> {
                    assertEquals("POSTS", resource);
                    assertEquals(1L, resourceId);
                    assertSame(scope, actualScope);
                    invoked.set(true);
                    return true;
                });

        assertDoesNotThrow(() -> guard.check(REQUEST, 1L));
        assertTrue(invoked.get());
    }

    @Test
    void deniesWhenSubjectStoreRejectsResource() {
        DataAccessScope scope = new DataAccessScope(
                java.util.Set.of(DataScopeType.SELF),
                java.util.Set.of(new DataSubject(DataSubjectType.USER, "alice")), false);
        DataPermissionGuard guard = new DataPermissionGuard(
                () -> Optional.of(new DataPermissionContext("alice")),
                (context, request) -> scope,
                (resource, resourceId, actualScope) -> false);

        assertThrows(DataPermissionDeniedException.class, () -> guard.check(REQUEST, 1L));
    }

    @Test
    void rejectsInvalidArgumentsAndCollaboratorResults() {
        assertThrows(NullPointerException.class, () -> new DataPermissionGuard(null,
                (context, request) -> DataAccessScope.allAccess(),
                (resource, resourceId, scope) -> true));
        assertThrows(NullPointerException.class, () -> new DataPermissionGuard(
                () -> Optional.of(new DataPermissionContext("alice")), null,
                (resource, resourceId, scope) -> true));
        assertThrows(NullPointerException.class, () -> new DataPermissionGuard(
                () -> Optional.of(new DataPermissionContext("alice")),
                (context, request) -> DataAccessScope.allAccess(), null));

        DataPermissionGuard guard = new DataPermissionGuard(
                () -> Optional.of(new DataPermissionContext("alice")),
                (context, request) -> DataAccessScope.allAccess(),
                (resource, resourceId, scope) -> true);

        assertThrows(NullPointerException.class, () -> guard.resolve(null));
        assertThrows(NullPointerException.class, () -> guard.check(REQUEST, null));

        DataPermissionGuard nullContextProvider = new DataPermissionGuard(
                () -> null,
                (context, request) -> DataAccessScope.allAccess(),
                (resource, resourceId, scope) -> true);
        assertThrows(NullPointerException.class, () -> nullContextProvider.resolve(REQUEST));

        DataPermissionGuard nullEvaluator = new DataPermissionGuard(
                () -> Optional.of(new DataPermissionContext("alice")),
                (context, request) -> null,
                (resource, resourceId, scope) -> true);
        assertThrows(NullPointerException.class, () -> nullEvaluator.resolve(REQUEST));
    }
}
