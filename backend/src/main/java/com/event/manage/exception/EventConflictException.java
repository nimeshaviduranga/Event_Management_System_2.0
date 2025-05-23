package com.event.manage.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.event.manage.model.dto.EventDto.EventConflictDto;

@ResponseStatus(HttpStatus.CONFLICT)
public class EventConflictException extends RuntimeException {

    private final List<EventConflictDto> conflicts;

    public EventConflictException(String message, List<EventConflictDto> conflicts) {
        super(message);
        this.conflicts = conflicts;
    }

    public List<EventConflictDto> getConflicts() {
        return conflicts;
    }
}