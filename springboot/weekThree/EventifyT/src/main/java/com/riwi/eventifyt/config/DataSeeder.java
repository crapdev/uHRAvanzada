package com.riwi.eventifyt.config;


import com.riwi.eventifyt.domain.Event;
import com.riwi.eventifyt.domain.Venue;
import com.riwi.eventifyt.repository.EventRepository;
import com.riwi.eventifyt.repository.VenueRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Configuration
public class DataSeeder {
    // CommandLineRunner: Spring ejecuta este código UNA vez, justo después de arrancar
    @Bean
    CommandLineRunner seedData(EventRepository eventRepository, VenueRepository venueRepository) {
        return args -> {
            // Solo insertamos si la tabla está vacía → no se duplica en cada reinicio
            if (eventRepository.count() == 0) {
                eventRepository.saveAll(buildFiftyEvents());
            }

            /*if (eventRepository.count() == 0) {
                eventRepository.save(Event.builder()      // sin id: lo genera la BD
                        .name("Evento de prueba")
                        .date(LocalDate.of(2026, 10, 20))
                        .description("Evento inicial de Eventify")
                        .build());
            }*/

            if (venueRepository.count() == 0) {
                venueRepository.save(Venue.builder()
                        .name("Centro de Convenciones")
                        .address("Calle 50 # 10-20")
                        .capacity(500)
                        .build());
            }
        };
    }
        // Genera 50 eventos de prueba: "Evento 1" ... "Evento 50"
        private List<Event> buildFiftyEvents() {
            List<Event> events = new ArrayList<>();
            for (int i = 1; i <= 50; i++) {
                events.add(Event.builder()
                        .name("Evento " + i)
                        .date(LocalDate.of(2026, 1, 1).plusDays(i))
                        .description("Descripción del evento número " + i)
                        .build());
            }
            return events;
        }

}
