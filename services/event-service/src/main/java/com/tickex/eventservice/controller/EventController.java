package com.tickex.eventservice.controller;

import com.tickex.eventservice.model.dto.EventDetailDto;
import com.tickex.eventservice.model.dto.EventFilter;
import com.tickex.eventservice.model.dto.EventListItemDto;
import com.tickex.eventservice.model.dto.EventSessionSummaryDto;
import com.tickex.eventservice.service.EventService;
import com.tickex.eventservice.service.EventSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/events")
public class EventController {

    private final EventService eventService;
    private final EventSessionService eventSessionService;

    @GetMapping
    public Page<EventListItemDto> listEvents(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) LocalDate dateFrom,
            @RequestParam(required = false) LocalDate dateTo,
            @RequestParam(required = false) String search,
            Pageable pageable
    ) {
        EventFilter filter = EventFilter.builder()
                .city(city)
                .category(category)
                .dateFrom(dateFrom)
                .dateTo(dateTo)
                .search(search)
                .build();

        return eventService.searchEvents(filter, pageable);
    }

    @GetMapping("/{eventId}")
    public EventDetailDto getEvent(@PathVariable UUID eventId) {
        return eventService.getEventDetail(eventId);
    }

    @GetMapping("/{eventId}/sessions")
    public List<EventSessionSummaryDto> getSessions(@PathVariable UUID eventId) {
        return eventSessionService.getSessionsByEvent(eventId);
    }

    @GetMapping("/featured")
    public List<EventListItemDto> getFeaturedEvents() {
        return eventService.getFeaturedEvents();
    }
}
