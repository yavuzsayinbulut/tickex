package com.tickex.eventservice.service;

import com.tickex.eventservice.model.dto.backoffice.CreateEventSessionRequest;
import com.tickex.eventservice.model.dto.backoffice.UpdateEventSessionRequest;
import com.tickex.eventservice.model.dto.EventSessionSummaryDto;

import java.util.List;
import java.util.UUID;

public interface EventSessionService {

    List<EventSessionSummaryDto> getSessionsByEvent(UUID eventId);

    EventSessionSummaryDto createSession(CreateEventSessionRequest request, UUID currentUserId);

    EventSessionSummaryDto updateSession(UUID sessionId, UpdateEventSessionRequest request, UUID currentUserId);
}
