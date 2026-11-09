package com.tickex.eventservice.model.dto.backoffice;

import com.tickex.eventservice.model.enums.EventCategory;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class UpdateEventRequest {

    @Size(max = 255)
    private String title;

    @Size(max = 5000)
    private String description;

    private EventCategory category;

    private UUID venueId;

    private LocalDate startDate;

    private LocalDate endDate;

    private List<String> tags;

    private Boolean featured;
}
