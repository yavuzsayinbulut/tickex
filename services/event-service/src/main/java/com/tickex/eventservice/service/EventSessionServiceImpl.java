package com.tickex.eventservice.service;

import com.tickex.eventservice.exception.AccessDeniedDomainException;
import com.tickex.eventservice.exception.ResourceNotFoundException;
import com.tickex.eventservice.mapper.EventSessionMapper;
import com.tickex.eventservice.model.dto.backoffice.CreateEventSessionRequest;
import com.tickex.eventservice.model.dto.backoffice.UpdateEventSessionRequest;
import com.tickex.eventservice.model.dto.EventSessionSummaryDto;
import com.tickex.eventservice.model.entity.EventEntity;
import com.tickex.eventservice.model.entity.EventSessionEntity;
import com.tickex.eventservice.repository.EventRepository;
import com.tickex.eventservice.repository.EventSessionRepository;
import com.tickex.eventservice.service.EventSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventSessionServiceImpl implements EventSessionService {

    private final EventRepository eventRepository;
    private final EventSessionRepository eventSessionRepository;

    @Override
    public List<EventSessionSummaryDto> getSessionsByEvent(UUID eventId) {
        return eventSessionRepository.findByEventIdOrderByStartTimeAsc(eventId)
                .stream()
                .map(EventSessionMapper::toSummaryDto)
                .toList();
    }

    @Override
    public EventSessionSummaryDto createSession(CreateEventSessionRequest request, UUID currentUserId) {
        EventEntity event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + request.getEventId()));

        validateOwnership(event, currentUserId);

        EventSessionEntity entity = EventSessionEntity.builder()
                .eventId(event.getId())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .doorOpenTime(request.getDoorOpenTime())
                .status(request.getStatus())
                .salesStartTime(request.getSalesStartTime())
                .salesEndTime(request.getSalesEndTime())
                .minPrice(request.getMinPrice())
                .maxPrice(request.getMaxPrice())
                .build();

        EventSessionEntity saved = eventSessionRepository.save(entity);
        return EventSessionMapper.toSummaryDto(saved);
    }

    @Override
    public EventSessionSummaryDto updateSession(UUID sessionId, UpdateEventSessionRequest request, UUID currentUserId) {
        EventSessionEntity session = eventSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found: " + sessionId));

        EventEntity event = eventRepository.findById(session.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + session.getEventId()));

        validateOwnership(event, currentUserId);

        if (request.getStartTime() != null) {
            session.setStartTime(request.getStartTime());
        }
        if (request.getEndTime() != null) {
            session.setEndTime(request.getEndTime());
        }
        if (request.getDoorOpenTime() != null) {
            session.setDoorOpenTime(request.getDoorOpenTime());
        }
        if (request.getStatus() != null) {
            session.setStatus(request.getStatus());
        }
        if (request.getSalesStartTime() != null) {
            session.setSalesStartTime(request.getSalesStartTime());
        }
        if (request.getSalesEndTime() != null) {
            session.setSalesEndTime(request.getSalesEndTime());
        }
        if (request.getMinPrice() != null) {
            session.setMinPrice(request.getMinPrice());
        }
        if (request.getMaxPrice() != null) {
            session.setMaxPrice(request.getMaxPrice());
        }

        EventSessionEntity saved = eventSessionRepository.save(session);
        return EventSessionMapper.toSummaryDto(saved);
    }

    private void validateOwnership(EventEntity event, UUID currentUserId) {
        // ADMIN için ayrı kontrol Security tarafında yapılabilir.
        if (!Objects.equals(event.getOrganizerId(), currentUserId)) {
            throw new AccessDeniedDomainException("You are not the organizer of this event");
        }
    }
}
