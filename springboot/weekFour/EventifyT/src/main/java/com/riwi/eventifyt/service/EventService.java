package com.riwi.eventifyt.service;

import com.riwi.eventifyt.DTO.EventFilter;
import com.riwi.eventifyt.DTO.EventRequest;
import com.riwi.eventifyt.DTO.EventResponse;
import com.riwi.eventifyt.DTO.EventSummaryDTO;
import com.riwi.eventifyt.domain.Category;
import com.riwi.eventifyt.domain.Event;
import com.riwi.eventifyt.domain.Venue;
import com.riwi.eventifyt.exception.InvalidException;
import com.riwi.eventifyt.exception.ResourceNotFoundException;
import com.riwi.eventifyt.mapper.EventMapper;
import com.riwi.eventifyt.repository.CategoryRepository;
import com.riwi.eventifyt.repository.EventRepository;
import com.riwi.eventifyt.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;

    public EventResponse create(EventRequest request) {
        validate(request);
        Event event = eventMapper.toEntity(request, findVenueOrThrow(request.venueId()), findCategories(request.categoryIds()));
        // Un solo save: el cascade inserta el evento y luego sus filas en events_categories
        Event saved = eventRepository.save(event);
        return eventMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public EventResponse findById(Long id) {
        Event event = findEventOrThrow(id);
        return eventMapper.toResponse(event);
    }

    // Catalogo paginado con Slice: una sola consulta, sin COUNT, ordenada por fecha DESC
    @Transactional(readOnly = true)
    public Slice<EventSummaryDTO> search(EventFilter filter, int page, int size) {
        EventFilter f = filter == null ? EventFilter.empty() : filter;
        return eventRepository.search(f.name(), f.city(), f.category(), f.minCapacity(), f.from(), f.to(),
                PageRequest.of(Math.max(page, 0), size));
    }

    public EventResponse update(Long id, EventRequest request) {
        validate(request);
        Event event = findEventOrThrow(id);
        eventMapper.updateEntity(event, request, findVenueOrThrow(request.venueId()), findCategories(request.categoryIds()));
        Event updated = eventRepository.save(event);
        return eventMapper.toResponse(updated);
    }

    // Soft delete: no hay DELETE fisico, solo un UPDATE active = false.
    // Desde ese momento @SQLRestriction lo oculta de todas las consultas
    public void delete(Long id) {
        Event event = findEventOrThrow(id);
        event.softDelete();
    }

    /* -- Privates -- */
    private Event findEventOrThrow(Long id) {
        return eventRepository.findWithRelationsById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontro el evento con el id " + id));
    }

    private Venue findVenueOrThrow(Long venueId) {
        return venueRepository.findById(venueId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el lugar con id " + venueId));
    }

    private List<Category> findCategories(Set<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return List.of();
        }
        List<Category> categories = categoryRepository.findAllById(categoryIds);
        if (categories.size() != categoryIds.size()) {
            throw new ResourceNotFoundException("Alguna de las categorías indicadas no existe");
        }
        return categories;
    }

    private void validate(EventRequest request) {
        if (request.name() == null || request.name().isBlank()) {
            throw new InvalidException("El nombre del evento no puede estar vacío");
        }
        if (request.venueId() == null) {
            throw new InvalidException("El evento debe tener un lugar asignado");
        }
    }
}
