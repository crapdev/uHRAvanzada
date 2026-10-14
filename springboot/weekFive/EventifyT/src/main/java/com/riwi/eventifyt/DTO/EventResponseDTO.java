package com.riwi.eventifyt.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

// DTO de SALIDA: lo que la API devuelve del detalle de un evento.
// Todo viene "aplanado" en textos: no se exponen ids de las relaciones ni la entidad JPA.
@Schema(description = "Detalle de un evento")
public record EventResponseDTO(
        @Schema(description = "Id del evento", example = "1")
        Long id,
        @Schema(description = "Nombre del evento", example = "Concierto de Jazz")
        String name,
        @Schema(description = "Fecha del evento", example = "2026-12-15")
        LocalDate date,
        @Schema(description = "Descripción del evento", example = "Noche de jazz en vivo")
        String description,
        @Schema(description = "Nombre del lugar", example = "Movistar Arena")
        String venueName,
        @Schema(description = "Ciudad del lugar", example = "Bogotá")
        String city,
        @Schema(description = "Nombres de las categorías", example = "[\"Conciertos\", \"Festivales\"]")
        List<String> categoryNames
) {}
