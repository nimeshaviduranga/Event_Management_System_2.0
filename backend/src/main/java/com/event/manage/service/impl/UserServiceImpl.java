package com.event.manage.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.event.manage.exception.ResourceNotFoundException;
import com.event.manage.exception.UserAlreadyExistsException;
import com.event.manage.exception.ValidationException;
import com.event.manage.mapper.UserMapper;
import com.event.manage.model.dto.UserDto;
import com.event.manage.model.dto.UserDto.AdminUserDto;
import com.event.manage.model.dto.UserDto.PasswordChangeDto;
import com.event.manage.model.dto.UserDto.RegistrationDto;
import com.event.manage.model.dto.UserDto.UserResponse;
import com.event.manage.model.dto.UserDto.UserSearchDto;
import com.event.manage.model.dto.UserDto.UserStatsDto;
import com.event.manage.model.dto.UserDto.UserUpdateDto;
import com.event.manage.model.entity.User;
import com.event.manage.repository.UserRepository;
import com.event.manage.service.UserService;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of UserService interface.
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserResponse register(RegistrationDto registrationDto) {
        log.info("Registering new user with email: {}", registrationDto.getEmail());

        // Validate password confirmation
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            throw new ValidationException("Passwords do not match");
        }

        // Check if email already exists
        if (userRepository.existsByEmailIgnoreCase(registrationDto.getEmail())) {
            throw new UserAlreadyExistsException("Email already in use: " + registrationDto.getEmail());
        }

        // Create new user
        User user = userMapper.registrationToEntity(registrationDto);
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getId());

        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID id) {
        log.info("Getting user by ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        log.info("Getting user by email: {}", email);
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(UUID id, UserUpdateDto updateDto) {
        log.info("Updating user: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        // Check if new email is already in use by another user
        if (!user.getEmail().equalsIgnoreCase(updateDto.getEmail()) &&
                userRepository.existsByEmailIgnoreCase(updateDto.getEmail())) {
            throw new UserAlreadyExistsException("Email already in use: " + updateDto.getEmail());
        }

        userMapper.updateEntityFromDto(updateDto, user);
        User updatedUser = userRepository.save(user);

        log.info("User updated successfully: {}", updatedUser.getId());
        return userMapper.toResponse(updatedUser);
    }

    @Override
    @Transactional
    public boolean changePassword(UUID id, PasswordChangeDto passwordChangeDto) {
        log.info("Changing password for user: {}", id);

        // Validate password confirmation
        if (!passwordChangeDto.getNewPassword().equals(passwordChangeDto.getConfirmPassword())) {
            throw new ValidationException("Passwords do not match");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        // Verify current password
        if (!passwordEncoder.matches(passwordChangeDto.getCurrentPassword(), user.getPassword())) {
            throw new ValidationException("Current password is incorrect");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(passwordChangeDto.getNewPassword()));
        userRepository.save(user);

        log.info("Password changed successfully for user: {}", id);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        log.info("Getting all users with pagination");
        return userRepository.findAll(pageable)
                .map(userMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserSearchDto> searchUsers(String searchTerm, Pageable pageable) {
        log.info("Searching users with term: {}", searchTerm);
        return userRepository.searchUsers(searchTerm, pageable)
                .map(userMapper::toSearchDto);
    }

    @Override
    @Transactional(readOnly = true)
    public UserStatsDto getUserStats(UUID id) {
        log.info("Getting stats for user: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        return userMapper.toStatsDto(user);
    }

    @Override
    @Transactional
    public AdminUserDto updateUserAsAdmin(UUID id, AdminUserDto adminUserDto) {
        log.info("Admin updating user: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        // Check if new email is already in use by another user
        if (adminUserDto.getEmail() != null && !user.getEmail().equalsIgnoreCase(adminUserDto.getEmail()) &&
                userRepository.existsByEmailIgnoreCase(adminUserDto.getEmail())) {
            throw new UserAlreadyExistsException("Email already in use: " + adminUserDto.getEmail());
        }

        userMapper.updateEntityFromAdminDto(adminUserDto, user);
        User updatedUser = userRepository.save(user);

        log.info("User updated successfully by admin: {}", updatedUser.getId());
        return userMapper.toAdminDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        log.info("Deleting user: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        // Soft delete by setting active to false
        user.setActive(false);
        userRepository.save(user);

        log.info("User soft-deleted: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getUsersByRole(String role, Pageable pageable) {
        log.info("Getting users by role: {}", role);
        try {
            User.Role userRole = User.Role.valueOf(role.toUpperCase());
            return userRepository.findAllByRole(userRole, pageable)
                    .map(userMapper::toResponse);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid role: " + role);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmailIgnoreCase(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserStatsDto> getMostActiveUsers(Pageable pageable) {
        log.info("Getting most active users");
        return userRepository.findMostActiveUsers(pageable)
                .map(userMapper::toStatsDto);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmailIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

        if (!user.isActive()) {
            throw new UsernameNotFoundException("User is inactive: " + username);
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}