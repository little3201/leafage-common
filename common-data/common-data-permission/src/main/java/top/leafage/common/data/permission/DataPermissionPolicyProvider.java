package top.leafage.common.data.permission;

import java.util.List;

/**
 * Loads the effective data-permission configuration for the current caller.
 */
public interface DataPermissionPolicyProvider {

    /**
     * Finds the policy resolved from the current operation and caller.
     *
     * @return all policies granted to the current caller for the operation
     */
    List<ResolvedDataPermissionPolicy> currentPolicies();
}
