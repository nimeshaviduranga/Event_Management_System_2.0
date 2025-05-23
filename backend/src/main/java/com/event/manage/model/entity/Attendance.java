package com.event.manage.model.entity;

import java.time.ZonedDateTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Attendance entity representing a user's attendance at an event.
 *
 * This is a join table between User and Event with additional attributes
 * like attendance status and response timestamp.
 *
 * @author Event Management Team
 * @version 1.0
 */
@Entity
@Table(name = "attendance")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"event", "user"})
@ToString(exclude = {"event", "user"})
public class Attendance {

    /**
     * Composite primary key
     */
    @EmbeddedId
    private AttendanceId id;

    /**
     * Event being attended
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("eventId")
    @JoinColumn(name = "event_id")
    private Event event;

    /**
     * User attending the event
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Attendance status
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.GOING;

    /**
     * Timestamp when the user responded
     */
    @Column(name = "responded_at", nullable = false)
    @Builder.Default
    private ZonedDateTime respondedAt = ZonedDateTime.now();

    /**
     * Enumeration of attendance status options
     */
    public enum Status {
        /**
         * User confirmed they will attend
         */
        GOING,

        /**
         * User might attend (uncertain)
         */
        MAYBE,

        /**
         * User declined to attend
         */
        DECLINED
    }

    /**
     * Factory method to create a new attendance record
     *
     * @param event The event
     * @param user The user
     * @param status The attendance status
     * @return A new attendance record
     */
    public static Attendance of(Event event, User user, Status status) {
        AttendanceId id = new AttendanceId(event.getId(), user.getId());
        return Attendance.builder()
                .id(id)
                .event(event)
                .user(user)
                .status(status)
                .respondedAt(ZonedDateTime.now())
                .build();
    }

    /**
     * Factory method to create a GOING attendance record
     *
     * @param event The event
     * @param user The user
     * @return A new attendance record with GOING status
     */
    public static Attendance going(Event event, User user) {
        return of(event, user, Status.GOING);
    }

    /**
     * Factory method to create a MAYBE attendance record
     *
     * @param event The event
     * @param user The user
     * @return A new attendance record with MAYBE status
     */
    public static Attendance maybe(Event event, User user) {
        return of(event, user, Status.MAYBE);
    }

    /**
     * Factory method to create a DECLINED attendance record
     *
     * @param event The event
     * @param user The user
     * @return A new attendance record with DECLINED status
     */
    public static Attendance declined(Event event, User user) {
        return of(event, user, Status.DECLINED);
    }

    /**
     * Update the response status and timestamp
     *
     * @param newStatus The new status
     */
    public void updateStatus(Status newStatus) {
        this.status = newStatus;
        this.respondedAt = ZonedDateTime.now();
    }

    /**
     * Check if the attendance is positive (GOING or MAYBE)
     *
     * @return true if status is GOING or MAYBE
     */
    public boolean isPositive() {
        return status == Status.GOING || status == Status.MAYBE;
    }

    /**
     * Check if the attendance is confirmed (GOING)
     *
     * @return true if status is GOING
     */
    public boolean isConfirmed() {
        return status == Status.GOING;
    }
}
