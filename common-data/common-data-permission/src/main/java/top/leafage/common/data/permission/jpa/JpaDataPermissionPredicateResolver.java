package top.leafage.common.data.permission.jpa;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import top.leafage.common.data.permission.DataPermissionPredicateResolver;
import top.leafage.common.data.permission.DataPermissionSubject;
import top.leafage.common.data.permission.enums.DataScopeType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Resolves data permission subjects through the generic resource-subject mapping table.
 */
public class JpaDataPermissionPredicateResolver implements DataPermissionPredicateResolver {

    @Override
    public <T> Specification<T> resolve(String resource, DataScopeType scopeType,
                                        Set<DataPermissionSubject> subjects) {
        return (root, query, criteriaBuilder) -> {
            if (subjects.isEmpty()) {
                return criteriaBuilder.disjunction();
            }
            Subquery<Long> ownership = query.subquery(Long.class);
            var subject = ownership.from(ResourceSubject.class);
            List<Predicate> subjectPredicates = new ArrayList<>();
            for (DataPermissionSubject candidate : subjects) {
                subjectPredicates.add(criteriaBuilder.and(
                        criteriaBuilder.equal(subject.get("subjectType"), candidate.type()),
                        criteriaBuilder.equal(subject.get("subjectId"), candidate.id())));
            }
            ownership.select(subject.get("resourceId"))
                    .where(criteriaBuilder.equal(subject.get("resource"), resource),
                            criteriaBuilder.equal(subject.get("resourceId"), root.get("id")),
                            criteriaBuilder.or(subjectPredicates.toArray(Predicate[]::new)));
            return criteriaBuilder.exists(ownership);
        };
    }
}
