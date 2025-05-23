package com.event.manage.model.dto;

import java.time.ZonedDateTime;
import java.util.UUID;

import com.event.manage.model.entity.User.Role;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for User entity.
 *
 * Provides different DTOs for various use cases:
 * - Full UserDto for complete user information
 * - RegistrationDto for user registration
 * - LoginDto for authentication
 * - UserResponse for public user information
 * - UserUpdateDto for profile updates
 *
 * @author Event Management Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

    private UUID id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Name can only contain letters and spaces")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must be less than 100 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
            message = "Password must contain at least one lowercase letter, one uppercase letter, and one digit")
    private String password;

    private Role role;

    private boolean active;

    private ZonedDateTime createdAt;

    private ZonedDateTime updatedAt;

    /**
     * DTO for user registration requests.
     * Contains only the fields needed for user registration.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RegistrationDto {

        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Name can only contain letters and spaces")
        private String name;

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 100, message = "Email must be less than 100 characters")
        private String email;

        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
                message = "Password must contain at least one lowercase letter, one uppercase letter, and one digit")
        private String password;

        @NotBlank(message = "Password confirmation is required")
        private String confirmPassword;
    }

    /**
     * DTO for login requests.
     * Contains only the fields needed for authentication.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class LoginDto {

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        private String email;

        @NotBlank(message = "Password is required")
        private String password;
    }

    /**
     * DTO for user responses without sensitive data.
     * Password and other sensitive information are excluded.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class UserResponse {

        private UUID id;

        private String name;

        private String email;

        private Role role;

        private boolean active;

        private ZonedDateTime createdAt;

        private ZonedDateTime updatedAt;
    }

    /**
     * DTO for updating user profile.
     * Contains only fields that can be updated by the user.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class UserUpdateDto {

        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Name can only contain letters and spaces")
        private String name;

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 100, message = "Email must be less than 100 characters")
        private String email;
    }

    /**
     * DTO for password change requests.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PasswordChangeDto {

        @NotBlank(message = "Current password is required")
        private String currentPassword;

        @NotBlank(message = "New password is required")
        @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
                message = "Password must contain at least one lowercase letter, one uppercase letter, and one digit")
        private String newPassword;

        @NotBlank(message = "Password confirmation is required")
        private String confirmPassword;
    }

    /**
     * DTO for admin operations on user accounts.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AdminUserDto {

        private UUID id;

        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        private String name;

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        private String email;

        @NotNull(message = "Role is required")
        private Role role;

        @NotNull(message = "Active status is required")
        private Boolean active;
    }

    /**
     * DTO for user statistics and analytics.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class UserStatsDto {

        private UUID userId;
        private String name;
        private String email;
        private long hostedEventsCount;
        private long attendedEventsCount;
        private long upcomingEventsCount;
        private ZonedDateTime lastActiveAt;
        private ZonedDateTime joinedAt;
    }

    /**
     * DTO for user search results.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class UserSearchDto {

        private UUID id;
        private String name;
        private String email;
        private Role role;
        private boolean active;
        private long hostedEventsCount;
    }
}