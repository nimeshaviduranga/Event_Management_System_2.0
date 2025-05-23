package com.event.manage.mapper;

import java.time.ZonedDateTime;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.event.manage.model.dto.AttendanceDto;
import com.event.manage.model.dto.AttendanceDto.AttendanceHistoryDto;
import com.event.manage.model.dto.AttendanceDto.AttendanceStatsDto;
import com.event.manage.model.dto.AttendanceDto.EventAttendanceSummaryDto;
import com.event.manage.model.dto.AttendanceDto.UserAttendanceSummaryDto;
import com.event.manage.model.entity.Attendance;
import com.event.manage.model.entity.Event;
import com.event.manage.model.entity.User;

/**
 * MapStruct mapper for converting between Attendance entity and AttendanceDto.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class, EventMapper.class})
public interface AttendanceMapper {

    /**
     * Convert Attendance entity to AttendanceDto.
     *
     * @param attendance The attendance entity
     * @return AttendanceDto
     */
    @Mapping(target = "eventId", source = "event.id")
    @Mapping(target = "eventTitle", source = "event.title")
    @Mapping(target = "eventStartTime", source = "event.startTime")
    @Mapping(target = "eventLocation", source = "event.location")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", source = "user.name")
    @Mapping(target = "userEmail", source = "user.email")
    AttendanceDto toDto(Attendance attendance);

    /**
     * Convert list of Attendance entities to list of AttendanceDtos.
     *
     * @param attendances The list of attendance entities
     * @return List of AttendanceDtos
     */
    List<AttendanceDto> toDtoList(List<Attendance> attendances);

    /**
     * Convert Attendance entity to AttendanceHistoryDto.
     *
     * @param attendance The attendance entity
     * @return AttendanceHistoryDto
     */
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", source = "user.name")
    @Mapping(target = "eventId", source = "event.id")
    @Mapping(target = "eventTitle", source = "event.title")
    @Mapping(target = "eventLocation", source = "event.location")
    @Mapping(target = "eventStartTime", source = "event.startTime")
    @Mapping(target = "eventEndTime", source = "event.endTime")
    @Mapping(target = "eventCompleted", expression = "java(attendance.getEvent().isPast())")
    @Mapping(target = "hostName", source = "event.host.name")
    AttendanceHistoryDto toHistoryDto(Attendance attendance);

    /**
     * Convert list of Attendance entities to list of AttendanceHistoryDtos.
     *
     * @param attendances The list of attendance entities
     * @return List of AttendanceHistoryDtos
     */
    List<AttendanceHistoryDto> toHistoryDtoList(List<Attendance> attendances);

    /**
     * Create an AttendanceStatsDto from counts.
     *
     * @param going Number of attendees with GOING status
     * @param maybe Number of attendees with MAYBE status
     * @param declined Number of attendees with DECLINED status
     * @param total Total number of attendees
     * @return AttendanceStatsDto with the counts
     */
    default AttendanceStatsDto toStatsDto(long going, long maybe, long declined, long total) {
        return AttendanceStatsDto.builder()
                .going(going)
                .maybe(maybe)
                .declined(declined)
                .total(total)
                .goingPercentage(total > 0 ? (double) going / total * 100 : 0)
                .maybePercentage(total > 0 ? (double) maybe / total * 100 : 0)
                .declinedPercentage(total > 0 ? (double) declined / total * 100 : 0)
                .build();
    }

    /**
     * Create an EventAttendanceSummaryDto.
     *
     * @param event The event entity
     * @param stats The attendance stats
     * @param lastUpdated The last updated timestamp
     * @return EventAttendanceSummaryDto
     */
    default EventAttendanceSummaryDto toEventSummaryDto(Event event, AttendanceStatsDto stats, ZonedDateTime lastUpdated) {
        return EventAttendanceSummaryDto.builder()
                .eventId(event.getId())
                .eventTitle(event.getTitle())
                .eventStartTime(event.getStartTime())
                .eventLocation(event.getLocation())
                .hostName(event.getHost().getName())
                .stats(stats)
                .lastUpdated(lastUpdated)
                .build();
    }

    /**
     * Create a UserAttendanceSummaryDto.
     *
     * @param user The user entity
     * @param totalEventsAttended Total events attended
     * @param upcomingEvents Upcoming events count
     * @param pastEvents Past events count
     * @param goingCount Count of GOING responses
     * @param maybeCount Count of MAYBE responses
     * @param declinedCount Count of DECLINED responses
     * @param attendanceRate Attendance rate as a percentage
     * @param firstEventDate First event date
     * @param lastEventDate Last event date
     * @return UserAttendanceSummaryDto
     */
    default UserAttendanceSummaryDto toUserSummaryDto(
            User user,
            long totalEventsAttended,
            long upcomingEvents,
            long pastEvents,
            long goingCount,
            long maybeCount,
            long declinedCount,
            double attendanceRate,
            ZonedDateTime firstEventDate,
            ZonedDateTime lastEventDate) {

        return UserAttendanceSummaryDto.builder()
                .userId(user.getId())
                .userName(user.getName())
                .totalEventsAttended(totalEventsAttended)
                .upcomingEvents(upcomingEvents)
                .pastEvents(pastEvents)
                .goingCount(goingCount)
                .maybeCount(maybeCount)
                .declinedCount(declinedCount)
                .attendanceRate(attendanceRate)
                .firstEventDate(firstEventDate)
                .lastEventDate(lastEventDate)
                .build();
    }
}