package com.riwi.Eventify.service;

import com.riwi.Eventify.model.Event;
import com.riwi.Eventify.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
}
