package com.tickex.eventservice.model.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class EventFilter {
    String city;
    String category;
    LocalDate dateFrom;
    LocalDate dateTo;
    String search;
}
