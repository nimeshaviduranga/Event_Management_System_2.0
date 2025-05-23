package com.event.manage.mapper;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import com.event.manage.model.dto.UserDto;
import com.event.manage.model.dto.UserDto.AdminUserDto;
import com.event.manage.model.dto.UserDto.UserResponse;
import com.event.manage.model.dto.UserDto.UserSearchDto;
import com.event.manage.model.dto.UserDto.UserStatsDto;
import com.event.manage.model.dto.UserDto.UserUpdateDto;
import com.event.manage.model.entity.User;

/**
 * MapStruct mapper for converting between User entity and various UserDto classes.
 *
 * Provides mapping methods for different use cases:
 * - Entity to DTO conversion
 * - DTO to Entity conversion
 * - Specialized DTOs for different operations
 * - List conversions
 *
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Convert User entity to UserDto.
     *
     * @param user The user entity
     * @return UserDto
     */
    UserDto toDto(User user);

    /**
     * Convert User entity to UserResponse (without password).
     *
     * @param user The user entity
     * @return UserResponse without sensitive data
     */
    UserResponse toResponse(User user);

    /**
     * Convert UserDto to User entity.
     * Ignores fields that should not be set during conversion.
     *
     * @param userDto The user DTO
     * @return User entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "hostedEvents", ignore = true)
    @Mapping(target = "attendances", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "role", constant = "USER")
    User toEntity(UserDto userDto);

    /**
     * Convert registration DTO to User entity.
     * Sets default values for new user registration.
     *
     * @param registrationDto The registration DTO
     * @return User entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", constant = "USER")
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "hostedEvents", ignore = true)
    @Mapping(target = "attendances", ignore = true)
    @Mapping(target = "password", source = "password")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "email", source = "email")
    User registrationToEntity(UserDto.RegistrationDto registrationDto);

    /**
     * Update User entity from UserUpdateDto.
     * Only updates fields that are allowed to be changed by users.
     *
     * @param userUpdateDto The user update DTO with new values
     * @param user The user entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "hostedEvents", ignore = true)
    @Mapping(target = "attendances", ignore = true)
    void updateEntityFromDto(UserUpdateDto userUpdateDto, @MappingTarget User user);

    /**
     * Update User entity from AdminUserDto.
     * Allows admin to update role and active status.
     *
     * @param adminUserDto The admin user DTO with new values
     * @param user The user entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "hostedEvents", ignore = true)
    @Mapping(target = "attendances", ignore = true)
    void updateEntityFromAdminDto(AdminUserDto adminUserDto, @MappingTarget User user);

    /**
     * Convert User entity to AdminUserDto.
     *
     * @param user The user entity
     * @return AdminUserDto for admin operations
     */
    AdminUserDto toAdminDto(User user);

    /**
     * Convert User entity to UserSearchDto.
     *
     * @param user The user entity
     * @return UserSearchDto for search results
     */
    @Mapping(target = "hostedEventsCount", source = "user", qualifiedByName = "countHostedEvents")
    UserSearchDto toSearchDto(User user);

    /**
     * Convert User entity to UserStatsDto.
     *
     * @param user The user entity
     * @return UserStatsDto with statistics
     */
    @Mapping(target = "userId", source = "id")
    @Mapping(target = "hostedEventsCount", source = "user", qualifiedByName = "countHostedEvents")
    @Mapping(target = "attendedEventsCount", source = "user", qualifiedByName = "countAttendedEvents")
    @Mapping(target = "upcomingEventsCount", source = "user", qualifiedByName = "countUpcomingEvents")
    @Mapping(target = "lastActiveAt", source = "updatedAt")
    @Mapping(target = "joinedAt", source = "createdAt")
    UserStatsDto toStatsDto(User user);

    /**
     * Convert list of User entities to list of UserResponse DTOs.
     *
     * @param users List of user entities
     * @return List of UserResponse DTOs
     */
    List<UserResponse> toResponseList(List<User> users);

    /**
     * Convert list of User entities to list of UserSearchDto DTOs.
     *
     * @param users List of user entities
     * @return List of UserSearchDto DTOs
     */
    List<UserSearchDto> toSearchDtoList(List<User> users);

    /**
     * Convert list of User entities to list of AdminUserDto DTOs.
     *
     * @param users List of user entities
     * @return List of AdminUserDto DTOs
     */
    List<AdminUserDto> toAdminDtoList(List<User> users);

    /**
     * Count the number of events hosted by a user.
     * Excludes soft-deleted events.
     *
     * @param user The user entity
     * @return Number of hosted events
     */
    @Named("countHostedEvents")
    default long countHostedEvents(User user) {
        if (user == null || user.getHostedEvents() == null) {
            return 0;
        }
        return user.getHostedEvents().stream()
                .filter(event -> !event.isDeleted())
                .count();
    }

    /**
     * Count the number of events a user is attending.
     *
     * @param user The user entity
     * @return Number of attended events
     */
    @Named("countAttendedEvents")
    default long countAttendedEvents(User user) {
        if (user == null || user.getAttendances() == null) {
            return 0;
        }
        return user.getAttendances().stream()
                .filter(attendance -> !attendance.getEvent().isDeleted())
                .count();
    }

    /**
     * Count the number of upcoming events a user is involved with.
     * Includes both hosted and attended events.
     *
     * @param user The user entity
     * @return Number of upcoming events
     */
    @Named("countUpcomingEvents")
    default long countUpcomingEvents(User user) {
        if (user == null) {
            return 0;
        }

        long hostedUpcoming = 0;
        long attendedUpcoming = 0;

        if (user.getHostedEvents() != null) {
            hostedUpcoming = user.getHostedEvents().stream()
                    .filter(event -> !event.isDeleted() && event.isFuture())
                    .count();
        }

        if (user.getAttendances() != null) {
            attendedUpcoming = user.getAttendances().stream()
                    .filter(attendance -> !attendance.getEvent().isDeleted()
                            && attendance.getEvent().isFuture())
                    .count();
        }

        return hostedUpcoming + attendedUpcoming;
    }
}