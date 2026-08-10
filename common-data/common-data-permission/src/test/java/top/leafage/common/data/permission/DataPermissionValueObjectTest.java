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

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataPermissionValueObjectTest {

    @Test
    void createsAndDefensivelyCopiesAccessScopes() {
        Set<DataScopeType> scopeTypes = new HashSet<>(Set.of(DataScopeType.SELF));
        Set<DataSubject> subjects = new HashSet<>(Set.of(new DataSubject(DataSubjectType.USER, "alice")));

        DataAccessScope scope = new DataAccessScope(scopeTypes, subjects, false);
        scopeTypes.clear();
        subjects.clear();

        assertEquals(Set.of(DataScopeType.SELF), scope.scopeTypes());
        assertEquals(Set.of(new DataSubject(DataSubjectType.USER, "alice")), scope.subjects());
        assertFalse(scope.isEmpty());
        assertThrows(UnsupportedOperationException.class, () -> scope.subjects().clear());
    }

    @Test
    void createsNoneAndAllAccessScopes() {
        DataAccessScope none = DataAccessScope.none();
        DataAccessScope all = DataAccessScope.allAccess();

        assertTrue(none.isEmpty());
        assertFalse(none.all());
        assertEquals(Set.of(DataScopeType.NONE), none.scopeTypes());
        assertTrue(all.all());
        assertFalse(all.isEmpty());
        assertEquals(Set.of(DataScopeType.ALL), all.scopeTypes());
    }

    @Test
    void rejectsInconsistentAccessScopes() {
        assertThrows(IllegalArgumentException.class,
                () -> new DataAccessScope(Set.of(DataScopeType.SELF), Set.of(), true));
        assertThrows(IllegalArgumentException.class,
                () -> new DataAccessScope(Set.of(DataScopeType.ALL), Set.of(), false));
        assertThrows(IllegalArgumentException.class,
                () -> new DataAccessScope(Set.of(DataScopeType.NONE, DataScopeType.SELF), Set.of(), false));
        assertThrows(IllegalArgumentException.class,
                () -> new DataAccessScope(Set.of(DataScopeType.NONE),
                        Set.of(new DataSubject(DataSubjectType.USER, "alice")), false));
    }

    @Test
    void validatesPermissionValueObjects() {
        assertEquals("alice", new DataPermissionContext("alice").principalName());
        assertEquals("POSTS", new DataPermissionRequest("POSTS", "posts:read").resource());
        assertEquals("posts:read", new DataPermissionRequest("POSTS", "posts:read").authority());
        assertEquals(DataSubjectType.USER, new DataSubject(DataSubjectType.USER, "alice").type());
        assertEquals("alice", new DataSubject(DataSubjectType.USER, "alice").id());

        assertThrows(NullPointerException.class, () -> new DataPermissionContext(null));
        assertThrows(IllegalArgumentException.class, () -> new DataPermissionContext(" "));
        assertThrows(NullPointerException.class, () -> new DataPermissionRequest(null, "read"));
        assertThrows(IllegalArgumentException.class, () -> new DataPermissionRequest("POSTS", " "));
        assertThrows(NullPointerException.class, () -> new DataSubject(null, "alice"));
        assertThrows(IllegalArgumentException.class, () -> new DataSubject(DataSubjectType.USER, " "));
    }

    @Test
    void retainsDeniedExceptionMessage() {
        DataPermissionDeniedException exception = new DataPermissionDeniedException("access denied");

        assertEquals("access denied", exception.getMessage());
    }
}
