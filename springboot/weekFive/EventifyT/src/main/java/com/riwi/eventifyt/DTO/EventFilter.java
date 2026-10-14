package com.riwi.eventifyt.DTO;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

// Criterios de busqueda del catalogo. Todos son opcionales: null = "no filtrar por esto"
public record EventFilter(String name,
                          String city,
                          String category,
                          Integer minCapacity,
                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

    // Un input vacio del formulario llega como "" → lo tratamos igual que null
    public EventFilter {
        name = clean(name);
        city = clean(city);
        category = clean(category);
    }

    public static EventFilter empty() {
        return new EventFilter(null, null, null, null, null, null);
    }

    private static String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
