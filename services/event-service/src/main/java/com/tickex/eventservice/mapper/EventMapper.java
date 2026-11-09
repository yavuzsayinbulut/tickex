package com.tickex.eventservice.mapper;

import com.tickex.eventservice.model.dto.*;
import com.tickex.eventservice.model.entity.EventEntity;
import com.tickex.eventservice.model.entity.VenueEntity;

import java.util.List;

public final class EventMapper {

    private EventMapper() {
    }

    public static EventBasicDto toBasicDto(EventEntity event) {
        if (event == null) return null;

        return EventBasicDto.builder()
                .id(event.getId())
                .slug(event.getSlug())
                .title(event.getTitle())
                .description(event.getDescription())
                .category(event.getCategory().name())
                .status(event.getStatus().name())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .minPrice(event.getMinPrice())
                .maxPrice(event.getMaxPrice())
                .build();
    }

    public static EventListItemDto toListItemDto(EventEntity event, VenueEntity venue) {
        return EventListItemDto.builder()
                .id(event.getId())
                .slug(event.getSlug())
                .title(event.getTitle())
                .category(event.getCategory().name())
                .city(venue != null ? venue.getCity() : null)
                .venueName(venue != null ? venue.getName() : null)
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .minPrice(event.getMinPrice())
                .maxPrice(event.getMaxPrice())
                .featured(event.isFeatured())
                .build();
    }

    public static EventDetailDto toDetailDto(EventEntity event,
                                             VenueEntity venue,
                                             List<EventSessionSummaryDto> sessions,
                                             List<String> tags) {
        return EventDetailDto.builder()
                .event(toBasicDto(event))
                .venue(VenueMapper.toDto(venue))
                .sessions(sessions)
                .tags(tags)
                .build();
    }
}
