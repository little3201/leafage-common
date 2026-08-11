package top.leafage.common.data.permission;

import jakarta.persistence.Embeddable;

/**
 * A configured subject for a custom data scope.
 *
 * @param type the application-defined subject type code
 * @param id   the application-defined subject identifier
 */
@Embeddable
public record DataPermissionSubject(String type, String id) {

    /**
     * Validates a configured subject.
     */
    public DataPermissionSubject {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("type must not be blank");
        }
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be blank");
        }
    }
}
