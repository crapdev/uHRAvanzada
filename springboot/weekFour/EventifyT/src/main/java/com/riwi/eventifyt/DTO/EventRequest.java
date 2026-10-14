package com.riwi.eventifyt.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;

public record EventRequest(
        @NotBlank(message = "El nombre del evento es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
        String name,

        @NotNull(message = "La fecha es obligatoria")
        LocalDate date,

        @Size(max = 500, message = "La descripción no puede superar 500 caracteres")
        String description,

        @Schema(description = "Id del lugar (obligatorio)", example = "1")
        @NotNull(message = "El lugar es obligatorio")
        Long venueId,

        @Schema(description = "Ids de las categorías del evento", example = "[1, 8]")
        Set<Long> categoryIds
) {}
