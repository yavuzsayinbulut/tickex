package com.tickex.eventservice.mapper;

import com.tickex.eventservice.model.dto.EventSessionSummaryDto;
import com.tickex.eventservice.model.entity.EventSessionEntity;

public final class EventSessionMapper {

    private EventSessionMapper() {
    }

    public static EventSessionSummaryDto toSummaryDto(EventSessionEntity entity) {
        if (entity == null) return null;

        return EventSessionSummaryDto.builder()
                .id(entity.getId())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .status(entity.getStatus().name())
                .minPrice(entity.getMinPrice())
                .maxPrice(entity.getMaxPrice())
                .build();
    }
}
