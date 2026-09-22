package com.riwi.eventifyt.mapper;

import com.riwi.eventifyt.DTO.EventRequest;
import com.riwi.eventifyt.DTO.EventResponse;
import com.riwi.eventifyt.domain.Event;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    // Request → Entity (para crear). Sin id: lo genera la BD
    public Event toEntity(EventRequest request){
        return Event.builder()
                .name(request.name())
                .date(request.date())
                .description(request.description())
                .build();
    }
    // Entity → Response (para devolver al cliente)
    public EventResponse toResponse(Event event){
        return new EventResponse(
                event.getId(),
                event.getName(),
                event.getDate(),
                event.getDescription()
        );
    }

    // Copia los datos del Request sobre una entidad YA existente (para el PUT)
    public void updateEntity(Event event, EventRequest request) {
        event.setName(request.name());
        event.setDate(request.date());
        event.setDescription(request.description());
    }
}
