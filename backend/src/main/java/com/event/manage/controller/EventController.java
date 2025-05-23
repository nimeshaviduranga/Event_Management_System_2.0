package com.event.manage.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.event.manage.model.dto.EventDto;
import com.event.manage.model.dto.EventDto.EventCreateDto;
import com.event.manage.model.dto.EventDto.EventFilterDto;
import com.event.manage.model.dto.EventDto.EventSummaryDto;
import com.event.manage.model.dto.EventDto.EventUpdateDto;
import com.event.manage.security.UserUtils;
import com.event.manage.service.EventService;

import jakarta.validation.Valid;

/**
 * REST controller for event-related endpoints.
 */
@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;
    private final UserUtils userUtils;

    @Autowired
    public EventController(EventService eventService, UserUtils userUtils) {
        this.eventService = eventService;
        this.userUtils = userUtils;
    }

    /**
     * Create an event (only by authenticated users)
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EventDto> createEvent(@Valid @RequestBody EventCreateDto createDto,
                                                Authentication authentication) {
        UUID currentUserId = userUtils.getCurrentUserId(authentication);
        EventDto newEvent = eventService.createEvent(currentUserId, createDto);
        return new ResponseEntity<>(newEvent, HttpStatus.CREATED);
    }

    /**
     * Update an event (only by host or admin)
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EventDto> updateEvent(@PathVariable UUID id,
                                                @Valid @RequestBody EventUpdateDto updateDto,
                                                Authentication authentication) {
        UUID currentUserId = userUtils.getCurrentUserId(authentication);
        EventDto updatedEvent = eventService.updateEvent(id, updateDto, currentUserId);
        return ResponseEntity.ok(updatedEvent);
    }

    /**
     * Delete an event (host or admin)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteEvent(@PathVariable UUID id, Authentication authentication) {
        UUID currentUserId = userUtils.getCurrentUserId(authentication);
        eventService.deleteEvent(id, currentUserId);
        return ResponseEntity.noContent().build();
    }

    /**
     * List events with filtering by date, location, visibility
     */
    @PostMapping("/filter")
    public ResponseEntity<Page<EventSummaryDto>> filterEvents(@RequestBody EventFilterDto filterDto, Pageable pageable) {
        Page<EventSummaryDto> events = eventService.filterEvents(filterDto, pageable);
        return ResponseEntity.ok(events);
    }

    /**
     * List upcoming events (paginated)
     */
    @GetMapping("/upcoming")
    public ResponseEntity<Page<EventSummaryDto>> getUpcomingEvents(Pageable pageable) {
        Page<EventSummaryDto> events = eventService.getUpcomingEvents(pageable);
        return ResponseEntity.ok(events);
    }

    /**
     * Status check of an event
     */
    @GetMapping("/{id}/status")
    public ResponseEntity<Boolean> checkEventStatus(@PathVariable UUID id) {
        // This endpoint checks if the event exists and is active
        // We can use the getEventById method and handle the result
        try {
            eventService.getEventById(id, null);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }

    /**
     * Get event details with attendee count
     */
    @GetMapping("/{id}")
    public ResponseEntity<EventDto> getEventById(@PathVariable UUID id, Authentication authentication) {
        UUID currentUserId = authentication != null ? userUtils.getCurrentUserId(authentication) : null;
        EventDto event = eventService.getEventById(id, currentUserId);
        return ResponseEntity.ok(event);
    }

    /**
     * List all events a user is hosting
     */
    @GetMapping("/by-host/{hostId}")
    public ResponseEntity<Page<EventSummaryDto>> getEventsByHost(@PathVariable UUID hostId, Pageable pageable) {
        Page<EventSummaryDto> events = eventService.getEventsByHost(hostId, pageable);
        return ResponseEntity.ok(events);
    }
}