package top.leafage.common.data.permission;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;
import top.leafage.common.data.permission.enums.DataScopeType;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class DataPermissionSpecificationTest {

    @Test
    void appliesActionOverrideAndConfiguredCustomSubjects() {
        AtomicReference<Class<?>> capturedEntityType = new AtomicReference<>();
        AtomicReference<String> capturedResource = new AtomicReference<>();
        AtomicReference<DataScopeType> capturedScope = new AtomicReference<>();
        AtomicReference<Set<DataPermissionSubject>> capturedSubjects = new AtomicReference<>();
        Specification<Post> expected = (root, query, criteriaBuilder) -> null;
        DataPermissionPredicateResolver predicateResolver = new DataPermissionPredicateResolver() {
            @Override
            public <T> Specification<T> resolve(Class<T> entityType, String resource, DataScopeType scope,
                                                Set<DataPermissionSubject> subjects) {
                capturedEntityType.set(entityType);
                capturedResource.set(resource);
                capturedScope.set(scope);
                capturedSubjects.set(subjects);
                @SuppressWarnings("unchecked")
                Specification<T> specification = (Specification<T>) expected;
                return specification;
            }
        };
        Set<DataPermissionSubject> customSubjects = Set.of(
                new DataPermissionSubject("USER", "alice"),
                new DataPermissionSubject("GROUP", "12"));
        DataPermissionPolicy policy = new DataPermissionPolicy(21L, Set.of("modify", "remove"),
                new DataScopeConfiguration(DataScopeType.GROUP,
                        Map.of("remove", DataScopeType.CUSTOM), customSubjects));
        DataPermissionSpecification specifications = new ConfiguredDataPermissionSpecification(
                () -> List.of(new ResolvedDataPermissionPolicy("posts", "remove", policy)),
                scope -> Set.of(), predicateResolver);

        Specification<Post> actual = specifications.forEntity(Post.class);

        assertSame(expected, actual);
        assertEquals(Post.class, capturedEntityType.get());
        assertEquals("posts", capturedResource.get());
        assertEquals(DataScopeType.CUSTOM, capturedScope.get());
        assertEquals(customSubjects, capturedSubjects.get());
    }

    @Test
    void resolvesDefaultScopeSubjectsForConfiguredAction() {
        AtomicReference<DataScopeType> resolvedScope = new AtomicReference<>();
        DataPermissionPolicy policy = new DataPermissionPolicy(21L, Set.of("modify"),
                new DataScopeConfiguration(DataScopeType.GROUP, Map.of(), Set.of()));
        DataPermissionSpecification specifications = new ConfiguredDataPermissionSpecification(
                () -> List.of(new ResolvedDataPermissionPolicy("posts", "modify", policy)),
                scope -> {
                    resolvedScope.set(scope);
                    return Set.of(new DataPermissionSubject("GROUP", "12"));
                },
                new DataPermissionPredicateResolver() {
                    @Override
                    public <T> Specification<T> resolve(Class<T> entityType, String resource, DataScopeType scope,
                                                        Set<DataPermissionSubject> subjects) {
                        return (root, query, criteriaBuilder) -> null;
                    }
                });

        specifications.forEntity(Post.class);

        assertEquals(DataScopeType.GROUP, resolvedScope.get());
    }

    static class Post {
    }
}
