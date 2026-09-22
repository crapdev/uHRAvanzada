package com.riwi.eventifyt.service;

import com.riwi.eventifyt.DTO.EventRequest;
import com.riwi.eventifyt.DTO.EventResponse;
import com.riwi.eventifyt.domain.Event;
import com.riwi.eventifyt.exception.InvalidException;
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


}
