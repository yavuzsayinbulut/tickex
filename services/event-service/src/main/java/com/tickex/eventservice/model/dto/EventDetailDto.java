package com.tickex.eventservice.model.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class EventDetailDto {
    EventBasicDto event;
    VenueDto venue;
    List<EventSessionSummaryDto> sessions;
    List<String> tags;
}
