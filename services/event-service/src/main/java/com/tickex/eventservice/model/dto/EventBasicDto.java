package com.tickex.eventservice.model.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Value
@Builder
public class EventBasicDto {
    UUID id;
    String slug;
    String title;
    String description;
    String category;
    String status;
    LocalDate startDate;
    LocalDate endDate;
    BigDecimal minPrice;
    BigDecimal maxPrice;
}
