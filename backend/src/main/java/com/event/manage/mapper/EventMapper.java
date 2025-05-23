package com.event.manage.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import com.event.manage.model.dto.EventDto;
import com.event.manage.model.dto.EventDto.AttendeeDto;
import com.event.manage.model.entity.Attendance;
import com.event.manage.model.entity.Event;
import com.event.manage.model.entity.User;

/**
 * MapStruct mapper for converting between Event entity and EventDto.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface EventMapper {

    /**
     * Convert Event entity to EventDto.
     *
     * @param event The event entity
     * @return EventDto
     */
    @Mapping(target = "hostId", source = "host.id")
    @Mapping(target = "hostName", source = "host.name")
    @Mapping(target = "attendees", source = "attendances", qualifiedByName = "attendancesToAttendeeDtos")
    @Mapping(target = "attendeeCount", expression = "java(event.getAttendances().size())")
    EventDto toDto(Event event);

    /**
     * Convert list of Event entities to list of EventDtos.
     *
     * @param events The list of event entities
     * @return List of EventDtos
     */
    List<EventDto> toDtoList(List<Event> events);

    /**
     * Convert EventDto to Event entity.
     *
     * @param eventDto The event DTO
     * @return Event entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "host", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", constant = "false")
    @Mapping(target = "attendances", ignore = true)
    Event toEntity(EventDto eventDto);

    /**
     * Convert EventCreateDto to Event entity.
     *
     * @param createDto The event create DTO
     * @return Event entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "host", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", constant = "false")
    @Mapping(target = "attendances", ignore = true)
    Event createDtoToEntity(EventDto.EventCreateDto createDto);

    /**
     * Update Event entity from EventDto.
     *
     * @param eventDto The event DTO with updated values
     * @param event The event entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "host", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "attendances", ignore = true)
    void updateEntityFromDto(EventDto.EventCreateDto eventDto, @MappingTarget Event event);

    /**
     * Convert a list of Attendance entities to a list of AttendeeDtos.
     *
     * @param attendances The list of attendance entities
     * @return List of AttendeeDtos
     */
    @Named("attendancesToAttendeeDtos")
    default List<AttendeeDto> attendancesToAttendeeDtos(List<Attendance> attendances) {
        if (attendances == null) {
            return null;
        }

        return attendances.stream()
                .map(attendance -> {
                    User user = attendance.getUser();
                    return AttendeeDto.builder()
                            .userId(user.getId())
                            .userName(user.getName())
                            .status(attendance.getStatus().name())
                            .respondedAt(attendance.getRespondedAt())
                            .build();
                })
                .collect(Collectors.toList());
    }
}