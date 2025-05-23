package com.event.manage.service.impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.event.manage.exception.EventConflictException;
import com.event.manage.exception.ResourceNotFoundException;
import com.event.manage.exception.UnauthorizedException;
import com.event.manage.exception.ValidationException;
import com.event.manage.mapper.EventMapper;
import com.event.manage.model.dto.EventDto;
import com.event.manage.model.dto.EventDto.EventCreateDto;
import com.event.manage.model.dto.EventDto.EventFilterDto;
import com.event.manage.model.dto.EventDto.EventStatsDto;
import com.event.manage.model.dto.EventDto.EventSummaryDto;
import com.event.manage.model.dto.EventDto.EventUpdateDto;
import com.event.manage.model.dto.EventDto.EventConflictDto;
import com.event.manage.model.entity.Attendance;
import com.event.manage.model.entity.Event;
import com.event.manage.model.entity.User;
import com.event.manage.repository.AttendanceRepository;
import com.event.manage.repository.EventRepository;
import com.event.manage.repository.UserRepository;
import com.event.manage.service.AttendanceService;
import com.event.manage.service.EventService;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of EventService interface.
 */
@Service
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final AttendanceRepository attendanceRepository;
    private final EventMapper eventMapper;

    @Autowired
    public EventServiceImpl(
            EventRepository eventRepository,
            UserRepository userRepository,
            AttendanceRepository attendanceRepository,
            EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.attendanceRepository = attendanceRepository;
        this.eventMapper = eventMapper;
    }

    @Override
    @Transactional
    public EventDto createEvent(UUID hostId, EventCreateDto createDto) {
        log.info("Creating new event for host: {}", hostId);

        // Validate event dates
        validateEventDates(createDto.getStartTime(), createDto.getEndTime());

        // Get host user
        User host = userRepository.findById(hostId)
                .orElseThrow(() -> new ResourceNotFoundException("Host user not found with ID: " + hostId));

        // Check for time conflicts
        checkConflicts(hostId, createDto.getStartTime(), createDto.getEndTime(), null);

        // Create event
        Event event = eventMapper.createDtoToEntity(createDto);
        event.setHost(host);

        Event savedEvent = eventRepository.save(event);
        log.info("Event created successfully: {}", savedEvent.getId());

        return eventMapper.toDto(savedEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public EventDto getEventById(UUID id, UUID currentUserId) {
        log.info("Getting event by ID: {}", id);
        Event event = eventRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + id));

        // Check if user is authorized to view this event
        if (event.getVisibility() == Event.Visibility.PRIVATE &&
                !event.getHost().getId().equals(currentUserId) &&
                !attendanceRepository.existsByEvent_IdAndUser_Id(id, currentUserId)) {
            throw new UnauthorizedException("You are not authorized to view this private event");
        }

        // Check if user is attending
        boolean isAttending = attendanceRepository.existsByEvent_IdAndUser_Id(id, currentUserId);
        String attendanceStatus = null;

        if (isAttending) {
            Attendance attendance = attendanceRepository.findByEvent_IdAndUser_Id(id, currentUserId)
                    .orElse(null);
            if (attendance != null) {
                attendanceStatus = attendance.getStatus().name();
            }
        }

        EventDto eventDto = eventMapper.toDto(event);
        eventDto.setAttending(isAttending);
        eventDto.setAttendanceStatus(attendanceStatus);
        return eventDto;
    }

    @Override
    @Transactional
    public EventDto updateEvent(UUID id, EventUpdateDto updateDto, UUID currentUserId) {
        log.info("Updating event: {}", id);
        Event event = eventRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + id));

        // Check if user is authorized to update this event
        if (!event.getHost().getId().equals(currentUserId) &&
                !userRepository.findById(currentUserId)
                        .map(User::isAdmin)
                        .orElse(false)) {
            throw new UnauthorizedException("Only the host or an admin can update this event");
        }

        // Validate event dates
        validateEventDates(updateDto.getStartTime(), updateDto.getEndTime());

        // Check for time conflicts
        checkConflicts(event.getHost().getId(), updateDto.getStartTime(), updateDto.getEndTime(), id);

        // Update event
        eventMapper.updateEntityFromDto(updateDto, event);
        Event updatedEvent = eventRepository.save(event);

        log.info("Event updated successfully: {}", updatedEvent.getId());
        return eventMapper.toDto(updatedEvent,
                attendanceRepository.existsByEvent_IdAndUser_Id(id, currentUserId),
                null);
    }

    @Override
    @Transactional
    public void deleteEvent(UUID id, UUID currentUserId) {
        log.info("Deleting event: {}", id);
        Event event = eventRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + id));

        // Check if user is authorized to delete this event
        if (!event.getHost().getId().equals(currentUserId) &&
                !userRepository.findById(currentUserId)
                        .map(User::isAdmin)
                        .orElse(false)) {
            throw new UnauthorizedException("Only the host or an admin can delete this event");
        }

        // Soft delete the event
        event.softDelete();
        eventRepository.save(event);

        log.info("Event soft-deleted: {}", id);
    }

    @Override
    @Transactional
    public EventDto restoreEvent(UUID id, UUID currentUserId) {
        log.info("Restoring event: {}", id);
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + id));

        if (!event.isDeleted()) {
            throw new ValidationException("Event is not deleted");
        }

        // Check if user is authorized to restore this event
        if (!event.getHost().getId().equals(currentUserId) &&
                !userRepository.findById(currentUserId)
                        .map(User::isAdmin)
                        .orElse(false)) {
            throw new UnauthorizedException("Only the host or an admin can restore this event");
        }

        // Restore the event
        event.restore();
        Event restoredEvent = eventRepository.save(event);

        log.info("Event restored: {}", id);
        return eventMapper.toDto(restoredEvent, false, null);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventSummaryDto> getAllEvents(Pageable pageable) {
        log.info("Getting all events with pagination");
        return eventRepository.findAllByDeletedFalse(pageable)
                .map(eventMapper::toSummaryDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventSummaryDto> getEventsByHost(UUID hostId, Pageable pageable) {
        log.info("Getting events by host: {}", hostId);
        return eventRepository.findAllByHost_IdAndDeletedFalse(hostId, pageable)
                .map(eventMapper::toSummaryDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventSummaryDto> getEventsByAttendee(UUID userId, Pageable pageable) {
        log.info("Getting events by attendee: {}", userId);
        return eventRepository.findEventsByAttendeeId(userId, pageable)
                .map(eventMapper::toSummaryDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventSummaryDto> getUpcomingEvents(Pageable pageable) {
        log.info("Getting upcoming events");
        return eventRepository.findAllByStartTimeAfterAndDeletedFalse(ZonedDateTime.now(), pageable)
                .map(eventMapper::toSummaryDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventSummaryDto> searchEvents(String searchTerm, Pageable pageable) {
        log.info("Searching events with term: {}", searchTerm);
        return eventRepository.searchEvents(searchTerm, pageable)
                .map(eventMapper::toSummaryDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventSummaryDto> filterEvents(EventFilterDto filterDto, Pageable pageable) {
        log.info("Filtering events");

        // Build dynamic specifications based on filter criteria
        // This would involve creating a custom specification builder for the EventRepository
        // For simplicity, we'll just return all events here
        // In a real application, this would be implemented with Spring Data JPA Specifications

        return eventRepository.findAllByDeletedFalse(pageable)
                .map(eventMapper::toSummaryDto);
    }

    @Override
    @Transactional(readOnly = true)
    public EventStatsDto getEventStats(UUID id) {
        log.info("Getting stats for event: {}", id);
        Event event = eventRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + id));

        // Get attendance counts
        long goingCount = attendanceRepository.countByEvent_IdAndStatus(id, Attendance.Status.GOING);
        long maybeCount = attendanceRepository.countByEvent_IdAndStatus(id, Attendance.Status.MAYBE);
        long declinedCount = attendanceRepository.countByEvent_IdAndStatus(id, Attendance.Status.DECLINED);
        long totalAttendees = goingCount + maybeCount + declinedCount;

        // Calculate attendance rate
        double attendanceRate = totalAttendees > 0 ? (double) goingCount / totalAttendees * 100 : 0;

        return EventStatsDto.builder()
                .eventId(event.getId())
                .title(event.getTitle())
                .hostName(event.getHost().getName())
                .startTime(event.getStartTime())
                .totalAttendees(totalAttendees)
                .goingCount(goingCount)
                .maybeCount(maybeCount)
                .declinedCount(declinedCount)
                .attendanceRate(attendanceRate)
                .createdAt(event.getCreatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventSummaryDto> getEventsHappeningToday() {
        log.info("Getting events happening today");
        LocalDate today = LocalDate.now();
        ZonedDateTime startOfDay = today.atStartOfDay(ZoneId.systemDefault());
        ZonedDateTime endOfDay = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).minusSeconds(1);

        return eventRepository.findEventsHappeningToday(startOfDay, endOfDay).stream()
                .map(eventMapper::toSummaryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventSummaryDto> getMostPopularEvents(Pageable pageable) {
        log.info("Getting most popular events");
        return eventRepository.findMostPopularEvents(pageable)
                .map(eventMapper::toSummaryDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventConflictDto> checkEventConflicts(UUID userId, ZonedDateTime startTime,
                                                      ZonedDateTime endTime, UUID excludeEventId) {
        log.info("Checking event conflicts for user: {}", userId);

        List<Event> conflictingEvents = eventRepository.findConflictingEventsForUser(
                userId, startTime, endTime);

        // Filter out the event being updated if provided
        if (excludeEventId != null) {
            conflictingEvents = conflictingEvents.stream()
                    .filter(event -> !event.getId().equals(excludeEventId))
                    .collect(Collectors.toList());
        }

        return conflictingEvents.stream()
                .map(event -> EventConflictDto.builder()
                        .eventId(event.getId())
                        .title(event.getTitle())
                        .startTime(event.getStartTime())
                        .endTime(event.getEndTime())
                        .location(event.getLocation())
                        .conflictReason("Time overlap with existing event")
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventSummaryDto> getPublicEvents(Pageable pageable) {
        log.info("Getting public events");
        return eventRepository.findAllByVisibilityAndDeletedFalse(
                        Event.Visibility.PUBLIC, pageable)
                .map(eventMapper::toSummaryDto);
    }

    /**
     * Validate event dates to ensure they are valid.
     *
     * @param startTime the start time
     * @param endTime the end time
     */
    private void validateEventDates(ZonedDateTime startTime, ZonedDateTime endTime) {
        ZonedDateTime now = ZonedDateTime.now();

        if (startTime.isBefore(now)) {
            throw new ValidationException("Event start time must be in the future");
        }

        if (endTime.isBefore(startTime)) {
            throw new ValidationException("Event end time must be after start time");
        }

        if (endTime.isBefore(now)) {
            throw new ValidationException("Event end time must be in the future");
        }
    }

    /**
     * Check for time conflicts with existing events.
     *
     * @param userId the user ID to check conflicts for
     * @param startTime the start time
     * @param endTime the end time
     * @param excludeEventId event ID to exclude from conflict check (for updates)
     */
    private void checkConflicts(UUID userId, ZonedDateTime startTime, ZonedDateTime endTime, UUID excludeEventId) {
        List<EventConflictDto> conflicts = checkEventConflicts(userId, startTime, endTime, excludeEventId);

        if (!conflicts.isEmpty()) {
            throw new EventConflictException("Event conflicts with existing events", conflicts);
        }
    }
}