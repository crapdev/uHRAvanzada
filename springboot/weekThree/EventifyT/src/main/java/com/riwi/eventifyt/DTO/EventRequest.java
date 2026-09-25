package com.riwi.eventifyt.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record EventRequest(
        @NotBlank(message = "El nombre del evento es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
        String name,

        @NotNull(message = "La fecha es obligatoria")
        LocalDate date,

        @Size(max = 500, message = "La descripción no puede superar 500 caracteres")
        String description
) {}
