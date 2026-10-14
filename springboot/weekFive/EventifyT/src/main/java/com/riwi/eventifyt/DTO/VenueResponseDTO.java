package com.riwi.eventifyt.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

// DTO de SALIDA de un lugar
@Schema(description = "Detalle de un lugar")
public record VenueResponseDTO(
        @Schema(description = "Id del lugar", example = "1")
        Long id,
        @Schema(description = "Nombre del lugar", example = "Teatro Metropolitano")
        String name,
        @Schema(description = "Dirección del lugar", example = "Calle 41 # 57-30")
        String address,
        @Schema(description = "Ciudad del lugar", example = "Medellín")
        String city,
        @Schema(description = "Capacidad de personas", example = "1500")
        Integer capacity
) {}
