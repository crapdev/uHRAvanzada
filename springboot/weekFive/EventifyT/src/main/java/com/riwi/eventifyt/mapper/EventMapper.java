package com.riwi.eventifyt.mapper;

import com.riwi.eventifyt.DTO.EventCreateDTO;
import com.riwi.eventifyt.DTO.EventResponseDTO;
import com.riwi.eventifyt.domain.Category;
import com.riwi.eventifyt.domain.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
 * Solo escribimos la INTERFAZ. Al compilar, MapStruct genera la clase EventMapperImpl
 * (en target/generated-sources) con todo el codigo de copiar campo por campo.
 *  - componentModel = "spring": la clase generada es un @Component → se puede inyectar
 *  - uses = ReferenceMapper.class: para pasar ids → entidades (venueId → Venue, categoryIds → Category)
 * Los campos que se llaman igual (name, date, description) se copian solos.
 * Con @Mapping configuramos los que NO se llaman igual.
 */
@Mapper(componentModel = "spring", uses = ReferenceMapper.class)
public interface EventMapper {

    // ---------- Entrada: DTO → Entidad ----------

    @Mapping(target = "id", ignore = true)                  // el id lo genera la base de datos
    @Mapping(target = "active", ignore = true)              // todo evento nace activo
    @Mapping(target = "venue", source = "venueId")          // usa ReferenceMapper.toVenue
    @Mapping(target = "categories", source = "categoryIds") // usa ReferenceMapper.toCategories
    Event toEntity(EventCreateDTO dto);

    // Igual que toEntity, pero copia los datos sobre un evento que YA existe (para actualizar)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "venue", source = "venueId")
    @Mapping(target = "categories", source = "categoryIds")
    void updateEntity(EventCreateDTO dto, @MappingTarget Event event);

    // ---------- Salida: Entidad → DTO ----------

    @Mapping(target = "venueName", source = "venue.name")     // event.getVenue().getName()
    @Mapping(target = "city", source = "venue.city")          // event.getVenue().getCity()
    @Mapping(target = "categoryNames", source = "categories") // usa toCategoryNames (abajo)
    EventResponseDTO toResponseDTO(Event event);

    // Para llenar el formulario de "Editar" del panel admin con los datos actuales
    @Mapping(target = "venueId", source = "venue.id")
    @Mapping(target = "categoryIds", source = "categories")   // usa toCategoryIds (abajo)
    EventCreateDTO toCreateDTO(Event event);

    // ---------- Conversiones a mano (MapStruct las usa cuando los tipos coinciden) ----------

    // Set<Category> → List<String> con los nombres ordenados alfabeticamente
    default List<String> toCategoryNames(Set<Category> categories) {
        List<String> names = new ArrayList<>();
        for (Category category : categories) {
            names.add(category.getName());
        }
        Collections.sort(names);
        return names;
    }

    // Set<Category> → Set<Long> con los ids
    default Set<Long> toCategoryIds(Set<Category> categories) {
        Set<Long> ids = new HashSet<>();
        for (Category category : categories) {
            ids.add(category.getId());
        }
        return ids;
    }
}
