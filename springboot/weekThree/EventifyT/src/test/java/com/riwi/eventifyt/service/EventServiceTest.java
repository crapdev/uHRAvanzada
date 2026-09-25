package com.riwi.eventifyt.service;

import com.riwi.eventifyt.DTO.EventRequest;
import com.riwi.eventifyt.DTO.EventResponse;
import com.riwi.eventifyt.domain.Event;
import com.riwi.eventifyt.exception.InvalidException;
import com.riwi.eventifyt.mapper.EventMapper;
import com.riwi.eventifyt.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;

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

    private EventRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new EventRequest("Conferencia java", LocalDate.of(2026, 10, 20), "description jajaj");
    }

    @Test
    void create_validEvent_ReturnsSavedEvent() {
        // Arrange
        Event mappedEntity = Event.builder()
                .name("Conferencia java").date(LocalDate.of(2026, 10, 20)).description("description jajaj").build();
        Event savedEntity = Event.builder()
                .id(1L).name("Conferencia java").date(LocalDate.of(2026, 10, 20)).description("description jajaj").build();
        EventResponse expectedResponse =
                new EventResponse(1L, "Conferencia java", LocalDate.of(2026, 10, 20), "description jajaj");

        when(eventMapper.toEntity(validRequest)).thenReturn(mappedEntity);
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
    void create_EmptyName_ThrowsInvalidException() {
        EventRequest invalidRequest = new EventRequest("   ", LocalDate.of(2026, 10, 10), "Descripción");

        assertThrows(InvalidException.class, () -> eventService.create(invalidRequest));
        verify(eventRepository, never()).save(any());
    }

    @Test
    void create_NullName_ThrowsInvalidException() {
        EventRequest invalidRequest = new EventRequest(null, LocalDate.of(2026, 10, 10), "Descripción");

        assertThrows(InvalidException.class, () -> eventService.create(invalidRequest));
        verify(eventRepository, never()).save(any());
    }

    @Test
    void listAll_ReturnsListOfEvents() {
        // Arrange
        Event event1 = Event.builder().id(1L).name("Evento 1").date(LocalDate.of(2026, 10, 10)).description("Desc 1").build();
        EventResponse response1 = new EventResponse(1L, "Evento 1", LocalDate.of(2026, 10, 10), "Desc 1");

        when(eventRepository.findAll(any(Sort.class))).thenReturn(List.of(event1));
        when(eventMapper.toResponse(event1)).thenReturn(response1);

        // Act
        List<EventResponse> result = eventService.listAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Evento 1", result.get(0).name());
        verify(eventRepository, times(1)).findAll(any(Sort.class));
    }
}