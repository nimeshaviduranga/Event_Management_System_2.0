package com.event.manage.model.dto;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import com.event.manage.model.entity.Event.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Event entity.
 *
 * Provides different DTOs for various use cases:
 * - Full EventDto for complete event information
 * - EventCreateDto for event creation
 * - EventUpdateDto for event updates
 * - EventSummaryDto for event listings
 * - EventFilterDto for search and filtering
 *
 * @author Event Management Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventDto {

    private UUID id;

    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 100, message = "Title must be between 5 and 100 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    private String description;

    private UUID hostId;

    private String hostName;

    @NotNull(message = "Start time is required")
    @Future(message = "Start time must be in the future")
    private ZonedDateTime startTime;

    @NotNull(message = "End time is required")
    @Future(message = "End time must be in the future")
    private ZonedDateTime endTime;

    @NotBlank(message = "Location is required")
    @Size(min = 3, max = 100, message = "Location must be between 3 and 100 characters")
    private String location;

    @NotNull(message = "Visibility is required")
    private Visibility visibility;

    private ZonedDateTime createdAt;

    private ZonedDateTime updatedAt;

    private List<AttendeeDto> attendees;

    private long attendeeCount;

    private boolean isAttending;

    private String attendanceStatus;

    /**
     * DTO for creating new events.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class EventCreateDto {

        @NotBlank(message = "Title is required")
        @Size(min = 5, max = 100, message = "Title must be between 5 and 100 characters")
        private String title;

        @NotBlank(message = "Description is required")
        @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
        private String description;

        @NotNull(message = "Start time is required")
        @Future(message = "Start time must be in the future")
        private ZonedDateTime startTime;

        @NotNull(message = "End time is required")
        @Future(message = "End time must be in the future")
        private ZonedDateTime endTime;

        @NotBlank(message = "Location is required")
        @Size(min = 3, max = 100, message = "Location must be between 3 and 100 characters")
        private String location;

        @NotNull(message = "Visibility is required")
        private Visibility visibility;
    }

    /**
     * DTO for updating existing events.
     * Similar to create but may have relaxed validation for past events.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class EventUpdateDto {

        @NotBlank(message = "Title is required")
        @Size(min = 5, max = 100, message = "Title must be between 5 and 100 characters")
        private String title;

        @NotBlank(message = "Description is required")
        @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
        private String description;

        @NotNull(message = "Start time is required")
        private ZonedDateTime startTime;

        @NotNull(message = "End time is required")
        private ZonedDateTime endTime;

        @NotBlank(message = "Location is required")
        @Size(min = 3, max = 100, message = "Location must be between 3 and 100 characters")
        private String location;

        @NotNull(message = "Visibility is required")
        private Visibility visibility;
    }

    /**
     * DTO for event summary in listings.
     * Contains essential information without heavy data like attendees.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class EventSummaryDto {

        private UUID id;
        private String title;
        private String description;
        private String hostName;
        private ZonedDateTime startTime;
        private ZonedDateTime endTime;
        private String location;
        private Visibility visibility;
        private long attendeeCount;
        private boolean isUpcoming;
        private boolean isOngoing;
        private boolean isPast;
    }

    /**
     * DTO for filtering and searching events.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class EventFilterDto {

        private String hostId;
        private String hostName;
        private String location;
        private ZonedDateTime startDate;
        private ZonedDateTime endDate;
        private Visibility visibility;
        private String searchTerm;
        private Boolean upcomingOnly;
        private Boolean pastOnly;
        private Integer minAttendees;
        private Integer maxAttendees;
        private String sortBy;
        private String sortDirection;
    }

    /**
     * DTO for event attendee information.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AttendeeDto {

        private UUID userId;
        private String userName;
        private String userEmail;
        private String status;
        private ZonedDateTime respondedAt;
    }

    /**
     * DTO for event statistics and analytics.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class EventStatsDto {

        private UUID eventId;
        private String title;
        private String hostName;
        private ZonedDateTime startTime;
        private long totalAttendees;
        private long goingCount;
        private long maybeCount;
        private long declinedCount;
        private double attendanceRate;
        private ZonedDateTime createdAt;
    }

    /**
     * DTO for event calendar view.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class EventCalendarDto {

        private UUID id;
        private String title;
        private ZonedDateTime start;
        private ZonedDateTime end;
        private String location;
        private String color;
        private boolean allDay;
        private String url;
    }

    /**
     * DTO for event conflict checking.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class EventConflictDto {

        private UUID eventId;
        private String title;
        private ZonedDateTime startTime;
        private ZonedDateTime endTime;
        private String location;
        private String conflictReason;
    }
}