package top.leafage.common.data.permission;

import org.springframework.data.jpa.domain.Specification;

/**
 * Builds a database-side data-permission predicate from configured policies.
 */
public interface DataPermissionSpecification {

    /**
     * Builds the visibility predicate for one entity type.
     *
     * @param entityType the entity type the specification will be applied to;
     *                   only policies of this entity are considered
     * @param <T>        the entity type
     * @return a specification that restricts visible rows of {@code entityType}
     */
    <T> Specification<T> forEntity(Class<T> entityType);
}
