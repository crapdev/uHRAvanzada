package com.riwi.Eventify.service;

import com.riwi.Eventify.exception.InvalidEventException;
import com.riwi.Eventify.model.Event;
import com.riwi.Eventify.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository){
        this.eventRepository = eventRepository;
    }

    public Event create(Event event){

        if (event.getName() == null || event.getName().isBlank()){ throw new InvalidEventException("El nombre del evento no puede estar vacio"); }
        return eventRepository.save(event);

    }

    public List<Event> listAll(){ return eventRepository.listEvents();}
}
