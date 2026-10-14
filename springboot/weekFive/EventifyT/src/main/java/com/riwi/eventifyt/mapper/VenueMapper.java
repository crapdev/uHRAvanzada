package com.riwi.eventifyt.mapper;

import com.riwi.eventifyt.DTO.VenueCreateDTO;
import com.riwi.eventifyt.DTO.VenueResponseDTO;
import com.riwi.eventifyt.domain.Venue;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

// Venue no tiene relaciones y sus campos se llaman igual que en los DTOs,
// asi que MapStruct lo resuelve casi todo solo
@Mapper(componentModel = "spring")
public interface VenueMapper {

    @Mapping(target = "id", ignore = true)
    Venue toEntity(VenueCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    void updateEntity(VenueCreateDTO dto, @MappingTarget Venue venue);

    VenueResponseDTO toResponseDTO(Venue venue);

    // Para llenar el formulario de "Editar" del panel admin
    VenueCreateDTO toCreateDTO(Venue venue);
}
