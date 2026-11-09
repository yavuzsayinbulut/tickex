package com.tickex.eventservice.model.dto.backoffice;

import com.tickex.eventservice.model.enums.EventCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class CreateEventRequest {

    @NotBlank
    @Size(max = 255)
    private String title;

    @Size(max = 5000)
    private String description;

    @NotNull
    private EventCategory category;

    @NotNull
    private UUID venueId;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    private List<String> tags;

    private boolean featured;
}
