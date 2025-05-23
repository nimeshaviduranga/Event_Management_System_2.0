package com.event.manage.service.impl;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.event.manage.exception.ResourceNotFoundException;
import com.event.manage.exception.UnauthorizedException;
import com.event.manage.exception.ValidationException;
import com.event.manage.mapper.AttendanceMapper;
import com.event.manage.model.dto.AttendanceDto;
import com.event.manage.model.dto.AttendanceDto.AttendanceCreateDto;
import com.event.manage.model.dto.AttendanceDto.AttendanceHistoryDto;
import com.event.manage.model.dto.AttendanceDto.AttendanceStatsDto;
import com.event.manage.model.dto.AttendanceDto.BulkAttendanceDto;
import com.event.manage.model.dto.AttendanceDto.EventAttendanceSummaryDto;
import com.event.manage.model.dto.AttendanceDto.StatusUpdateDto;
import com.event.manage.model.dto.AttendanceDto.UserAttendanceSummaryDto;
import com.event.manage.model.entity.Attendance;
import com.event.manage.model.entity.AttendanceId;
import com.event.manage.model.entity.Event;
import com.event.manage.model.entity.User;
import com.event.manage.repository.AttendanceRepository;
import com.event.manage.repository.EventRepository;
import com.event.manage.repository.UserRepository;
import com.event.manage.service.AttendanceService;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of AttendanceService interface.
 */
@Service
@Slf4j
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final AttendanceMapper attendanceMapper;

    @Autowired
    public AttendanceServiceImpl(
            AttendanceRepository attendanceRepository,
            EventRepository eventRepository,
            UserRepository userRepository,
            AttendanceMapper attendanceMapper) {
        this.attendanceRepository = attendanceRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.attendanceMapper = attendanceMapper;
    }

    @Override
    @Transactional
    public AttendanceDto createAttendance(UUID userId, AttendanceCreateDto createDto) {
        log.info("Creating attendance for user: {} and event: {}", userId, createDto.getEventId());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        Event event = eventRepository.findByIdAndDeletedFalse(createDto.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + createDto.getEventId()));

        // Check if the event is in the past
        if (event.isPast()) {
            throw new ValidationException("Cannot attend a past event");
        }

        // Check if the event is private
        if (event.getVisibility() == Event.Visibility.PRIVATE && !event.getHost().getId().equals(userId)) {
            // TODO: In a real application, we would check if the user has an invitation
            throw new UnauthorizedException("You are not authorized to attend this private event");
        }

        // Check if attendance already exists
        AttendanceId attendanceId = new AttendanceId(event.getId(), user.getId());
        if (attendanceRepository.existsById(attendanceId)) {
            throw new ValidationException("You are already attending this event");
        }

        // Create attendance
        Attendance attendance = Attendance.of(event, user, createDto.getStatus());
        Attendance savedAttendance = attendanceRepository.save(attendance);

        log.info("Attendance created successfully for user: {} and event: {}", userId, createDto.getEventId());
        return attendanceMapper.toDto(savedAttendance);
    }

    @Override
    @Transactional
    public AttendanceDto updateAttendanceStatus(UUID userId, UUID eventId, StatusUpdateDto statusUpdateDto) {
        log.info("Updating attendance status for user: {} and event: {}", userId, eventId);

        Attendance attendance = attendanceRepository.findByEvent_IdAndUser_Id(eventId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found"));

        // Check if the event is in the past
        if (attendance.getEvent().isPast()) {
            throw new ValidationException("Cannot update attendance for a past event");
        }

        // Update status
        attendance.updateStatus(statusUpdateDto.getStatus());
        Attendance updatedAttendance = attendanceRepository.save(attendance);

        log.info("Attendance status updated successfully for user: {} and event: {}", userId, eventId);
        return attendanceMapper.toDto(updatedAttendance);
    }

    @Override
    @Transactional
    public void deleteAttendance(UUID userId, UUID eventId) {
        log.info("Deleting attendance for user: {} and event: {}", userId, eventId);

        Attendance attendance = attendanceRepository.findByEvent_IdAndUser_Id(eventId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found"));

        // Check if the event is in the past
        if (attendance.getEvent().isPast()) {
            throw new ValidationException("Cannot remove attendance for a past event");
        }

        attendanceRepository.delete(attendance);
        log.info("Attendance deleted successfully for user: {} and event: {}", userId, eventId);
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceDto getAttendance(UUID userId, UUID eventId) {
        log.info("Getting attendance for user: {} and event: {}", userId, eventId);

        Attendance attendance = attendanceRepository.findByEvent_IdAndUser_Id(eventId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found"));

        return attendanceMapper.toDto(attendance);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AttendanceDto> getAttendeesByEvent(UUID eventId, Pageable pageable) {
        log.info("Getting attendees for event: {}", eventId);

        Event event = eventRepository.findByIdAndDeletedFalse(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + eventId));

        return attendanceRepository.findAllByEvent(event, pageable)
                .map(attendanceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AttendanceDto> getEventsByAttendee(UUID userId, Pageable pageable) {
        log.info("Getting events for attendee: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        return attendanceRepository.findAllByUser(user, pageable)
                .map(attendanceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceStatsDto getAttendanceStats(UUID eventId) {
        log.info("Getting attendance stats for event: {}", eventId);

        // Check if event exists
        eventRepository.findByIdAndDeletedFalse(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + eventId));

        // Get attendance counts
        long goingCount = attendanceRepository.countByEvent_IdAndStatus(eventId, Attendance.Status.GOING);
        long maybeCount = attendanceRepository.countByEvent_IdAndStatus(eventId, Attendance.Status.MAYBE);
        long declinedCount = attendanceRepository.countByEvent_IdAndStatus(eventId, Attendance.Status.DECLINED);
        long totalCount = attendanceRepository.countByEvent_Id(eventId);

        return attendanceMapper.toStatsDto(goingCount, maybeCount, declinedCount, totalCount);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AttendanceHistoryDto> getUserAttendanceHistory(UUID userId, Pageable pageable) {
        log.info("Getting attendance history for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        return attendanceRepository.findAllByUser(user, pageable)
                .map(attendanceMapper::toHistoryDto);
    }

    @Override
    @Transactional
    public List<AttendanceDto> createBulkAttendance(BulkAttendanceDto bulkAttendanceDto) {
        log.info("Creating bulk attendance for event: {}", bulkAttendanceDto.getEventId());

        Event event = eventRepository.findByIdAndDeletedFalse(bulkAttendanceDto.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + bulkAttendanceDto.getEventId()));

        // Check if the event is in the past
        if (event.isPast()) {
            throw new ValidationException("Cannot add attendees to a past event");
        }

        // Get users
        List<User> users = userRepository.findAllById(bulkAttendanceDto.getUserIds());

        if (users.size() != bulkAttendanceDto.getUserIds().size()) {
            throw new ValidationException("Some users were not found");
        }

        // Create attendances
        List<Attendance> attendances = users.stream()
                .map(user -> {
                    // Skip if attendance already exists
                    if (attendanceRepository.existsByEvent_IdAndUser_Id(event.getId(), user.getId())) {
                        return null;
                    }
                    return Attendance.of(event, user, bulkAttendanceDto.getStatus());
                })
                .filter(attendance -> attendance != null)
                .collect(Collectors.toList());

        if (attendances.isEmpty()) {
            throw new ValidationException("All users are already attending this event");
        }

        List<Attendance> savedAttendances = attendanceRepository.saveAll(attendances);

        log.info("Bulk attendance created successfully for event: {}", bulkAttendanceDto.getEventId());
        return savedAttendances.stream()
                .map(attendanceMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EventAttendanceSummaryDto getEventAttendanceSummary(UUID eventId) {
        log.info("Getting attendance summary for event: {}", eventId);

        Event event = eventRepository.findByIdAndDeletedFalse(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + eventId));

        // Get attendance stats
        AttendanceStatsDto stats = getAttendanceStats(eventId);

        return attendanceMapper.toEventSummaryDto(event, stats, ZonedDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public UserAttendanceSummaryDto getUserAttendanceSummary(UUID userId) {
        log.info("Getting attendance summary for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Get attendance counts
        List<Attendance> attendances = attendanceRepository.findAllByUser_Id(userId);

        long totalEventsAttended = attendances.size();
        ZonedDateTime now = ZonedDateTime.now();

        long upcomingEvents = attendances.stream()
                .filter(a -> !a.getEvent().isDeleted() && a.getEvent().getStartTime().isAfter(now))
                .count();

        long pastEvents = attendances.stream()
                .filter(a -> !a.getEvent().isDeleted() && a.getEvent().getEndTime().isBefore(now))
                .count();

        long goingCount = attendances.stream()
                .filter(a -> a.getStatus() == Attendance.Status.GOING)
                .count();

        long maybeCount = attendances.stream()
                .filter(a -> a.getStatus() == Attendance.Status.MAYBE)
                .count();

        long declinedCount = attendances.stream()
                .filter(a -> a.getStatus() == Attendance.Status.DECLINED)
                .count();

        double attendanceRate = totalEventsAttended > 0 ? (double) goingCount / totalEventsAttended * 100 : 0;

        // Get first and last event dates
        ZonedDateTime firstEventDate = attendances.stream()
                .map(a -> a.getEvent().getStartTime())
                .min(ZonedDateTime::compareTo)
                .orElse(null);

        ZonedDateTime lastEventDate = attendances.stream()
                .map(a -> a.getEvent().getStartTime())
                .max(ZonedDateTime::compareTo)
                .orElse(null);

        return attendanceMapper.toUserSummaryDto(
                user, totalEventsAttended, upcomingEvents, pastEvents,
                goingCount, maybeCount, declinedCount, attendanceRate,
                firstEventDate, lastEventDate);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUserAttendingEvent(UUID userId, UUID eventId) {
        return attendanceRepository.existsByEvent_IdAndUser_Id(eventId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Attendance.Status getAttendanceStatus(UUID userId, UUID eventId) {
        return attendanceRepository.findByEvent_IdAndUser_Id(eventId, userId)
                .map(Attendance::getStatus)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceHistoryDto> getUpcomingAttendance(UUID userId) {
        log.info("Getting upcoming events for user: {}", userId);

        ZonedDateTime now = ZonedDateTime.now();
        List<Attendance> upcomingAttendances = attendanceRepository.findUpcomingAttendanceByUser(userId, now);

        return upcomingAttendances.stream()
                .map(attendanceMapper::toHistoryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceHistoryDto> getPastAttendance(UUID userId) {
        log.info("Getting past events for user: {}", userId);

        ZonedDateTime now = ZonedDateTime.now();
        List<Attendance> pastAttendances = attendanceRepository.findPastAttendanceByUser(userId, now);

        return pastAttendances.stream()
                .map(attendanceMapper::toHistoryDto)
                .collect(Collectors.toList());
    }
}