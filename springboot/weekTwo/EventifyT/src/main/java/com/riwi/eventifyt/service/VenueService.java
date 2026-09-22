package com.riwi.eventifyt.service;

import com.riwi.eventifyt.domain.Venue;
import com.riwi.eventifyt.exception.InvalidException;
import com.riwi.eventifyt.repository.VenueRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VenueService {
    private final VenueRepository venueRepository;

    public VenueService(VenueRepository venueRepository){ this.venueRepository = venueRepository;}

    public Venue create(Venue venue){

        if (venue.getName() == null || venue.getName().isBlank()){ throw new InvalidException("El nombre del lugar no puede estar vacio");}
        return venueRepository.save(venue);
    }

    public List<Venue> listAll(){ return venueRepository.findAll();}
}
