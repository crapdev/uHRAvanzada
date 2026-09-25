package com.riwi.eventifyt.repository;

import com.riwi.eventifyt.domain.Event;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Test
    void save_ValidEvent_PersistsAndGeneratesId() {
        // Arrange
        Event event = Event.builder()
                .name("Test de integración")
                .date(LocalDate.of(2026, 12, 1))
                .description("Evento creado desde el test")
                .build();

        // Act
        Event saved = eventRepository.save(event);

        // Assert
        assertNotNull(saved.getId());  // la BD real generó el id

        Optional<Event> found = eventRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Test de integración", found.get().getName());
    }

    @Test
    void findByNameContainingIgnoreCase_MatchingEvents_ReturnsThem() {
        // Arrange
        eventRepository.save(Event.builder()
                .name("Conferencia Java")
                .date(LocalDate.of(2026, 10, 20))
                .description("desc")
                .build());
        eventRepository.save(Event.builder()
                .name("Taller de PYTHON")
                .date(LocalDate.of(2026, 11, 5))
                .description("desc")
                .build());

        // Act: probamos la consulta derivada del Bloque 3
        List<Event> result = eventRepository.findByNameContainingIgnoreCase("java");

        // Assert
        assertEquals(1, result.size());
        assertEquals("Conferencia Java", result.get(0).getName());
    }
}