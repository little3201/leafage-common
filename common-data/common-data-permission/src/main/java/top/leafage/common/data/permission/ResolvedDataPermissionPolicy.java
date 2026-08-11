package top.leafage.common.data.permission;

import java.util.Objects;

/**
 * Data permission resolved from the current request and database configuration.
 *
 * @param resource the associated resource or table code loaded from the database
 * @param action   the current operation loaded from the database
 * @param policy   the current caller's effective data-permission policy
 */
public record ResolvedDataPermissionPolicy(String resource, String action,
                                           DataPermissionPolicy policy) {

    /**
     * Validates the database-resolved operation.
     */
    public ResolvedDataPermissionPolicy {
        if (resource == null || resource.isBlank()) {
            throw new IllegalArgumentException("resource must not be blank");
        }
        if (action == null || action.isBlank()) {
            throw new IllegalArgumentException("action must not be blank");
        }
        Objects.requireNonNull(policy, "policy must not be null");
    }
}
