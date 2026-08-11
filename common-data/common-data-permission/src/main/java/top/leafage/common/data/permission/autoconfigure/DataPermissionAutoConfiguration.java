package top.leafage.common.data.permission.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import top.leafage.common.data.permission.*;
import top.leafage.common.data.permission.jpa.JpaDataPermissionPredicateResolver;
import top.leafage.common.data.permission.jpa.DataPermission;
import top.leafage.common.data.permission.jpa.DataPermissionStore;
import top.leafage.common.data.permission.jpa.JpaDataPermissionStore;
import top.leafage.common.data.permission.jpa.ResourceSubject;
import top.leafage.common.data.permission.jpa.JpaResourceSubjectService;
import top.leafage.common.data.permission.jpa.ResourceSubjectService;

/**
 * Spring Boot auto-configuration for data permission specifications.
 */
@AutoConfiguration
@AutoConfigureBefore(name = "org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration")
@AutoConfigurationPackage(basePackageClasses = {DataPermission.class, ResourceSubject.class})
@ConditionalOnBean({DataPermissionPolicyProvider.class, DataScopeSubjectResolver.class})
public class DataPermissionAutoConfiguration {

    /**
     * Provides the generic JPA subject predicate resolver unless the application overrides it.
     *
     * @return generic resolver
     */
    @Bean
    @ConditionalOnMissingBean
    public DataPermissionPredicateResolver dataPermissionPredicateResolver() {
        return new JpaDataPermissionPredicateResolver();
    }

    /**
     * Provides generic resource-subject mapping operations based on JPA entity metadata.
     *
     * @param entityManager transaction-aware entity manager
     * @return resource-subject service
     */
    @Bean
    @ConditionalOnMissingBean
    public ResourceSubjectService resourceSubjectService(jakarta.persistence.EntityManager entityManager) {
        return new JpaResourceSubjectService(entityManager);
    }

    /**
     * Provides generic JPA access to data permission policies.
     *
     * @param entityManager transaction-aware entity manager
     * @return policy store
     */
    @Bean
    @ConditionalOnMissingBean
    public DataPermissionStore dataPermissionStore(jakarta.persistence.EntityManager entityManager) {
        return new JpaDataPermissionStore(entityManager);
    }

    @Bean
    @ConditionalOnMissingBean
    public DataPermissionSpecification dataPermissionSpecification(DataPermissionPolicyProvider policyProvider,
                                                                   DataScopeSubjectResolver subjectResolver,
                                                                   DataPermissionPredicateResolver predicateResolver) {
        return new ConfiguredDataPermissionSpecification(policyProvider, subjectResolver, predicateResolver);
    }
}
