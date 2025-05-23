package com.event.manage.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.event.manage.model.dto.AttendanceDto;
import com.event.manage.model.dto.AttendanceDto.AttendanceCreateDto;
import com.event.manage.model.dto.AttendanceDto.AttendanceHistoryDto;
import com.event.manage.model.dto.AttendanceDto.AttendanceStatsDto;
import com.event.manage.model.dto.AttendanceDto.BulkAttendanceDto;
import com.event.manage.model.dto.AttendanceDto.EventAttendanceSummaryDto;
import com.event.manage.model.dto.AttendanceDto.StatusUpdateDto;
import com.event.manage.model.dto.AttendanceDto.UserAttendanceSummaryDto;
import com.event.manage.model.entity.Attendance;

/**
 * Service interface for attendance-related operations.
 */
public interface AttendanceService {

    /**
     * Create a new attendance record.
     *
     * @param userId the user ID
     * @param createDto the attendance creation data
     * @return the created attendance record
     */
    AttendanceDto createAttendance(UUID userId, AttendanceCreateDto createDto);

    /**
     * Update attendance status.
     *
     * @param userId the user ID
     * @param eventId the event ID
     * @param statusUpdateDto the status update data
     * @return the updated attendance record
     */
    AttendanceDto updateAttendanceStatus(UUID userId, UUID eventId, StatusUpdateDto statusUpdateDto);

    /**
     * Delete attendance record.
     *
     * @param userId the user ID
     * @param eventId the event ID
     */
    void deleteAttendance(UUID userId, UUID eventId);

    /**
     * Get attendance record by user ID and event ID.
     *
     * @param userId the user ID
     * @param eventId the event ID
     * @return the attendance record
     */
    AttendanceDto getAttendance(UUID userId, UUID eventId);

    /**
     * Get all attendees for an event.
     *
     * @param eventId the event ID
     * @param pageable the pagination information
     * @return page of attendance records
     */
    Page<AttendanceDto> getAttendeesByEvent(UUID eventId, Pageable pageable);

    /**
     * Get all events a user is attending.
     *
     * @param userId the user ID
     * @param pageable the pagination information
     * @return page of attendance records
     */
    Page<AttendanceDto> getEventsByAttendee(UUID userId, Pageable pageable);

    /**
     * Get attendance statistics for an event.
     *
     * @param eventId the event ID
     * @return attendance statistics
     */
    AttendanceStatsDto getAttendanceStats(UUID eventId);

    /**
     * Get attendance history for a user.
     *
     * @param userId the user ID
     * @param pageable the pagination information
     * @return page of attendance history records
     */
    Page<AttendanceHistoryDto> getUserAttendanceHistory(UUID userId, Pageable pageable);

    /**
     * Create multiple attendance records in bulk.
     *
     * @param bulkAttendanceDto the bulk attendance data
     * @return list of created attendance records
     */
    List<AttendanceDto> createBulkAttendance(BulkAttendanceDto bulkAttendanceDto);

    /**
     * Get event attendance summary.
     *
     * @param eventId the event ID
     * @return event attendance summary
     */
    EventAttendanceSummaryDto getEventAttendanceSummary(UUID eventId);

    /**
     * Get user attendance summary.
     *
     * @param userId the user ID
     * @return user attendance summary
     */
    UserAttendanceSummaryDto getUserAttendanceSummary(UUID userId);

    /**
     * Check if a user is attending an event.
     *
     * @param userId the user ID
     * @param eventId the event ID
     * @return true if user is attending
     */
    boolean isUserAttendingEvent(UUID userId, UUID eventId);

    /**
     * Get attendance status for a user and event.
     *
     * @param userId the user ID
     * @param eventId the event ID
     * @return the attendance status
     */
    Attendance.Status getAttendanceStatus(UUID userId, UUID eventId);

    /**
     * Get upcoming events for a user.
     *
     * @param userId the user ID
     * @return list of attendance records for upcoming events
     */
    List<AttendanceHistoryDto> getUpcomingAttendance(UUID userId);

    /**
     * Get past events for a user.
     *
     * @param userId the user ID
     * @return list of attendance records for past events
     */
    List<AttendanceHistoryDto> getPastAttendance(UUID userId);
}