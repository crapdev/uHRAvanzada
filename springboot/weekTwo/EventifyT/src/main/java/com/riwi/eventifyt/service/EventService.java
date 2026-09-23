package com.riwi.eventifyt.service;

import com.riwi.eventifyt.DTO.EventRequest;
import com.riwi.eventifyt.DTO.EventResponse;
import com.riwi.eventifyt.domain.Event;
import com.riwi.eventifyt.exception.InvalidException;
import com.riwi.eventifyt.exception.ResourceNotFoundException;
import com.riwi.eventifyt.mapper.EventMapper;
import com.riwi.eventifyt.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;


    public EventResponse create(EventRequest request){
        validate(request);
        Event event = eventMapper.toEntity(request);
        Event saved = eventRepository.save(event);
        return eventMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<EventResponse> listAll() {
        return eventRepository.findAll().stream()
                .map(eventMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public EventResponse findById( Long id ){
        Event event = findEventOrThrow(id);
        return eventMapper.toResponse(event);
    }

    public EventResponse update(Long id, EventRequest request){
        validate(request);
        Event event = findEventOrThrow(id); // 404 si no existe
        eventMapper.updateEntity(event, request); // pisa los campos sobre la entidad ya persistida
        Event updated = eventRepository.save(event);
        return  eventMapper.toResponse(updated);
    }

    public void delete(Long id){
        Event event = findEventOrThrow(id); // valida que exista antes de borrar
        eventRepository.delete(event);
    }

    /* -- Privates --*/
    private Event findEventOrThrow(Long id){
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontro el evento con el id " + id));
    }

    private void validate(EventRequest request) {
        if (request.name() == null || request.name().isBlank()) {
            throw new InvalidException("El nombre del evento no puede estar vacío");
        }
    }



}
