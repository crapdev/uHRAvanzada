package com.riwi.eventifyt.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

// Proyeccion "plana" para listados masivos: se construye directamente en la consulta JPQL
// (SELECT new ...EventSummaryDTO(...)), sin cargar entidades Event/Venue en memoria
@Schema(description = "Resumen liviano de un evento para catálogos y búsquedas (solo eventos activos)")
public record EventSummaryDTO(
        @Schema(description = "Id del evento", example = "1")
        Long id,
        @Schema(description = "Nombre del evento", example = "Concierto de ROCK")
        String name,
        @Schema(description = "Fecha del evento", example = "2026-11-21")
        LocalDate date,
        @Schema(description = "Nombre del lugar donde se realiza", example = "Movistar Arena")
        String venueName,
        @Schema(description = "Ciudad del lugar", example = "Bogotá")
        String city
) {}
