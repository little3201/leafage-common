package top.leafage.common.data.permission.jpa;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import top.leafage.common.data.permission.DataPermissionSubject;
import top.leafage.common.data.permission.enums.DataScopeType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Database-backed data-scope policy assigned to a generic authorization subject.
 */
@Entity
@Table(name = "data_permissions")
public class DataPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String resource;

    @Column(name = "subject_type", nullable = false)
    private String subjectType;

    @Column(name = "subject_id", nullable = false)
    private String subjectId;

    @Enumerated(EnumType.STRING)
    @Column(name = "default_scope", nullable = false)
    private DataScopeType defaultScope = DataScopeType.NONE;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "data_permission_action_scopes", joinColumns = @JoinColumn(name = "data_permission_id"))
    @MapKeyColumn(name = "action")
    @Enumerated(EnumType.STRING)
    @Column(name = "scope_type", nullable = false)
    private final Map<String, DataScopeType> actionScopes = new HashMap<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "data_permission_subjects", joinColumns = @JoinColumn(name = "data_permission_id"))
    private final Set<DataPermissionSubject> customSubjects = new HashSet<>();

    public Long getId() { return id; }
    public String getResource() { return resource; }
    public void setResource(String resource) { this.resource = resource; }
    public String getSubjectType() { return subjectType; }
    public void setSubjectType(String subjectType) { this.subjectType = subjectType; }
    public String getSubjectId() { return subjectId; }
    public void setSubjectId(String subjectId) { this.subjectId = subjectId; }
    public DataScopeType getDefaultScope() { return defaultScope; }
    public void setDefaultScope(DataScopeType defaultScope) { this.defaultScope = defaultScope; }
    public Map<String, DataScopeType> getActionScopes() { return actionScopes; }
    public Set<DataPermissionSubject> getCustomSubjects() { return customSubjects; }
}
