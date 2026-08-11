package top.leafage.common.data.permission.jpa;

/**
 * Maintains generic resource-subject mappings for JPA entities.
 */
public interface ResourceSubjectService {

    /**
     * Binds a persisted entity as its own data-permission subject.
     *
     * @param entity persisted entity
     */
    void bind(Object entity);

    /**
     * Binds a persisted resource entity to a persisted subject entity.
     *
     * @param resource resource entity
     * @param subject subject entity
     */
    void bind(Object resource, Object subject);
}
