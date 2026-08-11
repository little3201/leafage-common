package top.leafage.common.data.permission;

import org.springframework.data.jpa.domain.Specification;

/**
 * Builds a database-side data-permission predicate from configured policies.
 */
public interface DataPermissionSpecification {

    /**
     * Builds the visibility predicate for one entity type.
     *
     * @param <T> the entity type
     * @return a specification that restricts visible rows
     */
    <T> Specification<T> forCurrentOperation();
}
