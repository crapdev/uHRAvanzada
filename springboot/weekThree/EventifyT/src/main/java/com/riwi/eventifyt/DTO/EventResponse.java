package com.riwi.eventifyt.DTO;

import java.time.LocalDate;

public record EventResponse(Long id, String name, LocalDate date, String description    ) {
}
