package top.leafage.common.data.permission;

import top.leafage.common.data.permission.enums.DataScopeType;

import java.util.Map;
import java.util.Set;

/**
 * Scope configuration represented by the {@code dataScope} JSON object.
 *
 * @param defaultScope   scope used when an action has no override
 * @param actions        action-to-scope overrides
 * @param customSubjects configured subjects for {@link DataScopeType#CUSTOM}
 */
public record DataScopeConfiguration(DataScopeType defaultScope,
                                     Map<String, DataScopeType> actions,
                                     Set<DataPermissionSubject> customSubjects) {

    /**
     * Creates an immutable configuration.
     */
    public DataScopeConfiguration {
        actions = Map.copyOf(actions);
        customSubjects = Set.copyOf(customSubjects);
    }

    /**
     * Resolves the effective scope for an action.
     *
     * @param action the requested action
     * @return the action override when present, otherwise the default scope
     */
    public DataScopeType scopeFor(String action) {
        return actions.getOrDefault(action, defaultScope);
    }
}
