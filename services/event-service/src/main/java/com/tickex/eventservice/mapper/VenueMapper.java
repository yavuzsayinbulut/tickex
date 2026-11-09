package com.tickex.eventservice.mapper;

import com.tickex.eventservice.model.dto.VenueDto;
import com.tickex.eventservice.model.entity.VenueEntity;

public final class VenueMapper {

    private VenueMapper() {
    }

    public static VenueDto toDto(VenueEntity entity) {
        if (entity == null) return null;

        return VenueDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .city(entity.getCity())
                .country(entity.getCountry())
                .addressLine(entity.getAddressLine())
                .build();
    }
}
