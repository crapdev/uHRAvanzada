package com.riwi.eventifyt.service;

import com.riwi.eventifyt.DTO.EventFilter;
import com.riwi.eventifyt.DTO.EventRequest;
import com.riwi.eventifyt.DTO.EventResponse;
import com.riwi.eventifyt.DTO.EventSummaryDTO;
import com.riwi.eventifyt.domain.Category;
import com.riwi.eventifyt.domain.Event;
import com.riwi.eventifyt.domain.Venue;
import com.riwi.eventifyt.exception.InvalidException;
import com.riwi.eventifyt.exception.ResourceNotFoundException;
import com.riwi.eventifyt.mapper.EventMapper;
import com.riwi.eventifyt.repository.CategoryRepository;
import com.riwi.eventifyt.repository.EventRepository;
import com.riwi.eventifyt.repository.VenueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private VenueRepository venueRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private EventMapper eventMapper;
    @InjectMocks
    private EventService eventService;

    private EventRequest validRequest;
    private Venue venue;
    private Category category;

    @BeforeEach
    void setUp() {
        validRequest = new EventRequest("Conferencia java", LocalDate.of(2026, 10, 20), "description jajaj", 1L, Set.of(3L));
        venue = Venue.builder().id(1L).name("Plaza Mayor").address("Calle 41").city("Medellín").capacity(1000).build();
        category = Category.builder().id(3L).name("Conferencias").build();
    }

    @Test
    void create_validEvent_ReturnsSavedEvent() {
        // Arrange
        Event mappedEntity = Event.builder()
                .name("Conferencia java").date(LocalDate.of(2026, 10, 20)).venue(venue).build();
        Event savedEntity = Event.builder()
                .id(1L).name("Conferencia java").date(LocalDate.of(2026, 10, 20)).venue(venue).build();
        EventResponse expectedResponse = new EventResponse(1L, "Conferencia java", LocalDate.of(2026, 10, 20),
                "description jajaj", 1L, "Plaza Mayor", "Medellín", List.of());

        when(venueRepository.findById(1L)).thenReturn(Optional.of(venue));
        when(categoryRepository.findAllById(Set.of(3L))).thenReturn(List.of(category));
        when(eventMapper.toEntity(validRequest, venue, List.of(category))).thenReturn(mappedEntity);
        when(eventRepository.save(mappedEntity)).thenReturn(savedEntity);
        when(eventMapper.toResponse(savedEntity)).thenReturn(expectedResponse);

        // Act
        EventResponse result = eventService.create(validRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Conferencia java", result.name());
        verify(eventRepository, times(1)).save(mappedEntity);
    }

    @Test
    void create_UnknownVenue_ThrowsNotFound() {
        when(venueRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> eventService.create(validRequest));
        verify(eventRepository, never()).save(any());
    }

    @Test
    void create_EmptyName_ThrowsInvalidException() {
        EventRequest invalidRequest = new EventRequest("   ", LocalDate.of(2026, 10, 10), "Descripción", 1L, Set.of());

        assertThrows(InvalidException.class, () -> eventService.create(invalidRequest));
        verify(eventRepository, never()).save(any());
    }

    @Test
    void create_NullName_ThrowsInvalidException() {
        EventRequest invalidRequest = new EventRequest(null, LocalDate.of(2026, 10, 10), "Descripción", 1L, Set.of());

        assertThrows(InvalidException.class, () -> eventService.create(invalidRequest));
        verify(eventRepository, never()).save(any());
    }

    @Test
    void search_PassesFiltersToRepository() {
        EventSummaryDTO summary = new EventSummaryDTO(1L, "Concierto de ROCK", LocalDate.of(2026, 11, 21), "Movistar Arena", "Bogotá");
        when(eventRepository.search(null, "bog", "rock", null, null, null, PageRequest.of(0, 10)))
                .thenReturn(new SliceImpl<>(List.of(summary)));

        var result = eventService.search(new EventFilter(" ", "bog", "rock", null, null, null), 0, 10);

        assertEquals(1, result.getContent().size());
        assertEquals("Concierto de ROCK", result.getContent().get(0).name());
    }

    @Test
    void delete_ExistingEvent_DeactivatesInsteadOfDeleting() {
        Event event = Event.builder().id(1L).name("Evento").date(LocalDate.now()).venue(venue).build();
        when(eventRepository.findWithRelationsById(1L)).thenReturn(Optional.of(event));

        eventService.delete(1L);

        assertFalse(event.isActive());
        verify(eventRepository, never()).delete(any());
    }
}
