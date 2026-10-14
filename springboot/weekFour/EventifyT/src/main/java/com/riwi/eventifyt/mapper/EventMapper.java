package com.riwi.eventifyt.mapper;

import com.riwi.eventifyt.DTO.CategoryResponse;
import com.riwi.eventifyt.DTO.EventRequest;
import com.riwi.eventifyt.DTO.EventResponse;
import com.riwi.eventifyt.domain.Category;
import com.riwi.eventifyt.domain.Event;
import com.riwi.eventifyt.domain.Venue;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

@Component
public class EventMapper {

    // Request → Entity (para crear). Sin id: lo genera la BD.
    // El lugar y las categorias ya vienen cargados desde el servicio
    public Event toEntity(EventRequest request, Venue venue, List<Category> categories) {
        return Event.builder()
                .name(request.name())
                .date(request.date())
                .description(request.description())
                .venue(venue)
                .categories(new HashSet<>(categories))
                .build();
    }

    // Entity → Response (para devolver al cliente)
    public EventResponse toResponse(Event event) {
        Venue venue = event.getVenue();
        List<CategoryResponse> categories = event.getCategories().stream()
                .sorted(Comparator.comparing(Category::getName))
                .map(this::toCategoryResponse)
                .toList();
        return new EventResponse(
                event.getId(),
                event.getName(),
                event.getDate(),
                event.getDescription(),
                venue.getId(),
                venue.getName(),
                venue.getCity(),
                categories
        );
    }

    // Copia los datos del Request sobre una entidad YA existente (para el PUT)
    public void updateEntity(Event event, EventRequest request, Venue venue, List<Category> categories) {
        event.setName(request.name());
        event.setDate(request.date());
        event.setDescription(request.description());
        event.setVenue(venue);
        // Reemplazamos el contenido de la coleccion (no la coleccion) para que Hibernate sincronice events_categories
        event.getCategories().clear();
        event.getCategories().addAll(categories);
    }

    public CategoryResponse toCategoryResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getDescription());
    }
}
