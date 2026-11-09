package com.tickex.eventservice.model.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Value
@Builder
public class EventListItemDto {
    UUID id;
    String slug;
    String title;
    String category;
    String city;
    String venueName;
    LocalDate startDate;
    LocalDate endDate;
    BigDecimal minPrice;
    BigDecimal maxPrice;
    boolean featured;
}
