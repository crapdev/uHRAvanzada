package com.riwi.eventifyt.service;

import com.riwi.eventifyt.DTO.EventCreateDTO;
import com.riwi.eventifyt.DTO.EventFilter;
import com.riwi.eventifyt.DTO.EventResponseDTO;
import com.riwi.eventifyt.DTO.EventSummaryDTO;
import com.riwi.eventifyt.domain.Event;
import com.riwi.eventifyt.domain.Venue;
import com.riwi.eventifyt.exception.BusinessRuleViolationException;
import com.riwi.eventifyt.exception.ResourceNotFoundException;
import com.riwi.eventifyt.mapper.EventMapper;
import com.riwi.eventifyt.repository.EventRepository;
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
    private EventMapper eventMapper;
    @InjectMocks
    private EventService eventService;

    private EventCreateDTO validDto;
    private Venue venue;

    @BeforeEach
    void setUp() {
        validDto = new EventCreateDTO(null, "Conferencia java", LocalDate.now().plusDays(10), "description jajaj", 1L, Set.of(3L));
        venue = Venue.builder().id(1L).name("Plaza Mayor").address("Calle 41").city("Medellín").capacity(1000).build();
    }

    @Test
    void create_validEvent_ReturnsSavedEvent() {
        // Arrange
        Event mappedEntity = Event.builder().name("Conferencia java").date(validDto.getDate()).venue(venue).build();
        Event savedEntity = Event.builder().id(1L).name("Conferencia java").date(validDto.getDate()).venue(venue).build();
        EventResponseDTO expectedResponse = new EventResponseDTO(1L, "Conferencia java", validDto.getDate(),
                "description jajaj", "Plaza Mayor", "Medellín", List.of("Conferencias"));

        when(eventMapper.toEntity(validDto)).thenReturn(mappedEntity);
        when(eventRepository.save(mappedEntity)).thenReturn(savedEntity);
        when(eventMapper.toResponseDTO(savedEntity)).thenReturn(expectedResponse);

        // Act
        EventResponseDTO result = eventService.create(validDto);

        // Assert
        assertEquals(1L, result.id());
        assertEquals("Plaza Mayor", result.venueName());
        verify(eventRepository, times(1)).save(mappedEntity);
    }

    @Test
    void create_UnknownVenue_ThrowsNotFound() {
        // El mapper (ReferenceMapper por dentro) no encuentra el lugar
        when(eventMapper.toEntity(validDto)).thenThrow(new ResourceNotFoundException("El lugar con ID 1 no existe"));

        try {
            eventService.create(validDto);
            fail("Debió lanzar ResourceNotFoundException");
        } catch (ResourceNotFoundException expected) {
            // correcto: el lugar no existe
        }
        verify(eventRepository, never()).save(any());
    }

    @Test
    void findById_NonExisting_ThrowsNotFoundWithClearMessage() {
        when(eventRepository.findWithRelationsById(9999L)).thenReturn(Optional.empty());

        try {
            eventService.findById(9999L);
            fail("Debió lanzar ResourceNotFoundException");
        } catch (ResourceNotFoundException exception) {
            assertEquals("El evento con ID 9999 no existe", exception.getMessage());
        }
    }

    @Test
    void update_BodyIdDifferentFromUrlId_ThrowsBusinessRuleViolation() {
        validDto.setId(5L);

        try {
            eventService.update(1L, validDto);
            fail("Debió lanzar BusinessRuleViolationException");
        } catch (BusinessRuleViolationException expected) {
            // correcto: el id del body no coincide con el de la URL
        }
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
