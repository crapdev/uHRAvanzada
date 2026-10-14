package com.riwi.eventifyt.DTO;

import java.time.LocalDate;
import java.util.List;

// Detalle completo de un evento (incluye lugar y categorias)
public record EventResponse(Long id,
                            String name,
                            LocalDate date,
                            String description,
                            Long venueId,
                            String venueName,
                            String city,
                            List<CategoryResponse> categories) {
}
