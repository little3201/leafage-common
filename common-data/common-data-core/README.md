# common-data-core

通用数据层接口（`CrudService`、`PagingAndSortService`、树节点转换）以及数据范围（data scope）核心实现。

## 数据范围（data scope）

### 设计要点

- **显式开启**：默认不生效，需配置 `data.scope.enabled=true`。
- **显式声明才过滤**：服务类或方法标注 `@DataScope` 后才启用过滤。
- **action = 方法名**：默认取被拦截的服务方法名（`retrieve`、`fetch`、`create`、`modify`、`remove`），
  可用 `@DataScope(action = "...")` 覆盖。
- **resource 默认 = 被查询实体**：由持久化适配器解析（JPA 默认取 `@Table(name)`）。若只想过滤主实体，
  用 `@DataScope("reports")` 限定资源，其他实体的查询不受影响。
- **无策略即拒绝**：标注了 `@DataScope` 但 `grantsFor` 返回空列表时，查询返回空、写校验拒绝。
- **单线程作用域缓存**：同一请求内相同 `(resource, action)` 的决策只计算一次。

### 核心 SPI

```java
public interface DataScopePolicyProvider {
    List<DataScopeGrant> grantsFor(String resource, String action);
}

public interface DataSubjectResolver {
    Set<DataSubject> resolve(DataScopeType scopeType);   // SELF / GROUP / GROUP_TREE
}
```

`grantsFor` 返回空列表表示拒绝；只要有一个 `ALL` 授权就整体放行；其余授权的主体集合取并集。
`CUSTOM` 使用授权里配置的主体，其他受限范围交给 `DataSubjectResolver` 解析。

启用 `leafage.data-scope.enabled=true` 后，应用必须提供这两个 Bean；缺失会在启动阶段直接失败，
避免权限静默失效。

### 决策规则

| 情形                        | 结果                   |
|-----------------------------|------------------------|
| 未标注 `@DataScope`         | 不过滤                 |
| 标注了，但没有授权          | `Denied`               |
| 任一授权为 `ALL`            | `Unrestricted`         |
| 有受限授权且主体非空        | `Restricted(主体并集)` |
| 只有 `NONE` / 主体为空      | `Denied`               |

### JPA 接入

`common-data-jpa` 提供 `top.leafage.common.data.jpa.scope` 适配层。让应用的 Repository 使用权限基类：

```java
@EnableJpaRepositories(
        repositoryBaseClass = DataScopeJpaRepository.class,
        repositoryFactoryBeanClass = DataScopeJpaRepositoryFactoryBean.class)
```

库不提供实体，应用自己定义映射实体（例如 `resource_subjects` 表），然后声明字段映射：

```java

@Bean
ResourceSubjectMapping resourceSubjectMapping() {
    return ResourceSubjectMapping.of(ResourceSubject.class);
}
```

如字段名不同，可用完整构造器指定（属性名是 **实体属性名**，不是数据库列名）；也可以直接实现
`DataScopePredicateResolver` 来完全接管查询条件生成。

服务侧只需标注注解，查询代码保持原样：

```java

@DataScope("reports")
@Service
public class ReportServiceImpl implements ReportService {

    public Page<ReportVO> retrieve(...) {
        return reportRepository.findAll(spec, pageable).map(ReportVO::from);
    }

    @DataScopeCheck("reports")
    public void remove(Long id) {
        reportRepository.deleteById(id);
    }
}
```

`@DataScopeCheck` 在方法执行前校验单条资源，默认取第一个参数作为资源 id，可用 `parameterIndex`
指定其他参数；action 默认取方法名。也可以直接调用 `dataScopeGuard.check(resource, action, id)`。

维护资源归属时使用显式主体：

```java
resourceSubjectService.bind(report, new DataSubject("USER", userId));
```

`bind/unbind` 要求调用方已开启事务（`Propagation.MANDATORY`），这样归属关系不会脱离业务事务提交。

`common-data-jpa` 已把 `aspectjweaver` 作为必需依赖；Boot 应用会自动启用 AOP 代理，非 Boot 应用需
自行开启 `@EnableAspectJAutoProxy`。同类内部方法自调用不经过代理；外层已标注方法的上下文会继续生效。

### 覆盖范围

JPA 基类会为以下方法自动叠加权限条件：

`findAll()`、`findAll(Sort)`、`findAll(Pageable)`、`findAll(Specification[, Sort|Pageable])`、
`findOne(Specification)`、`count()`、`count(Specification)`、`exists(Specification)`、
`findById`、`existsById`、`findAllById`、`deleteById`、`deleteAll`、`deleteAllInBatch()`、
`deleteAllByIdInBatch`。

已知盲区：

- derived query 方法（如 `findByTitle`）和 `@Query` 不经过 `getQuery`；
- `getReferenceById` / `getById` / `getOne` 不加载数据，无法过滤；
- `save`、`delete(entity)` 等直接写入不经过读过滤，需配合 `@DataScopeCheck` 或 Guard；
- `@Modifying` 的批量 update/delete 不经过 Repository 基类；
- fluent `findBy(...)` 与 `findAll(Example)` 不经过 `getQuery`；
- 复合主键暂不支持（启用过滤时会明确抛错）。
