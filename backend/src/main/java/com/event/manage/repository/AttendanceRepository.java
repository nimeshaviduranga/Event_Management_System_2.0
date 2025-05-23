package com.event.manage.repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.event.manage.model.entity.Attendance;
import com.event.manage.model.entity.AttendanceId;
import com.event.manage.model.entity.Event;
import com.event.manage.model.entity.User;

/**
 * Repository interface for Attendance entity operations.
 *
 * Provides data access methods for managing event attendance,
 * including status tracking and analytics.
 *
 * @author Event Management Team
 * @version 1.0
 */
@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, AttendanceId> {

    /**
     * Find an attendance record by event and user.
     *
     * @param event The event
     * @param user The user
     * @return Optional containing the attendance record if found, empty otherwise
     */
    Optional<Attendance> findByEventAndUser(Event event, User user);

    /**
     * Find an attendance record by event ID and user ID.
     *
     * @param eventId The event ID
     * @param userId The user ID
     * @return Optional containing the attendance record if found, empty otherwise
     */
    Optional<Attendance> findByEvent_IdAndUser_Id(UUID eventId, UUID userId);

    /**
     * Find all attendance records for a specific event.
     *
     * @param event The event
     * @return List of attendance records for the event
     */
    List<Attendance> findAllByEvent(Event event);

    /**
     * Find all attendance records for a specific event with pagination.
     *
     * @param event The event
     * @param pageable Pagination information
     * @return Page of attendance records for the event
     */
    Page<Attendance> findAllByEvent(Event event, Pageable pageable);

    /**
     * Find all attendance records for a specific event ID.
     *
     * @param eventId The event ID
     * @return List of attendance records for the event
     */
    List<Attendance> findAllByEvent_Id(UUID eventId);

    /**
     * Find all attendance records for a specific user.
     *
     * @param user The user
     * @return List of attendance records for the user
     */
    List<Attendance> findAllByUser(User user);

    /**
     * Find all attendance records for a specific user with pagination.
     *
     * @param user The user
     * @param pageable Pagination information
     * @return Page of attendance records for the user
     */
    Page<Attendance> findAllByUser(User user, Pageable pageable);

    /**
     * Find all attendance records for a specific user ID.
     *
     * @param userId The user ID
     * @return List of attendance records for the user
     */
    List<Attendance> findAllByUser_Id(UUID userId);

    /**
     * Find all attendance records for a specific event with a specific status.
     *
     * @param event The event
     * @param status The attendance status
     * @return List of attendance records matching the criteria
     */
    List<Attendance> findAllByEventAndStatus(Event event, Attendance.Status status);

    /**
     * Find all attendance records for a specific event ID with a specific status.
     *
     * @param eventId The event ID
     * @param status The attendance status
     * @return List of attendance records matching the criteria
     */
    List<Attendance> findAllByEvent_IdAndStatus(UUID eventId, Attendance.Status status);

    /**
     * Find all attendance records for a specific user with a specific status.
     *
     * @param user The user
     * @param status The attendance status
     * @return List of attendance records matching the criteria
     */
    List<Attendance> findAllByUserAndStatus(User user, Attendance.Status status);

    /**
     * Check if a user is attending an event.
     *
     * @param eventId The event ID
     * @param userId The user ID
     * @return true if the user is attending the event, false otherwise
     */
    boolean existsByEvent_IdAndUser_Id(UUID eventId, UUID userId);

    /**
     * Count total attendees for an event (all statuses).
     *
     * @param eventId The event ID
     * @return Total number of attendees
     */
    long countByEvent_Id(UUID eventId);

    /**
     * Count attendees for an event with a specific status.
     *
     * @param eventId The event ID
     * @param status The attendance status
     * @return Number of attendees with the specified status
     */
    long countByEvent_IdAndStatus(UUID eventId, Attendance.Status status);

    /**
     * Count total events a user is attending (all statuses).
     *
     * @param userId The user ID
     * @return Total number of events the user is attending
     */
    long countByUser_Id(UUID userId);

    /**
     * Count events a user is attending with a specific status.
     *
     * @param userId The user ID
     * @param status The attendance status
     * @return Number of events the user is attending with the specified status
     */
    long countByUser_IdAndStatus(UUID userId, Attendance.Status status);

    /**
     * Find attendance records for upcoming events.
     *
     * @param userId The user ID
     * @param now The current time
     * @return List of attendance records for upcoming events
     */
    @Query("SELECT a FROM Attendance a WHERE a.user.id = :userId AND a.event.startTime > :now AND a.event.deleted = false")
    List<Attendance> findUpcomingAttendanceByUser(@Param("userId") UUID userId, @Param("now") ZonedDateTime now);

    /**
     * Find attendance records for past events.
     *
     * @param userId The user ID
     * @param now The current time
     * @return List of attendance records for past events
     */
    @Query("SELECT a FROM Attendance a WHERE a.user.id = :userId AND a.event.endTime < :now AND a.event.deleted = false")
    List<Attendance> findPastAttendanceByUser(@Param("userId") UUID userId, @Param("now") ZonedDateTime now);

    /**
     * Find the most active attendees across all events.
     * Users with the most event attendances.
     *
     * @param pageable Pagination information
     * @return Page of attendance records grouped by user, ordered by count
     */
    @Query("SELECT a FROM Attendance a GROUP BY a.user ORDER BY COUNT(a) DESC")
    Page<Attendance> findMostActiveAttendees(Pageable pageable);

    /**
     * Find attendance records created after a specific date.
     * Useful for analytics and recent activity tracking.
     *
     * @param date The date to filter from
     * @return List of attendance records created after the specified date
     */
    @Query("SELECT a FROM Attendance a WHERE a.respondedAt > :date ORDER BY a.respondedAt DESC")
    List<Attendance> findAttendanceCreatedAfter(@Param("date") ZonedDateTime date);

    /**
     * Find all attendance records for events in a specific location.
     *
     * @param location The location to search for
     * @return List of attendance records for events at the specified location
     */
    @Query("SELECT a FROM Attendance a WHERE LOWER(a.event.location) LIKE LOWER(CONCAT('%', :location, '%')) AND a.event.deleted = false")
    List<Attendance> findAttendanceByEventLocation(@Param("location") String location);

    /**
     * Find users attending multiple events (popular attendees).
     *
     * @param minEventCount Minimum number of events attended
     * @return List of users attending at least the specified number of events
     */
    @Query("SELECT a.user FROM Attendance a GROUP BY a.user HAVING COUNT(a) >= :minEventCount")
    List<User> findUsersAttendingMultipleEvents(@Param("minEventCount") long minEventCount);

    /**
     * Get attendance statistics for a specific event.
     * Returns counts for each status type.
     *
     * @param eventId The event ID
     * @return List of objects containing status and count
     */
    @Query("SELECT a.status, COUNT(a) FROM Attendance a WHERE a.event.id = :eventId GROUP BY a.status")
    List<Object[]> getAttendanceStatsByEvent(@Param("eventId") UUID eventId);

    /**
     * Find events with low attendance (below threshold).
     *
     * @param maxAttendees Maximum number of attendees to be considered low attendance
     * @return List of events with attendance below the threshold
     */
    @Query("SELECT a.event FROM Attendance a WHERE a.event.deleted = false GROUP BY a.event HAVING COUNT(a) <= :maxAttendees")
    List<Event> findEventsWithLowAttendance(@Param("maxAttendees") long maxAttendees);

    /**
     * Find events with high attendance (above threshold).
     *
     * @param minAttendees Minimum number of attendees to be considered high attendance
     * @return List of events with attendance above the threshold
     */
    @Query("SELECT a.event FROM Attendance a WHERE a.event.deleted = false GROUP BY a.event HAVING COUNT(a) >= :minAttendees")
    List<Event> findEventsWithHighAttendance(@Param("minAttendees") long minAttendees);

    /**
     * Delete all attendance records for a specific event.
     * Used when an event is permanently deleted.
     *
     * @param eventId The event ID
     */
    void deleteByEvent_Id(UUID eventId);

    /**
     * Delete all attendance records for a specific user.
     * Used when a user account is deleted.
     *
     * @param userId The user ID
     */
    void deleteByUser_Id(UUID userId);
}