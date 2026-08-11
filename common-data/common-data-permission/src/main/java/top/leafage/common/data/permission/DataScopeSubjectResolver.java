package top.leafage.common.data.permission;

import top.leafage.common.data.permission.enums.DataScopeType;

import java.util.Set;

/**
 * Resolves the current caller's subjects for non-custom limited scopes.
 */
public interface DataScopeSubjectResolver {

    /**
     * Resolves subjects for a scope such as SELF, GROUP or GROUP_TREE.
     *
     * @param scopeType the limited scope type
     * @return subjects visible to the current caller
     */
    Set<DataPermissionSubject> resolveSubjects(DataScopeType scopeType);
}
