package com.riwi.eventifyt.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

// DTO de SALIDA de una categoria
@Schema(description = "Categoría de eventos")
public record CategoryResponseDTO(
        @Schema(description = "Id de la categoría (se usa en categoryIds)", example = "1")
        Long id,
        @Schema(description = "Nombre de la categoría", example = "Conciertos")
        String name,
        @Schema(description = "Descripción", example = "Presentaciones de música en vivo")
        String description
) {}
