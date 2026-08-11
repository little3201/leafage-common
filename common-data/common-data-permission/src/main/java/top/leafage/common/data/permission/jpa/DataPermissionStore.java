package top.leafage.common.data.permission.jpa;

import java.util.Collection;
import java.util.List;

/**
 * Accesses generic database-backed data permission policies.
 */
public interface DataPermissionStore {

    /**
     * Finds policies assigned to any provided authorization subjects for one resource.
     *
     * @param resource resource identifier
     * @param subjectType authorization subject type
     * @param subjectIds authorization subject identifiers
     * @return matching policies
     */
    List<DataPermission> findByResourceAndSubjectTypeAndSubjectIds(String resource, String subjectType,
                                                                     Collection<String> subjectIds);
}
