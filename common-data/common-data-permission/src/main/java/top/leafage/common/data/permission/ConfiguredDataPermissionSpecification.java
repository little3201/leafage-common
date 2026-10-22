package top.leafage.common.data.permission;

import org.springframework.data.jpa.domain.Specification;
import top.leafage.common.data.permission.enums.DataScopeType;

import java.util.Objects;
import java.util.ArrayList;
import java.util.List;

/**
 * Configuration-driven implementation of {@link DataPermissionSpecification}.
 */
public class ConfiguredDataPermissionSpecification implements DataPermissionSpecification {

    private final DataPermissionPolicyProvider policyProvider;
    private final DataScopeSubjectResolver subjectResolver;
    private final DataPermissionPredicateResolver predicateResolver;

    /**
     * Creates the specification service with application-provided adapters.
     */
    public ConfiguredDataPermissionSpecification(DataPermissionPolicyProvider policyProvider,
                                                 DataScopeSubjectResolver subjectResolver,
                                                 DataPermissionPredicateResolver predicateResolver) {
        this.policyProvider = Objects.requireNonNull(policyProvider, "policyProvider must not be null");
        this.subjectResolver = Objects.requireNonNull(subjectResolver, "subjectResolver must not be null");
        this.predicateResolver = Objects.requireNonNull(predicateResolver, "predicateResolver must not be null");
    }

    private static <T> Specification<T> unrestricted() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
    }

    private static <T> Specification<T> denied() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.disjunction();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> Specification<T> forEntity(Class<T> entityType) {
        var policies = policyProvider.currentPolicies().stream()
                .filter(candidate -> candidate.policy().appliesTo(candidate.action()))
                .toList();
        if (policies.isEmpty()) {
            return denied();
        }
        if (policies.stream().anyMatch(candidate ->
                candidate.policy().dataScope().scopeFor(candidate.action()) == DataScopeType.ALL)) {
            return unrestricted();
        }
        List<Specification<T>> specifications = new ArrayList<>();
        for (ResolvedDataPermissionPolicy candidate : policies) {
            DataScopeType scope = candidate.policy().dataScope().scopeFor(candidate.action());
            if (scope != DataScopeType.NONE) {
                specifications.add(scope == DataScopeType.CUSTOM
                        ? predicateResolver.resolve(entityType, candidate.resource(), scope,
                                candidate.policy().dataScope().customSubjects())
                        : predicateResolver.resolve(entityType, candidate.resource(), scope,
                                subjectResolver.resolveSubjects(scope)));
            }
        }
        return specifications.stream().reduce(Specification::or)
                .orElseGet(ConfiguredDataPermissionSpecification::denied);
    }
}
