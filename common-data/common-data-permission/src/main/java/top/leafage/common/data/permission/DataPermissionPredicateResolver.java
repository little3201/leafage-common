package top.leafage.common.data.permission;

import org.springframework.data.jpa.domain.Specification;
import top.leafage.common.data.permission.enums.DataScopeType;

import java.util.Set;

/**
 * Converts resolved subjects into entity-specific JPA predicates.
 */
public interface DataPermissionPredicateResolver {

    /**
     * Builds a predicate without hardcoding entity fields in the common module.
     *
     * @param resource  the database-resolved resource or table code
     * @param scopeType the effective limited scope
     * @param subjects  resolved user or group subjects
     * @param <T>       the entity type
     * @return the data-permission predicate
     */
    <T> Specification<T> resolve(String resource, DataScopeType scopeType,
                                 Set<DataPermissionSubject> subjects);
}
