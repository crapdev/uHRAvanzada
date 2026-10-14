package com.riwi.eventifyt.mapper;

import com.riwi.eventifyt.DTO.EventCreateDTO;
import com.riwi.eventifyt.DTO.EventResponseDTO;
import com.riwi.eventifyt.domain.Category;
import com.riwi.eventifyt.domain.Event;
import com.riwi.eventifyt.domain.Venue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

// Prueba la clase que GENERA MapStruct (EventMapperImpl). Solo levantamos ese bean;
// el ReferenceMapper (que va a la base de datos) se reemplaza por un mock
@SpringBootTest(classes = EventMapperImpl.class)
class EventMapperTest {

    @Autowired
    private EventMapper eventMapper;

    @MockBean
    private ReferenceMapper referenceMapper;

    private final Venue venue = Venue.builder().id(1L).name("Movistar Arena").address("Diagonal 61C").city("Bogotá").capacity(14000).build();
    private final Category rock = Category.builder().id(8L).name("Rock").build();
    private final Category conciertos = Category.builder().id(1L).name("Conciertos").build();

    // Escenario 6: venueId y categoryIds se resuelven a entidades
    @Test
    void toEntity_ResolvesVenueAndCategoriesFromIds() {
        EventCreateDTO dto = new EventCreateDTO(null, "Concierto", LocalDate.of(2026, 12, 1), "Desc", 1L, Set.of(1L, 8L));
        when(referenceMapper.toVenue(1L)).thenReturn(venue);
        when(referenceMapper.toCategories(Set.of(1L, 8L))).thenReturn(Set.of(conciertos, rock));

        Event event = eventMapper.toEntity(dto);

        assertNull(event.getId());
        assertTrue(event.isActive());
        assertEquals("Concierto", event.getName());
        assertSame(venue, event.getVenue());
        assertEquals(Set.of(conciertos, rock), event.getCategories());
    }

    // Escenarios 1 y 6: la salida tiene los nombres "planos"
    @Test
    void toResponseDTO_FlattensVenueAndCategoryNames() {
        Event event = Event.builder().id(5L).name("Concierto").date(LocalDate.of(2026, 12, 1))
                .venue(venue).categories(Set.of(rock, conciertos)).build();

        EventResponseDTO response = eventMapper.toResponseDTO(event);

        assertEquals(5L, response.id());
        assertEquals("Movistar Arena", response.venueName());
        assertEquals("Bogotá", response.city());
        assertEquals(List.of("Conciertos", "Rock"), response.categoryNames()); // ordenadas
    }

    @Test
    void toCreateDTO_ExtractsIdsForTheEditForm() {
        Event event = Event.builder().id(5L).name("Concierto").date(LocalDate.of(2026, 12, 1))
                .venue(venue).categories(Set.of(rock)).build();

        EventCreateDTO dto = eventMapper.toCreateDTO(event);

        assertEquals(1L, dto.getVenueId());
        assertEquals(Set.of(8L), dto.getCategoryIds());
    }
}
