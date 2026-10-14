package com.riwi.eventifyt.mapper;

import com.riwi.eventifyt.domain.Category;
import com.riwi.eventifyt.domain.Venue;
import com.riwi.eventifyt.exception.ResourceNotFoundException;
import com.riwi.eventifyt.repository.CategoryRepository;
import com.riwi.eventifyt.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

// Ayudante de MapStruct: convierte IDS en ENTIDADES buscandolas en la base de datos.
// EventMapper lo "usa" (uses = ReferenceMapper.class): cuando MapStruct necesita pasar
// de Long → Venue o de Set<Long> → Set<Category>, llama solo a estos metodos.
@Component
@RequiredArgsConstructor
public class ReferenceMapper {

    private final VenueRepository venueRepository;
    private final CategoryRepository categoryRepository;

    // venueId → Venue
    public Venue toVenue(Long venueId) {
        Optional<Venue> venue = venueRepository.findById(venueId);
        if (venue.isEmpty()) {
            throw new ResourceNotFoundException("El lugar con ID " + venueId + " no existe");
        }
        return venue.get();
    }

    // categoryIds → categorias
    public Set<Category> toCategories(Set<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return new HashSet<>();
        }
        List<Category> categories = categoryRepository.findAllById(categoryIds);
        if (categories.size() != categoryIds.size()) {
            throw new ResourceNotFoundException("Alguna de las categorías indicadas no existe");
        }
        return new HashSet<>(categories);
    }
}
