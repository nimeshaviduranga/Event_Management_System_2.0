package com.event.manage.repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.event.manage.model.entity.Event;
import com.event.manage.model.entity.User;

/**
 * Repository interface for Event entity operations.
 *
 * Provides comprehensive data access methods for event management
 * including filtering, searching, and analytics capabilities.
 *
 * @author Event Management Team
 * @version 1.0
 */
@Repository
public interface EventRepository extends JpaRepository<Event, UUID>, JpaSpecificationExecutor<Event> {

    /**
     * Find an event by ID that is not deleted.
     * Used to ensure soft-deleted events are not accessible.
     *
     * @param id The event ID
     * @return Optional containing the event if found and not deleted, empty otherwise
     */
    Optional<Event> findByIdAndDeletedFalse(UUID id);

    /**
     * Find all non-deleted events.
     *
     * @param pageable Pagination information
     * @return Page of non-deleted events
     */
    Page<Event> findAllByDeletedFalse(Pageable pageable);

    /**
     * Find all events hosted by a specific user that are not deleted.
     *
     * @param host The host user
     * @param pageable Pagination information
     * @return Page of events hosted by the user
     */
    Page<Event> findAllByHostAndDeletedFalse(User host, Pageable pageable);

    /**
     * Find all events hosted by a specific user ID that are not deleted.
     *
     * @param hostId The host user ID
     * @param pageable Pagination information
     * @return Page of events hosted by the user
     */
    Page<Event> findAllByHost_IdAndDeletedFalse(UUID hostId, Pageable pageable);

    /**
     * Find all public events with start time after now that are not deleted.
     * Used for displaying upcoming public events.
     *
     * @param now The current time
     * @param pageable Pagination information
     * @return Page of upcoming public events
     */
    Page<Event> findAllByVisibilityAndStartTimeAfterAndDeletedFalse(
            Event.Visibility visibility, ZonedDateTime now, Pageable pageable);

    /**
     * Find all events with start time after now that are not deleted.
     *
     * @param now The current time
     * @param pageable Pagination information
     * @return Page of upcoming events
     */
    Page<Event> findAllByStartTimeAfterAndDeletedFalse(ZonedDateTime now, Pageable pageable);

    /**
     * Find all events with start time before now that are not deleted.
     * Used for showing past events.
     *
     * @param now The current time
     * @param pageable Pagination information
     * @return Page of past events
     */
    Page<Event> findAllByStartTimeBeforeAndDeletedFalse(ZonedDateTime now, Pageable pageable);

    /**
     * Find events by location (case insensitive) that are not deleted.
     *
     * @param location The location to search for
     * @param pageable Pagination information
     * @return Page of events at the specified location
     */
    Page<Event> findAllByLocationContainingIgnoreCaseAndDeletedFalse(String location, Pageable pageable);

    /**
     * Find events by title containing search term (case insensitive) that are not deleted.
     *
     * @param title The title to search for
     * @param pageable Pagination information
     * @return Page of events with matching titles
     */
    Page<Event> findAllByTitleContainingIgnoreCaseAndDeletedFalse(String title, Pageable pageable);

    /**
     * Find events between specific start and end times that are not deleted.
     *
     * @param startTime The earliest start time
     * @param endTime The latest end time
     * @param pageable Pagination information
     * @return Page of events within the time range
     */
    Page<Event> findAllByStartTimeGreaterThanEqualAndEndTimeLessThanEqualAndDeletedFalse(
            ZonedDateTime startTime, ZonedDateTime endTime, Pageable pageable);

    /**
     * Find events by visibility that are not deleted.
     *
     * @param visibility The event visibility
     * @param pageable Pagination information
     * @return Page of events with the specified visibility
     */
    Page<Event> findAllByVisibilityAndDeletedFalse(Event.Visibility visibility, Pageable pageable);

    /**
     * Find all events that a user is attending (via attendance join table).
     *
     * @param userId The user ID
     * @return List of events the user is attending
     */
    @Query("SELECT e FROM Event e JOIN e.attendances a WHERE a.user.id = :userId AND e.deleted = false")
    List<Event> findAllEventsByAttendeeId(@Param("userId") UUID userId);

    /**
     * Find events that a user is attending with pagination.
     *
     * @param userId The user ID
     * @param pageable Pagination information
     * @return Page of events the user is attending
     */
    @Query("SELECT e FROM Event e JOIN e.attendances a WHERE a.user.id = :userId AND e.deleted = false")
    Page<Event> findEventsByAttendeeId(@Param("userId") UUID userId, Pageable pageable);

    /**
     * Find events that a user is attending with a specific status.
     *
     * @param userId The user ID
     * @param status The attendance status
     * @return List of events the user is attending with the specified status
     */
    @Query("SELECT e FROM Event e JOIN e.attendances a WHERE a.user.id = :userId AND a.status = :status AND e.deleted = false")
    List<Event> findEventsByAttendeeIdAndStatus(@Param("userId") UUID userId,
                                                @Param("status") com.event.manage.model.entity.Attendance.Status status);

    /**
     * Search events by title, description, or location.
     * Comprehensive search functionality.
     *
     * @param searchTerm The term to search for
     * @param pageable Pagination information
     * @return Page of events matching the search criteria
     */
    @Query("SELECT e FROM Event e WHERE e.deleted = false AND " +
            "(LOWER(e.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(e.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(e.location) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Event> searchEvents(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Find events with the most attendees.
     * Used for analytics and trending events.
     *
     * @param pageable Pagination information
     * @return Page of events ordered by attendance count
     */
    @Query("SELECT e FROM Event e LEFT JOIN e.attendances a WHERE e.deleted = false " +
            "GROUP BY e ORDER BY COUNT(a) DESC")
    Page<Event> findMostPopularEvents(Pageable pageable);

    /**
     * Find events created after a specific date.
     * Useful for analytics and recent events.
     *
     * @param date The date to filter from
     * @param pageable Pagination information
     * @return Page of events created after the specified date
     */
    @Query("SELECT e FROM Event e WHERE e.createdAt > :date AND e.deleted = false ORDER BY e.createdAt DESC")
    Page<Event> findEventsCreatedAfter(@Param("date") ZonedDateTime date, Pageable pageable);

    /**
     * Count total non-deleted events.
     *
     * @return Number of active events
     */
    long countByDeletedFalse();

    /**
     * Count events by visibility.
     *
     * @param visibility The visibility to count
     * @return Number of events with the specified visibility
     */
    long countByVisibilityAndDeletedFalse(Event.Visibility visibility);

    /**
     * Count events hosted by a specific user.
     *
     * @param hostId The host user ID
     * @return Number of events hosted by the user
     */
    long countByHost_IdAndDeletedFalse(UUID hostId);

    /**
     * Count upcoming events (start time after now).
     *
     * @param now The current time
     * @return Number of upcoming events
     */
    long countByStartTimeAfterAndDeletedFalse(ZonedDateTime now);

    /**
     * Find events happening today.
     * Events that start or are ongoing today.
     *
     * @param startOfDay Start of today
     * @param endOfDay End of today
     * @return List of events happening today
     */
    @Query("SELECT e FROM Event e WHERE e.deleted = false AND " +
            "((e.startTime >= :startOfDay AND e.startTime <= :endOfDay) OR " +
            "(e.startTime <= :startOfDay AND e.endTime >= :startOfDay))")
    List<Event> findEventsHappeningToday(@Param("startOfDay") ZonedDateTime startOfDay,
                                         @Param("endOfDay") ZonedDateTime endOfDay);

    /**
     * Find conflicting events for a user.
     * Events that overlap with the given time range for events the user is attending or hosting.
     *
     * @param userId The user ID
     * @param startTime The start time to check
     * @param endTime The end time to check
     * @return List of conflicting events
     */
    @Query("SELECT e FROM Event e WHERE e.deleted = false AND " +
            "(e.host.id = :userId OR e.id IN (SELECT a.event.id FROM Attendance a WHERE a.user.id = :userId)) AND " +
            "((e.startTime <= :endTime AND e.endTime >= :startTime))")
    List<Event> findConflictingEventsForUser(@Param("userId") UUID userId,
                                             @Param("startTime") ZonedDateTime startTime,
                                             @Param("endTime") ZonedDateTime endTime);
}