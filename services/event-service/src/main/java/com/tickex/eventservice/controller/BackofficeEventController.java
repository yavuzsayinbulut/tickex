package com.tickex.eventservice.controller;

import com.tickex.eventservice.model.dto.backoffice.CreateEventRequest;
import com.tickex.eventservice.model.dto.backoffice.CreateEventSessionRequest;
import com.tickex.eventservice.model.dto.backoffice.UpdateEventRequest;
import com.tickex.eventservice.model.dto.backoffice.UpdateEventSessionRequest;
import com.tickex.eventservice.model.dto.EventDetailDto;
import com.tickex.eventservice.model.dto.EventSessionSummaryDto;
import com.tickex.eventservice.model.enums.EventStatus;
import com.tickex.eventservice.security.CurrentUserProvider;
import com.tickex.eventservice.service.EventService;
import com.tickex.eventservice.service.EventSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/backoffice/events")
public class BackofficeEventController {

    private final EventService eventService;
    private final EventSessionService eventSessionService;
    private final CurrentUserProvider currentUserProvider;

    @PostMapping
    public EventDetailDto createEvent(@Valid @RequestBody CreateEventRequest request) {
        UUID organizerId = currentUserProvider.getCurrentUserId();
        return eventService.createEvent(request, organizerId);
    }

    @PutMapping("/{eventId}")
    public EventDetailDto updateEvent(@PathVariable UUID eventId,
                                      @Valid @RequestBody UpdateEventRequest request) {
        UUID currentUserId = currentUserProvider.getCurrentUserId();
        return eventService.updateEvent(eventId, request, currentUserId);
    }

    @PatchMapping("/{eventId}/status")
    public void changeStatus(@PathVariable UUID eventId,
                             @RequestParam EventStatus status) {
        UUID currentUserId = currentUserProvider.getCurrentUserId();
        eventService.changeStatus(eventId, status, currentUserId);
    }

    @PostMapping("/{eventId}/sessions")
    public EventSessionSummaryDto createSession(@PathVariable UUID eventId,
                                                @Valid @RequestBody CreateEventSessionRequest request) {
        UUID currentUserId = currentUserProvider.getCurrentUserId();
        request.setEventId(eventId);
        return eventSessionService.createSession(request, currentUserId);
    }

    @PutMapping("/sessions/{sessionId}")
    public EventSessionSummaryDto updateSession(@PathVariable UUID sessionId,
                                                @Valid @RequestBody UpdateEventSessionRequest request) {
        UUID currentUserId = currentUserProvider.getCurrentUserId();
        return eventSessionService.updateSession(sessionId, request, currentUserId);
    }
}
