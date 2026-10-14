package com.riwi.eventifyt.repository;

import com.riwi.eventifyt.DTO.EventSummaryDTO;
import com.riwi.eventifyt.domain.Category;
import com.riwi.eventifyt.domain.Event;
import com.riwi.eventifyt.domain.Venue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

// Usa la BD real (PostgreSQL con las migraciones de Flyway). Cada test hace rollback al terminar
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EventRepositoryTest {

    private static final PageRequest FIRST_PAGE = PageRequest.of(0, 10);

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Venue venue;

    // Cada evento necesita un lugar (relación obligatoria)
    @BeforeEach
    void setUp() {
        venue = venueRepository.save(Venue.builder()
                .name("Lugar de test").address("Calle test").city("Villa Test").capacity(100)
                .build());
    }

    @Test
    void save_WithNewCategory_PersistsEventCategoryAndLinkInCascade() {
        Category category = Category.builder().name("Categoría test").description("desc").build();
        Event event = Event.builder()
                .name("Test de integración")
                .date(LocalDate.of(2026, 12, 1))
                .venue(venue)
                .categories(Set.of(category))
                .build();

        Event saved = eventRepository.save(event);
        entityManager.flush();
        entityManager.clear();

        assertNotNull(saved.getId());
        assertNotNull(category.getId());   // la categoría se insertó por cascade
        Optional<Event> result = eventRepository.findWithRelationsById(saved.getId());
        assertTrue(result.isPresent());
        Event found = result.get();
        assertEquals("Villa Test", found.getVenue().getCity());
        assertEquals(1, found.getCategories().size());
    }

    @Test
    void softDelete_HidesEventFromAllQueries() {
        Event event = eventRepository.save(Event.builder()
                .name("Evento a borrar").date(LocalDate.of(2026, 5, 5)).venue(venue).build());

        event.softDelete();
        entityManager.flush();
        entityManager.clear();

        assertTrue(eventRepository.findById(event.getId()).isEmpty());
        assertTrue(eventRepository.findByCity("villa test", FIRST_PAGE).getContent().isEmpty());
        // ...pero la fila sigue existiendo en la BD
        Object active = entityManager.getEntityManager()
                .createNativeQuery("SELECT active FROM events WHERE id = :id")
                .setParameter("id", event.getId())
                .getSingleResult();
        assertEquals(Boolean.FALSE, active);
    }

    @Test
    void search_PartialAndCaseInsensitive_FindsSeededRockConcert() {
        // Escenario 6: "Concierto de ROCK" en Bogotá (viene del seed V3)
        Slice<EventSummaryDTO> result = eventRepository.search(null, "bog", "rock", null, null, null, FIRST_PAGE);

        boolean foundRockConcert = false;
        for (EventSummaryDTO event : result.getContent()) {
            assertEquals("Bogotá", event.city());
            if (event.name().equals("Concierto de ROCK")) {
                foundRockConcert = true;
            }
        }
        assertTrue(foundRockConcert);
    }

    @Test
    void findAllSummaries_ReturnsSliceOrderedByDateDesc() {
        Slice<EventSummaryDTO> slice = eventRepository.findAllSummaries(PageRequest.of(0, 20));

        assertEquals(20, slice.getContent().size());
        assertTrue(slice.hasNext());
        List<EventSummaryDTO> events = slice.getContent();
        for (int i = 1; i < events.size(); i++) {
            assertFalse(events.get(i).date().isAfter(events.get(i - 1).date()), "Debe estar ordenado por fecha DESC");
        }
    }

    @Test
    void findByDateBetweenAndMinCapacity_ApplyFilters() {
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 3, 31);

        for (EventSummaryDTO event : eventRepository.findByDateBetween(from, to, FIRST_PAGE).getContent()) {
            assertFalse(event.date().isBefore(from));
            assertFalse(event.date().isAfter(to));
        }
        assertFalse(eventRepository.findByMinCapacity(10_000, FIRST_PAGE).getContent().isEmpty());
        assertTrue(eventRepository.findByMinCapacity(1_000_000, FIRST_PAGE).getContent().isEmpty());
    }

    @Test
    void findWithRelationsById_UnknownId_ReturnsEmpty() {
        Optional<Event> found = eventRepository.findWithRelationsById(-1L);
        assertTrue(found.isEmpty());
    }
}
