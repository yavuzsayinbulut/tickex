package com.tickex.eventservice.service;

import com.tickex.eventservice.model.dto.backoffice.CreateEventRequest;
import com.tickex.eventservice.model.dto.backoffice.UpdateEventRequest;
import com.tickex.eventservice.model.dto.EventDetailDto;
import com.tickex.eventservice.model.dto.EventFilter;
import com.tickex.eventservice.model.dto.EventListItemDto;
import com.tickex.eventservice.model.enums.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface EventService {

    EventDetailDto getEventDetail(UUID eventId);

    Page<EventListItemDto> searchEvents(EventFilter filter, Pageable pageable);

    EventDetailDto createEvent(CreateEventRequest request, UUID organizerId);

    EventDetailDto updateEvent(UUID eventId, UpdateEventRequest request, UUID currentUserId);

    void changeStatus(UUID eventId, EventStatus newStatus, UUID currentUserId);

    List<EventListItemDto> getFeaturedEvents();
}
