package com.riwi.eventifyt.mapper;

import com.riwi.eventifyt.DTO.VenueRequest;
import com.riwi.eventifyt.DTO.VenueResponse;
import com.riwi.eventifyt.domain.Venue;
import org.springframework.stereotype.Component;

@Component
public class VenueMapper {

    public Venue toEntity(VenueRequest request){
        return Venue.builder()
                .name(request.name())
                .address(request.address())
                .city(request.city())
                .capacity(request.capacity())
                .build();
    }

    public VenueResponse toResponse(Venue venue){
        return new VenueResponse(
                venue.getId(),
                venue.getName(),
                venue.getAddress(),
                venue.getCity(),
                venue.getCapacity()
        );
    }

    public void updateEntity(Venue venue, VenueRequest request){
        venue.setName(request.name());
        venue.setAddress(request.address());
        venue.setCity(request.city());
        venue.setCapacity(request.capacity());
    }
}
