package com.event.manage.security;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Utility class for user-related operations in security context.
 */
@Component
public class UserUtils {

    /**
     * Get the current user ID from the authentication.
     *
     * @param authentication the current user authentication
     * @return the current user ID
     */
    public UUID getCurrentUserId(Authentication authentication) {
        // In a real application, you would extract this from the JWT token
        return UUID.fromString("11111111-1111-1111-1111-111111111111");
    }
}