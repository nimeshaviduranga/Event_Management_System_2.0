package com.event.manage.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Error response for exception handling.
 */
@Data
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
    private long timestamp;
}