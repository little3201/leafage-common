package top.leafage.common.data.permission;

import java.util.Set;

/**
 * The configured data permission for one functional privilege.
 *
 * @param privilegeId the business privilege identifier
 * @param actions     actions covered by this policy
 * @param dataScope   scope configuration including action-specific overrides
 */
public record DataPermissionPolicy(Long privilegeId, Set<String> actions,
                                   DataScopeConfiguration dataScope) {

    /**
     * Creates an immutable policy.
     */
    public DataPermissionPolicy {
        actions = Set.copyOf(actions);
    }

    /**
     * Checks whether the policy applies to an action.
     *
     * @param action the requested action
     * @return {@code true} when the action is configured
     */
    public boolean appliesTo(String action) {
        return actions.contains(action);
    }
}
