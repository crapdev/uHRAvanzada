package com.riwi.eventifyt.service;

import com.riwi.eventifyt.DTO.EventCreateDTO;
import com.riwi.eventifyt.DTO.EventFilter;
import com.riwi.eventifyt.DTO.EventResponseDTO;
import com.riwi.eventifyt.DTO.EventSummaryDTO;
import com.riwi.eventifyt.domain.Event;
import com.riwi.eventifyt.exception.BusinessRuleViolationException;
import com.riwi.eventifyt.exception.ResourceNotFoundException;
import com.riwi.eventifyt.mapper.EventMapper;
import com.riwi.eventifyt.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

// Recibe DTOs y devuelve DTOs: la entidad Event nunca sale de esta capa.
// Las validaciones de datos (nombre vacio, fecha pasada...) ya las hizo @Valid en el controlador.
@Service
@Transactional
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    public EventResponseDTO create(EventCreateDTO dto) {
        // MapStruct convierte el DTO en entidad y resuelve venueId → Venue y categoryIds → categorias
        Event event = eventMapper.toEntity(dto);
        // Un solo save: el cascade inserta el evento y luego sus filas en events_categories
        Event saved = eventRepository.save(event);
        return eventMapper.toResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public EventResponseDTO findById(Long id) {
        return eventMapper.toResponseDTO(findEventOrThrow(id));
    }

    // Datos actuales del evento en forma de DTO de entrada (para el formulario "Editar" del panel)
    @Transactional(readOnly = true)
    public EventCreateDTO findForEdit(Long id) {
        return eventMapper.toCreateDTO(findEventOrThrow(id));
    }

    // Catalogo paginado con Slice: sigue usando el record EventSummaryDTO de la semana 4
    @Transactional(readOnly = true)
    public Slice<EventSummaryDTO> search(EventFilter filter, int page, int size) {
        EventFilter f = filter == null ? EventFilter.empty() : filter;
        return eventRepository.search(f.name(), f.city(), f.category(), f.minCapacity(), f.from(), f.to(),
                PageRequest.of(Math.max(page, 0), size));
    }

    public EventResponseDTO update(Long id, EventCreateDTO dto) {
        // Regla de negocio: si el body trae id, debe ser el mismo de la URL
        if (dto.getId() != null && !dto.getId().equals(id)) {
            throw new BusinessRuleViolationException(
                    "El id del cuerpo (" + dto.getId() + ") no coincide con el de la URL (" + id + ")");
        }
        Event event = findEventOrThrow(id);
        eventMapper.updateEntity(dto, event);
        return eventMapper.toResponseDTO(eventRepository.save(event));
    }

    // Soft delete: no hay DELETE fisico, solo un UPDATE active = false.
    // Desde ese momento @SQLRestriction lo oculta de todas las consultas
    public void delete(Long id) {
        findEventOrThrow(id).softDelete();
    }

    /* -- Privates -- */
    private Event findEventOrThrow(Long id) {
        Optional<Event> event = eventRepository.findWithRelationsById(id);
        if (event.isEmpty()) {
            throw new ResourceNotFoundException("El evento con ID " + id + " no existe");
        }
        return event.get();
    }
}
