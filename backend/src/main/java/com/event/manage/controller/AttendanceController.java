package com.event.manage.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.event.manage.model.dto.EventDto.EventSummaryDto;
import com.event.manage.service.EventService;

/**
 * REST controller for attendance-related endpoints.
 */
@RestController
@RequestMapping("/attendances")
public class AttendanceController {

    private final EventService eventService;

    @Autowired
    public AttendanceController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * List all events a user is attending
     */
    @GetMapping("/users/{userId}/events")
    public ResponseEntity<Page<EventSummaryDto>> getEventsByAttendee(@PathVariable UUID userId, Pageable pageable) {
        Page<EventSummaryDto> events = eventService.getEventsByAttendee(userId, pageable);
        return ResponseEntity.ok(events);
    }
}