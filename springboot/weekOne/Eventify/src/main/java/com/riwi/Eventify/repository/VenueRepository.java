package com.riwi.Eventify.repository;


import com.riwi.Eventify.model.Event;
import com.riwi.Eventify.model.Venue;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class VenueRepository {
    private final List<Venue> venues = new ArrayList<>();
    private  Long currentId = 1L;

    public Venue save (Venue venue){
        venue.setId(currentId++);
        venues.add(venue);
        return venue;
    }

    public List<Venue> listVenues(){
        return venues;
    }


}
