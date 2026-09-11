package com.riwi.Eventify.repository;

import com.riwi.Eventify.model.Event;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class EventRepository {

    private final List<Event> events = new ArrayList<>();
    private  Long currentId = 1L;

    public Event save (Event event){
        event.setId(currentId++);
        events.add(event);
        return event;
    }

    public List<Event> listEvents(){
        return events;
    }
}
