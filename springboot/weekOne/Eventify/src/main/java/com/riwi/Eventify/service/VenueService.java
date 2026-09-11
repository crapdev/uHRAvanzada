package com.riwi.Eventify.service;

import com.riwi.Eventify.exception.InvalidVenueException;
import com.riwi.Eventify.model.Venue;
import com.riwi.Eventify.repository.VenueRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VenueService {
    private final VenueRepository venueRepository;

    public VenueService(VenueRepository venueRepository){ this.venueRepository = venueRepository;}

    public Venue create(Venue venue){

        if (venue.getName() == null || venue.getName().isBlank()){ throw new InvalidVenueException("El nombre del lugar no puede estar vacio");}
        return venueRepository.save(venue);
    }

    public List<Venue> listAll(){return venueRepository.listVenues();}
}
