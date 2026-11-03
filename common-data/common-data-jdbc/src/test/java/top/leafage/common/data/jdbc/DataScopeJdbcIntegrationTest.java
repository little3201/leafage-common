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

package top.leafage.common.data.jdbc;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import top.leafage.common.data.core.scope.DataSubject;
import top.leafage.common.data.core.scope.ResourceSubjectService;
import top.leafage.common.data.core.scope.context.DataScopeContext;
import top.leafage.common.data.core.scope.context.DataScopeContextHolder;
import top.leafage.common.data.core.scope.enums.DataScopeType;
import top.leafage.common.data.core.scope.guard.DataScopeDeniedException;
import top.leafage.common.data.core.scope.guard.DataScopeGuard;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = TestApplication.class, properties = {
        "spring.datasource.url=jdbc:h2:mem:datascopejdbc;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.sql.init.mode=always",
        "data.scope.enabled=true"
})
@Transactional
class DataScopeJdbcIntegrationTest {

    private static final DataSubject ALICE = new DataSubject("USER", "alice");

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ResourceSubjectRowRepository mappingRepository;

    @Autowired
    private TestPolicyProvider policyProvider;

    @Autowired
    private ResourceSubjectService resourceSubjectService;

    @Autowired
    private DataScopeGuard guard;

    private Post alicePost;

    private Post bobPost;

    private Post groupPost;

    @BeforeEach
    void setUp() {
        DataScopeContextHolder.clear();
        mappingRepository.deleteAll();
        postRepository.deleteAll();

        alicePost = postRepository.save(new Post("alice"));
        bobPost = postRepository.save(new Post("bob"));
        groupPost = postRepository.save(new Post("group"));

        mappingRepository.save(new ResourceSubjectRow("posts", alicePost.getId(), "USER", "alice"));
        mappingRepository.save(new ResourceSubjectRow("posts", groupPost.getId(), "GROUP", "g1"));
        policyProvider.reset(DataScopeType.CUSTOM, Set.of(ALICE));
    }

    @AfterEach
    void tearDown() {
        DataScopeContextHolder.clear();
    }

    @Test
    void filtersListsCountsAndSingleReads() {
        assertEquals(List.of("alice"), titles(within("retrieve", () -> postRepository.findAll())));
        long count = within("retrieve", postRepository::count);
        assertEquals(1L, count);
        assertEquals(Optional.empty(), within("fetch", () -> postRepository.findById(bobPost.getId())));
        assertTrue(within("fetch", () -> postRepository.findById(alicePost.getId())).isPresent());
    }

    @Test
    void appliesPermissionToPagedReadsAndTotalCount() {
        Page<Post> page = within("retrieve", () -> postRepository.findAll(PageRequest.of(0, 10)));

        assertEquals(1, page.getTotalElements());
        assertEquals(List.of("alice"), titles(page.getContent()));
    }

    @Test
    void appliesPermissionToIdentifierQueries() {
        assertFalse(within("fetch", () -> postRepository.existsById(bobPost.getId())));
        assertEquals(List.of("alice"), titles(within("retrieve",
                () -> postRepository.findAllById(List.of(alicePost.getId(), bobPost.getId())))));
    }

    @Test
    void allScopeSeesEveryRow() {
        policyProvider.reset(DataScopeType.ALL, Set.of());

        assertEquals(3, list(within("retrieve", () -> postRepository.findAll())).size());
    }

    @Test
    void missingPolicyDeniesEverything() {
        policyProvider.reset(null, Set.of());

        assertTrue(list(within("retrieve", () -> postRepository.findAll())).isEmpty());
        long count = within("retrieve", postRepository::count);
        assertEquals(0L, count);
        assertTrue(within("fetch", () -> postRepository.findById(alicePost.getId())).isEmpty());
    }

    @Test
    void groupScopeUsesResolvedOwners() {
        policyProvider.reset(DataScopeType.GROUP, Set.of());

        assertEquals(List.of("group"), titles(within("retrieve", () -> postRepository.findAll())));
    }

    @Test
    void resourceRestrictionLeavesOtherEntitiesUntouched() {
        policyProvider.reset(null, Set.of());

        assertEquals(3, list(within("other", "retrieve", () -> postRepository.findAll())).size());
        assertTrue(list(within("posts", "retrieve", () -> postRepository.findAll())).isEmpty());
    }

    @Test
    void deleteByIdDoesNotRemoveAnInaccessibleRow() {
        within("remove", () -> {
            postRepository.deleteById(bobPost.getId());
            return null;
        });

        assertTrue(postRepository.findById(bobPost.getId()).isPresent());
    }

    @Test
    void guardAllowsTheOwnerAndDeniesOtherRows() {
        within("modify", () -> {
            guard.check("posts", "modify", alicePost.getId());
            assertThrows(DataScopeDeniedException.class,
                    () -> guard.check("posts", "modify", bobPost.getId()));
            return null;
        });
    }

    @Test
    void bindsAndUnbindsExplicitSubjects() {
        DataSubject bob = new DataSubject("USER", "bob");

        resourceSubjectService.bind(bobPost, bob);

        assertTrue(StreamSupport.stream(mappingRepository.findAll().spliterator(), false)
                .anyMatch(row -> row.getResourceId().equals(bobPost.getId())
                        && "USER".equals(row.getSubjectType())
                        && "bob".equals(row.getSubjectId())));

        resourceSubjectService.unbind(bobPost, bob);

        assertFalse(StreamSupport.stream(mappingRepository.findAll().spliterator(), false)
                .anyMatch(row -> row.getResourceId().equals(bobPost.getId())
                        && "USER".equals(row.getSubjectType())
                        && "bob".equals(row.getSubjectId())));
    }

    private <T> T within(String action, Supplier<T> supplier) {
        DataScopeContextHolder.push(DataScopeContext.of(action));
        try {
            return supplier.get();
        } finally {
            DataScopeContextHolder.pop();
        }
    }

    private <T> T within(String resource, String action, Supplier<T> supplier) {
        DataScopeContextHolder.push(DataScopeContext.of(action, resource));
        try {
            return supplier.get();
        } finally {
            DataScopeContextHolder.pop();
        }
    }

    private List<Post> list(Iterable<Post> posts) {
        return StreamSupport.stream(posts.spliterator(), false).toList();
    }

    private List<String> titles(Iterable<Post> posts) {
        return list(posts).stream().map(Post::getTitle).toList();
    }
}
