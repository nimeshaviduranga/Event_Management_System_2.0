package com.event.manage.service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.event.manage.model.dto.EventDto;
import com.event.manage.model.dto.EventDto.EventCreateDto;
import com.event.manage.model.dto.EventDto.EventFilterDto;
import com.event.manage.model.dto.EventDto.EventStatsDto;
import com.event.manage.model.dto.EventDto.EventSummaryDto;
import com.event.manage.model.dto.EventDto.EventUpdateDto;
import com.event.manage.model.entity.Event;

/**
 * Service interface for event-related operations.
 */
public interface EventService {

    /**
     * Create a new event.
     *
     * @param hostId the host user ID
     * @param createDto the event creation data
     * @return the created event
     */
    EventDto createEvent(UUID hostId, EventCreateDto createDto);

    /**
     * Get event by ID.
     *
     * @param id the event ID
     * @param currentUserId the current user ID for checking attendance status
     * @return the event
     */
    EventDto getEventById(UUID id, UUID currentUserId);

    /**
     * Update event.
     *
     * @param id the event ID
     * @param updateDto the update data
     * @param currentUserId the current user ID for authorization
     * @return the updated event
     */
    EventDto updateEvent(UUID id, EventUpdateDto updateDto, UUID currentUserId);

    /**
     * Delete event (soft delete).
     *
     * @param id the event ID
     * @param currentUserId the current user ID for authorization
     */
    void deleteEvent(UUID id, UUID currentUserId);

    /**
     * Get all events with pagination.
     *
     * @param pageable the pagination information
     * @return page of events
     */
    Page<EventSummaryDto> getAllEvents(Pageable pageable);

    /**
     * Get events hosted by a user.
     *
     * @param hostId the host user ID
     * @param pageable the pagination information
     * @return page of events hosted by the user
     */
    Page<EventSummaryDto> getEventsByHost(UUID hostId, Pageable pageable);

    /**
     * Get events that a user is attending.
     *
     * @param userId the user ID
     * @param pageable the pagination information
     * @return page of events the user is attending
     */
    Page<EventSummaryDto> getEventsByAttendee(UUID userId, Pageable pageable);

    /**
     * Get upcoming events.
     *
     * @param pageable the pagination information
     * @return page of upcoming events
     */
    Page<EventSummaryDto> getUpcomingEvents(Pageable pageable);

    /**
     * Search events by title, description, or location.
     *
     * @param searchTerm the search term
     * @param pageable the pagination information
     * @return page of matching events
     */
    Page<EventSummaryDto> searchEvents(String searchTerm, Pageable pageable);

    /**
     * Filter events by various criteria.
     *
     * @param filterDto the filter criteria
     * @param pageable the pagination information
     * @return page of events matching the criteria
     */
    Page<EventSummaryDto> filterEvents(EventFilterDto filterDto, Pageable pageable);

    /**
     * Get event statistics.
     *
     * @param id the event ID
     * @return event statistics
     */
    EventStatsDto getEventStats(UUID id);

    /**
     * Get events happening today.
     *
     * @return list of events happening today
     */
    List<EventSummaryDto> getEventsHappeningToday();

    /**
     * Get most popular events.
     *
     * @param pageable the pagination information
     * @return page of most popular events
     */
    Page<EventSummaryDto> getMostPopularEvents(Pageable pageable);

    /**
     * Check for event time conflicts for a user.
     *
     * @param userId the user ID
     * @param startTime the start time
     * @param endTime the end time
     * @param excludeEventId event ID to exclude from conflict check (optional, for updates)
     * @return list of conflicting events
     */
    List<EventDto.EventConflictDto> checkEventConflicts(UUID userId, ZonedDateTime startTime,
                                                        ZonedDateTime endTime, UUID excludeEventId);

    /**
     * Get public events for visibility to non-authenticated users.
     *
     * @param pageable the pagination information
     * @return page of public events
     */
    Page<EventSummaryDto> getPublicEvents(Pageable pageable);

    /**
     * Restore a soft-deleted event.
     *
     * @param id the event ID
     * @param currentUserId the current user ID for authorization
     * @return the restored event
     */
    EventDto restoreEvent(UUID id, UUID currentUserId);
}