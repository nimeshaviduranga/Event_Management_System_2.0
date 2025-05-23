package com.event.manage.model.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Composite primary key for the Attendance entity.
 *
 * Represents the combination of event ID and user ID that uniquely
 * identifies an attendance record.
 *
 * @author Event Management Team
 * @version 1.0
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceId implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Foreign key to the event
     */
    @Column(name = "event_id")
    private UUID eventId;

    /**
     * Foreign key to the user
     */
    @Column(name = "user_id")
    private UUID userId;

    /**
     * Custom equals method for proper comparison
     *
     * @param o Object to compare with
     * @return true if objects are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttendanceId that = (AttendanceId) o;
        return Objects.equals(eventId, that.eventId) &&
                Objects.equals(userId, that.userId);
    }

    /**
     * Custom hashCode method for proper hashing
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(eventId, userId);
    }

    /**
     * String representation of the composite key
     *
     * @return string representation
     */
    @Override
    public String toString() {
        return String.format("AttendanceId{eventId=%s, userId=%s}", eventId, userId);
    }
}