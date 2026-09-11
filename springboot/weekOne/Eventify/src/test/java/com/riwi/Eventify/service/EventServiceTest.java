package com.riwi.Eventify.service;

import com.riwi.Eventify.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

public class EventServiceTest {
    private EventRepository eventRepository;
    private EventService eventService;

    @BeforeEach
    void setUp() {
        eventRepository = Mockito.mock(EventRepository.class);
        eventService = new EventService(eventRepository);
    }
}
