package top.leafage.common.data.core;

import org.junit.jupiter.api.Test;
import top.leafage.common.data.core.converter.ModelToTreeNodeConverter;
import top.leafage.common.data.core.domain.TreeNode;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TreeAndPagingTest {

    @Test
    void buildsTreeAndConvertsExpandedProperties() {
        List<TreeNode<Long>> roots = ModelToTreeNodeConverter.toTree(List.of(
                new Node(1L, "root", null, "one"), new Node(2L, "child", 1L, "two")), Set.of("code"));

        assertEquals(1, roots.size());
        assertEquals("one", roots.getFirst().getMeta().get("code"));
        assertEquals(2L, roots.getFirst().getChildren().getFirst().getId());
    }

    @Test
    void handlesMissingPropertiesAndValidatesInput() {
        assertNull(top.leafage.common.data.core.converter.AbstractTreeNodeConverter.convertValue("missing", "1", Node.class));
        assertThrows(NullPointerException.class,
                () -> top.leafage.common.data.core.converter.AbstractTreeNodeConverter.createNode(null, Set.of()));
    }

    @Test
    void createsPageableWithDefaultSortAndBounds() {
        PagingAndSortService service = new PagingAndSortService() { };

        assertEquals("id", service.pageable(0, 10, null, false).getSort().getOrderFor("id").getProperty());
        assertThrows(IllegalArgumentException.class, () -> service.pageable(0, 101, "name", false));
    }

    public static class Node {
        private final Long id;
        private final String name;
        private final Long superiorId;
        private final String code;

        Node(Long id, String name, Long superiorId, String code) {
            this.id = id;
            this.name = name;
            this.superiorId = superiorId;
            this.code = code;
        }

        public Long getId() { return id; }
        public String getName() { return name; }
        public Long getSuperiorId() { return superiorId; }
        public String getCode() { return code; }
    }
}
