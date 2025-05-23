package com.event.manage.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.event.manage.model.dto.AttendanceDto;
import com.event.manage.model.entity.Attendance;

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
    @Mapping(target = "userId", source = "user.id")
    AttendanceDto toDto(Attendance attendance);

    /**
     * Convert list of Attendance entities to list of AttendanceDtos.
     *
     * @param attendances The list of attendance entities
     * @return List of AttendanceDtos
     */
    List<AttendanceDto> toDtoList(List<Attendance> attendances);

    /**
     * Create an AttendanceStatsDto from counts.
     *
     * @param going Number of attendees with GOING status
     * @param maybe Number of attendees with MAYBE status
     * @param declined Number of attendees with DECLINED status
     * @return AttendanceStatsDto with the counts
     */
    default AttendanceDto.AttendanceStatsDto toStatsDto(long going, long maybe, long declined) {
        return AttendanceDto.AttendanceStatsDto.builder()
                .going(going)
                .maybe(maybe)
                .declined(declined)
                .total(going + maybe + declined)
                .build();
    }
}