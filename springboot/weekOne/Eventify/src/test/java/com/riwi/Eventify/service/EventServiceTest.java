package com.riwi.Eventify.service;

import com.riwi.Eventify.exception.InvalidEventException;
import com.riwi.Eventify.model.Event;
import com.riwi.Eventify.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;
    @InjectMocks
    private EventService eventService;

    private Event validEvent;


    @BeforeEach // ejecuta este metodo antes de cada prueba
    void setUp() {
        validEvent = new Event(null, "Conferencia java", LocalDate.of(2026, 10, 20), "description jajaj");
    }

    // al hacer una prueba se organiza con la estructura AAA  Arrange-preparar |  Act-Actuar  Y Assert-verificar
    @Test
    void save_validEvent_ReturnSavedEvent(){
        // Arrange
        Event savedMock = new Event(1L, "Conferencia java", LocalDate.of(2026, 10, 20), "description jajaj");
        when(eventRepository.save(validEvent)).thenReturn(savedMock);

        // Act
        Event result = eventService.create(validEvent);

        // Assert-verificar
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Conferencia java", result.getName()); // Misma coincidencia exacta
        verify(eventRepository, times(1)).save(validEvent);
    }
    @Test
    void save_EmptyName_ThrowsInvalidDataException() {
        // Arrange (Preparar)
        Event invalidEvent = new Event(null, "   ", LocalDate.of(2026,10,10), "Descripción");

        // Act & Assert (Actuar y Verificar)
        assertThrows(InvalidEventException.class, () -> eventService.create(invalidEvent));
        verify(eventRepository, never()).save(any());
    }

    @Test
    void save_NullName_ThrowsInvalidDataException() {
        // Arrange (Preparar)
        Event invalidEvent = new Event(null, null, LocalDate.of(2026,10,10), "Descripción");

        // Act & Assert (Actuar y Verificar)
        assertThrows(InvalidEventException.class, () -> eventService.create(invalidEvent));
        verify(eventRepository, never()).save(any());
    }

    @Test
    void findAll_ReturnsListOfEvents() {
        // Arrange (Preparar)
        List<Event> mockList = new ArrayList<>();
        mockList.add(new Event(1L, "Evento 1", LocalDate.of(2026,10,10), "Desc 1"));
        when(eventRepository.listEvents()).thenReturn(mockList);

        // Act (Actuar)
        List<Event> result = eventService.listAll();

        // Assert (Verificar)
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(eventRepository, times(1)).listEvents();
    }
}
