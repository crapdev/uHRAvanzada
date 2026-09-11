package com.riwi.Eventify.controller;

import com.riwi.Eventify.model.Venue;
import com.riwi.Eventify.service.VenueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//con esto Swagger agrupa los endpoints como Venues
@Tag(name = "Venues", description = "Gestion de lugares de Eventify")
@RestController
@RequestMapping("/api/venues")
public class VenueController {
    
    private final VenueService venueService;

    public VenueController(VenueService venueService){ this.venueService = venueService;}

    @Operation(summary = "Registrar un nuevo lugar")
    @PostMapping
    public ResponseEntity<Venue> create(@RequestBody Venue venue){
        Venue createdVenue = venueService.create(venue);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVenue);
    }

    @Operation(summary = "Obtener lugares")
    @GetMapping
    public ResponseEntity<List<Venue>> listAll(){
        List<Venue> venues = venueService.listAll();
        return ResponseEntity.ok(venues);
    }
}
