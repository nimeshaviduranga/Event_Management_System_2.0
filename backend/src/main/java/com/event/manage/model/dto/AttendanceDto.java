package com.event.manage.model.dto;

import java.time.ZonedDateTime;
import java.util.UUID;

import com.event.manage.model.entity.Attendance.Status;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Attendance entity.
 *
 * Provides different DTOs for various attendance-related operations:
 * - Full AttendanceDto for complete attendance information
 * - StatusUpdateDto for updating attendance status
 * - AttendanceStatsDto for statistics and analytics
 * - AttendanceHistoryDto for user attendance history
 *
 * @author Event Management Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttendanceDto {

    private UUID eventId;

    private String eventTitle;

    private ZonedDateTime eventStartTime;

    private String eventLocation;

    private UUID userId;

    private String userName;

    private String userEmail;

    @NotNull(message = "Status is required")
    private Status status;

    private ZonedDateTime respondedAt;

    /**
     * DTO for updating attendance status.
     * Contains only the status field that can be updated.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class StatusUpdateDto {

        @NotNull(message = "Status is required")
        private Status status;
    }

    /**
     * DTO for attendance statistics.
     * Provides aggregated counts for different attendance statuses.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AttendanceStatsDto {

        private long going;
        private long maybe;
        private long declined;
        private long total;
        private double goingPercentage;
        private double maybePercentage;
        private double declinedPercentage;
    }

    /**
     * DTO for user attendance history.
     * Contains information about past attendance patterns.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AttendanceHistoryDto {

        private UUID userId;
        private String userName;
        private UUID eventId;
        private String eventTitle;
        private String eventLocation;
        private ZonedDateTime eventStartTime;
        private ZonedDateTime eventEndTime;
        private Status status;
        private ZonedDateTime respondedAt;
        private boolean eventCompleted;
        private String hostName;
    }

    /**
     * DTO for event attendance summary.
     * Provides a summary of attendance for a specific event.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class EventAttendanceSummaryDto {

        private UUID eventId;
        private String eventTitle;
        private ZonedDateTime eventStartTime;
        private String eventLocation;
        private String hostName;
        private AttendanceStatsDto stats;
        private ZonedDateTime lastUpdated;
    }

    /**
     * DTO for user attendance summary.
     * Provides a summary of all events a user has attended.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class UserAttendanceSummaryDto {

        private UUID userId;
        private String userName;
        private long totalEventsAttended;
        private long upcomingEvents;
        private long pastEvents;
        private long goingCount;
        private long maybeCount;
        private long declinedCount;
        private double attendanceRate;
        private ZonedDateTime firstEventDate;
        private ZonedDateTime lastEventDate;
    }

    /**
     * DTO for attendance creation request.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AttendanceCreateDto {

        @NotNull(message = "Event ID is required")
        private UUID eventId;

        @NotNull(message = "Status is required")
        private Status status;
    }

    /**
     * DTO for bulk attendance operations.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class BulkAttendanceDto {

        @NotNull(message = "Event ID is required")
        private UUID eventId;

        @NotNull(message = "User IDs are required")
        private java.util.List<UUID> userIds;

        @NotNull(message = "Status is required")
        private Status status;
    }

    /**
     * DTO for attendance notifications.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AttendanceNotificationDto {

        private UUID eventId;
        private String eventTitle;
        private ZonedDateTime eventStartTime;
        private String eventLocation;
        private UUID userId;
        private String userName;
        private Status oldStatus;
        private Status newStatus;
        private ZonedDateTime changedAt;
        private String notificationType;
    }

    /**
     * DTO for attendance analytics.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AttendanceAnalyticsDto {

        private String period;
        private long totalAttendances;
        private long uniqueAttendees;
        private long uniqueEvents;
        private double averageAttendeesPerEvent;
        private double averageEventsPerUser;
        private AttendanceStatsDto statusDistribution;
        private java.util.List<DailyAttendanceDto> dailyStats;
    }

    /**
     * DTO for daily attendance statistics.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DailyAttendanceDto {

        private java.time.LocalDate date;
        private long attendanceCount;
        private long eventCount;
        private long userCount;
    }
}