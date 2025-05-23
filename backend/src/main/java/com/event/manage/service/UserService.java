package com.event.manage.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.event.manage.model.dto.UserDto;
import com.event.manage.model.dto.UserDto.AdminUserDto;
import com.event.manage.model.dto.UserDto.PasswordChangeDto;
import com.event.manage.model.dto.UserDto.RegistrationDto;
import com.event.manage.model.dto.UserDto.UserResponse;
import com.event.manage.model.dto.UserDto.UserSearchDto;
import com.event.manage.model.dto.UserDto.UserStatsDto;
import com.event.manage.model.dto.UserDto.UserUpdateDto;

/**
 * Service interface for user-related operations.
 */
public interface UserService extends UserDetailsService {

    /**
     * Register a new user.
     *
     * @param registrationDto the registration data
     * @return the created user
     */
    UserResponse register(RegistrationDto registrationDto);

    /**
     * Get user by ID.
     *
     * @param id the user ID
     * @return the user
     */
    UserResponse getUserById(UUID id);

    /**
     * Get user by email.
     *
     * @param email the user email
     * @return the user
     */
    UserResponse getUserByEmail(String email);

    /**
     * Update user profile.
     *
     * @param id the user ID
     * @param updateDto the update data
     * @return the updated user
     */
    UserResponse updateUser(UUID id, UserUpdateDto updateDto);

    /**
     * Change user password.
     *
     * @param id the user ID
     * @param passwordChangeDto the password change data
     * @return true if password was changed successfully
     */
    boolean changePassword(UUID id, PasswordChangeDto passwordChangeDto);

    /**
     * Get all users with pagination.
     *
     * @param pageable the pagination information
     * @return page of users
     */
    Page<UserResponse> getAllUsers(Pageable pageable);

    /**
     * Search users by name or email.
     *
     * @param searchTerm the search term
     * @param pageable the pagination information
     * @return page of matching users
     */
    Page<UserSearchDto> searchUsers(String searchTerm, Pageable pageable);

    /**
     * Get user statistics.
     *
     * @param id the user ID
     * @return user statistics
     */
    UserStatsDto getUserStats(UUID id);

    /**
     * Update user as admin (including role and active status).
     *
     * @param id the user ID
     * @param adminUserDto the admin update data
     * @return the updated user
     */
    AdminUserDto updateUserAsAdmin(UUID id, AdminUserDto adminUserDto);

    /**
     * Delete user.
     *
     * @param id the user ID
     */
    void deleteUser(UUID id);

    /**
     * Get all users by role.
     *
     * @param role the role
     * @param pageable the pagination information
     * @return page of users with the specified role
     */
    Page<UserResponse> getUsersByRole(String role, Pageable pageable);

    boolean existsByEmail(String email);

    Page<UserStatsDto> getMostActiveUsers(Pageable pageable);
}