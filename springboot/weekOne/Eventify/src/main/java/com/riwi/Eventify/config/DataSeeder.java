package com.riwi.Eventify.config;

import com.riwi.Eventify.model.Event;
import com.riwi.Eventify.model.Venue;
import com.riwi.Eventify.repository.EventRepository;
import com.riwi.Eventify.repository.VenueRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;


@Configuration
public class DataSeeder {
    @Bean
    public Event initialEvent(EventRepository eventRepository) {

        Event event = new Event(
                1L,
                "Evento de prueba",
                LocalDate.of(2026, 10, 20),
                "Evento inicial de Eventify"
        );

        return eventRepository.save(event);
    }

    @Bean
    public Venue initialVenue(VenueRepository venueRepository) {

        Venue venue = new Venue(
                1L,
                "Centro de Convenciones",
                "Calle 50 # 10-20",
                500
        );

        return venueRepository.save(venue);
    }
}
