package com.riwi.eventifyt.service;


import com.riwi.eventifyt.DTO.PageResponse;
import com.riwi.eventifyt.DTO.VenueRequest;
import com.riwi.eventifyt.DTO.VenueResponse;
import com.riwi.eventifyt.domain.Venue;
import com.riwi.eventifyt.exception.InvalidException;
import com.riwi.eventifyt.exception.ResourceNotFoundException;
import com.riwi.eventifyt.mapper.VenueMapper;
import com.riwi.eventifyt.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class VenueService {
    private final VenueRepository venueRepository;
    private final VenueMapper venueMapper;

    public VenueResponse create(VenueRequest request){
        validate(request);
        Venue venue = venueMapper.toEntity(request);
        Venue saved = venueRepository.save(venue);
        return venueMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public VenueResponse findById(Long id) {
        Venue venue = findVenueOrThrow(id);
        return venueMapper.toResponse(venue);
    }

    @Transactional(readOnly = true)
    public List<VenueResponse> listAll() {
        return venueRepository.findAll().stream()
                .map(venueMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<VenueResponse> listAll(Pageable pageable) {
        Page<Venue> page = venueRepository.findAll(pageable);
        return PageResponse.from(page.map(venueMapper::toResponse));
    }

    public VenueResponse update(Long id, VenueRequest request) {
        validate(request);
        Venue venue = findVenueOrThrow(id);
        venueMapper.updateEntity(venue, request);
        Venue updated = venueRepository.save(venue); // probar el stwr quitando esta linea a ver si hibernate cambia los datos en la base de datos
        return venueMapper.toResponse(updated);
    }

    public void delete(Long id) {
        Venue venue = findVenueOrThrow(id);
        venueRepository.delete(venue);
    }

    /* --- privates ---*/

    private Venue findVenueOrThrow(Long id) {
        return venueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el lugar con id " + id));
    }

    private void validate(VenueRequest request) {
        if (request.name() == null || request.name().isBlank()) {
            throw new InvalidException("El nombre del lugar no puede estar vacío");
        }
    }
}

