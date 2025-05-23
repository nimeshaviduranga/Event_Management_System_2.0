package com.event.manage.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.event.manage.model.entity.User;

/**
 * Repository interface for User entity operations.
 *
 * Provides data access methods for user management including
 * authentication, user lookup, and admin operations.
 *
 * @author Event Management Team
 * @version 1.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Find a user by email address.
     * Used for authentication and user lookup.
     *
     * @param email The email address to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findByEmail(String email);

    /**
     * Find a user by email address (case insensitive).
     * Useful for preventing duplicate accounts with different cases.
     *
     * @param email The email address to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findByEmailIgnoreCase(String email);

    /**
     * Check if a user exists with the given email.
     * Used for validation during registration.
     *
     * @param email The email address to check
     * @return true if a user exists with this email, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Check if a user exists with the given email (case insensitive).
     *
     * @param email The email address to check
     * @return true if a user exists with this email, false otherwise
     */
    boolean existsByEmailIgnoreCase(String email);

    /**
     * Find all active users.
     * Returns only users with active=true.
     *
     * @param pageable Pagination information
     * @return Page of active users
     */
    Page<User> findAllByActiveTrue(Pageable pageable);

    /**
     * Find all users by role.
     *
     * @param role The user role to filter by
     * @param pageable Pagination information
     * @return Page of users with the specified role
     */
    Page<User> findAllByRole(User.Role role, Pageable pageable);

    /**
     * Find users by name containing the search term (case insensitive).
     * Useful for user search functionality.
     *
     * @param name The name to search for
     * @param pageable Pagination information
     * @return Page of users whose names contain the search term
     */
    Page<User> findAllByNameContainingIgnoreCase(String name, Pageable pageable);

    /**
     * Find users by email containing the search term (case insensitive).
     *
     * @param email The email to search for
     * @param pageable Pagination information
     * @return Page of users whose emails contain the search term
     */
    Page<User> findAllByEmailContainingIgnoreCase(String email, Pageable pageable);

    /**
     * Count active users.
     *
     * @return Number of active users
     */
    long countByActiveTrue();

    /**
     * Count users by role.
     *
     * @param role The role to count
     * @return Number of users with the specified role
     */
    long countByRole(User.Role role);

    /**
     * Find users who are hosting events.
     * Returns users who have at least one non-deleted event.
     *
     * @return List of users who are event hosts
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.hostedEvents e WHERE e.deleted = false")
    List<User> findUsersWithActiveEvents();

    /**
     * Find users attending a specific event.
     *
     * @param eventId The event ID
     * @return List of users attending the event
     */
    @Query("SELECT u FROM User u JOIN u.attendances a WHERE a.event.id = :eventId")
    List<User> findUsersByEventId(@Param("eventId") UUID eventId);

    /**
     * Find users attending a specific event with a specific status.
     *
     * @param eventId The event ID
     * @param status The attendance status
     * @return List of users attending the event with the specified status
     */
    @Query("SELECT u FROM User u JOIN u.attendances a WHERE a.event.id = :eventId AND a.status = :status")
    List<User> findUsersByEventIdAndStatus(@Param("eventId") UUID eventId,
                                           @Param("status") com.event.manage.model.entity.Attendance.Status status);

    /**
     * Find the most active users (users with the most event attendances).
     *
     * @param pageable Pagination information
     * @return Page of users ordered by attendance count
     */
    @Query("SELECT u FROM User u LEFT JOIN u.attendances a GROUP BY u ORDER BY COUNT(a) DESC")
    Page<User> findMostActiveUsers(Pageable pageable);

    /**
     * Search users by name or email.
     * Useful for admin user management with search functionality.
     *
     * @param searchTerm The term to search for in name or email
     * @param pageable Pagination information
     * @return Page of users matching the search criteria
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<User> searchUsers(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Find users created after a specific date.
     * Useful for analytics and reporting.
     *
     * @param date The date to filter from
     * @return List of users created after the specified date
     */
    @Query("SELECT u FROM User u WHERE u.createdAt > :date ORDER BY u.createdAt DESC")
    List<User> findUsersCreatedAfter(@Param("date") java.time.ZonedDateTime date);
}