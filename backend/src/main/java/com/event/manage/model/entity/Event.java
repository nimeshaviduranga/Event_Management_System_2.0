package com.event.manage.model.entity;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * Event entity representing an event in the system.
 *
 * Events are created by users (hosts) and can be attended by other users.
 * Each event has a specific time, location, and visibility setting.
 *
 * @author Event Management Team
 * @version 1.0
 */
@Entity
@Table(name = "events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"host", "attendances"})
@ToString(exclude = {"host", "attendances"})
public class Event {

    /**
     * Unique identifier for the event
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     * Event title
     */
    @Column(nullable = false, length = 100)
    private String title;

    /**
     * Detailed description of the event
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    /**
     * User who is hosting this event
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    private User host;

    /**
     * Event start time
     */
    @Column(name = "start_time", nullable = false)
    private ZonedDateTime startTime;

    /**
     * Event end time
     */
    @Column(name = "end_time", nullable = false)
    private ZonedDateTime endTime;

    /**
     * Event location
     */
    @Column(nullable = false, length = 100)
    private String location;

    /**
     * Event visibility (PUBLIC or PRIVATE)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Visibility visibility = Visibility.PUBLIC;

    /**
     * Soft delete flag
     */
    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private boolean deleted = false;

    /**
     * Timestamp when the event was created
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    /**
     * Timestamp when the event was last updated
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;

    /**
     * Users attending this event
     */
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Attendance> attendances = new HashSet<>();

    /**
     * Enumeration of event visibility options
     */
    public enum Visibility {
        /**
         * Event is visible to all users
         */
        PUBLIC,

        /**
         * Event is only visible to invited users
         */
        PRIVATE
    }

    /**
     * Add an attendance record to this event
     *
     * @param attendance The attendance record to add
     */
    public void addAttendance(Attendance attendance) {
        attendances.add(attendance);
        attendance.setEvent(this);
    }

    /**
     * Remove an attendance record from this event
     *
     * @param attendance The attendance record to remove
     */
    public void removeAttendance(Attendance attendance) {
        attendances.remove(attendance);
        attendance.setEvent(null);
    }

    /**
     * Check if the event is in the future
     *
     * @return true if event start time is after now
     */
    public boolean isFuture() {
        return startTime.isAfter(ZonedDateTime.now());
    }

    /**
     * Check if the event is currently happening
     *
     * @return true if current time is between start and end time
     */
    public boolean isOngoing() {
        ZonedDateTime now = ZonedDateTime.now();
        return now.isAfter(startTime) && now.isBefore(endTime);
    }

    /**
     * Check if the event has ended
     *
     * @return true if event end time is before now
     */
    public boolean isPast() {
        return endTime.isBefore(ZonedDateTime.now());
    }

    /**
     * Get the number of attendees
     *
     * @return count of attendance records
     */
    public int getAttendeeCount() {
        return attendances.size();
    }

    /**
     * Check if a user is hosting this event
     *
     * @param userId User ID to check
     * @return true if the user is the host
     */
    public boolean isHostedBy(UUID userId) {
        return host != null && host.getId().equals(userId);
    }

    /**
     * Soft delete this event
     */
    public void softDelete() {
        this.deleted = true;
    }

    /**
     * Restore this soft-deleted event
     */
    public void restore() {
        this.deleted = false;
    }
}