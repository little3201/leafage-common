package top.leafage.common.data.permission.jpa;

import jakarta.persistence.*;

/**
 * Associates a persisted resource instance with a data-permission subject.
 */
@Entity
@Table(name = "resource_subjects")
public class ResourceSubject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String resource;
    private Long resourceId;
    private String subjectType;
    private String subjectId;

    /**
     * Creates an empty subject mapping for JPA.
     */
    public ResourceSubject() {
    }

    /**
     * Creates a resource-subject mapping.
     *
     * @param resource    resource identifier
     * @param resourceId  resource primary key
     * @param subjectType subject type
     * @param subjectId   subject identifier
     */
    public ResourceSubject(String resource, Long resourceId, String subjectType, String subjectId) {
        this.resource = resource;
        this.resourceId = resourceId;
        this.subjectType = subjectType;
        this.subjectId = subjectId;
    }

    public Long getId() {
        return id;
    }

    public String getResource() {
        return resource;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public String getSubjectType() {
        return subjectType;
    }

    public String getSubjectId() {
        return subjectId;
    }
}
