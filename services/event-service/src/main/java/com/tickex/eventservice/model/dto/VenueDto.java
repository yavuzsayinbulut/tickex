package com.tickex.eventservice.model.dto;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class VenueDto {
    UUID id;
    String name;
    String city;
    String country;
    String addressLine;
}
