package com.riwi.eventifyt.service;


import com.riwi.eventifyt.DTO.PageResponse;
import com.riwi.eventifyt.DTO.VenueCreateDTO;
import com.riwi.eventifyt.DTO.VenueResponseDTO;
import com.riwi.eventifyt.domain.Venue;
import com.riwi.eventifyt.exception.BusinessRuleViolationException;
import com.riwi.eventifyt.exception.DuplicateResourceException;
import com.riwi.eventifyt.exception.ResourceNotFoundException;
import com.riwi.eventifyt.mapper.VenueMapper;
import com.riwi.eventifyt.repository.EventRepository;
import com.riwi.eventifyt.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class VenueService {
    private final VenueRepository venueRepository;
    private final EventRepository eventRepository;
    private final VenueMapper venueMapper;

    public VenueResponseDTO create(VenueCreateDTO dto) {
        if (venueRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new DuplicateResourceException("Ya existe un lugar con el nombre '" + dto.getName() + "'");
        }
        Venue saved = venueRepository.save(venueMapper.toEntity(dto));
        return venueMapper.toResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public VenueResponseDTO findById(Long id) {
        return venueMapper.toResponseDTO(findVenueOrThrow(id));
    }

    // Datos actuales del lugar en forma de DTO de entrada (para el formulario "Editar" del panel)
    @Transactional(readOnly = true)
    public VenueCreateDTO findForEdit(Long id) {
        return venueMapper.toCreateDTO(findVenueOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<VenueResponseDTO> listAll() {
        List<VenueResponseDTO> result = new ArrayList<>();
        for (Venue venue : venueRepository.findAll(Sort.by("name"))) {   // ORDER BY name ASC
            result.add(venueMapper.toResponseDTO(venue));
        }
        return result;
    }

    @Transactional(readOnly = true)
    public PageResponse<VenueResponseDTO> listAll(Pageable pageable) {
        Page<Venue> page = venueRepository.findAll(pageable);
        List<VenueResponseDTO> content = new ArrayList<>();
        for (Venue venue : page.getContent()) {
            content.add(venueMapper.toResponseDTO(venue));
        }
        return new PageResponse<>(content, page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages(), page.isLast());
    }

    public VenueResponseDTO update(Long id, VenueCreateDTO dto) {
        if (dto.getId() != null && !dto.getId().equals(id)) {
            throw new BusinessRuleViolationException(
                    "El id del cuerpo (" + dto.getId() + ") no coincide con el de la URL (" + id + ")");
        }
        Venue venue = findVenueOrThrow(id);
        if (venueRepository.existsByNameIgnoreCaseAndIdNot(dto.getName(), id)) {
            throw new DuplicateResourceException("Ya existe otro lugar con el nombre '" + dto.getName() + "'");
        }
        venueMapper.updateEntity(dto, venue);
        return venueMapper.toResponseDTO(venueRepository.save(venue));
    }

    public void delete(Long id) {
        Venue venue = findVenueOrThrow(id);
        // Regla de negocio: no se puede borrar un lugar que tiene eventos vinculados
        if (eventRepository.existsAnyByVenueId(id)) {
            throw new BusinessRuleViolationException(
                    "No se puede eliminar el lugar '" + venue.getName() + "' porque tiene eventos vinculados");
        }
        venueRepository.delete(venue);
    }

    /* --- Privates --- */
    private Venue findVenueOrThrow(Long id) {
        Optional<Venue> venue = venueRepository.findById(id);
        if (venue.isEmpty()) {
            throw new ResourceNotFoundException("El lugar con ID " + id + " no existe");
        }
        return venue.get();
    }
}
