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
 * User entity representing a user in the event management system.
 *
 * Each user can:
 * - Host multiple events
 * - Attend multiple events with different statuses
 * - Have either USER or ADMIN role
 *
 * @author Event Management Team
 * @version 1.0
 */
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"hostedEvents", "attendances"})
@ToString(exclude = {"hostedEvents", "attendances"})
public class User {

    /**
     * Unique identifier for the user
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     * User's full name
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * User's email address (unique identifier for login)
     */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /**
     * User's encrypted password
     */
    @Column(nullable = false, length = 100)
    private String password;

    /**
     * User's role in the system
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.USER;

    /**
     * Whether the user account is active
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;

    /**
     * Timestamp when the user was created
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    /**
     * Timestamp when the user was last updated
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;

    /**
     * Events hosted by this user
     */
    @OneToMany(mappedBy = "host", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Event> hostedEvents = new HashSet<>();

    /**
     * Events this user is attending
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Attendance> attendances = new HashSet<>();

    /**
     * Enumeration of user roles in the system
     */
    public enum Role {
        /**
         * Regular user who can attend and host events
         */
        USER,

        /**
         * Administrator with elevated privileges
         */
        ADMIN
    }

    /**
     * Add a hosted event to this user
     *
     * @param event The event to add
     */
    public void addHostedEvent(Event event) {
        hostedEvents.add(event);
        event.setHost(this);
    }

    /**
     * Remove a hosted event from this user
     *
     * @param event The event to remove
     */
    public void removeHostedEvent(Event event) {
        hostedEvents.remove(event);
        event.setHost(null);
    }

    /**
     * Add an attendance record to this user
     *
     * @param attendance The attendance to add
     */
    public void addAttendance(Attendance attendance) {
        attendances.add(attendance);
        attendance.setUser(this);
    }

    /**
     * Remove an attendance record from this user
     *
     * @param attendance The attendance to remove
     */
    public void removeAttendance(Attendance attendance) {
        attendances.remove(attendance);
        attendance.setUser(null);
    }

    /**
     * Check if this user is an admin
     *
     * @return true if user has ADMIN role
     */
    public boolean isAdmin() {
        return Role.ADMIN.equals(this.role);
    }
}