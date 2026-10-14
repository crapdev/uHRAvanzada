package com.riwi.eventifyt.controller.admin.form;

import com.riwi.eventifyt.DTO.CategoryResponse;
import com.riwi.eventifyt.DTO.EventRequest;
import com.riwi.eventifyt.DTO.EventResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

// Mutable form object for the admin panel (Thymeleaf needs getters/setters)
@Getter
@Setter
@NoArgsConstructor
public class EventForm {

    @NotBlank(message = "El nombre del evento es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String name;

    @NotNull(message = "La fecha es obligatoria")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)   // <input type="date"> uses yyyy-MM-dd
    private LocalDate date;

    @Size(max = 500, message = "La descripción no puede superar 500 caracteres")
    private String description;

    // <select> de lugares
    @NotNull(message = "El lugar es obligatorio")
    private Long venueId;

    // Checkboxes de categorias: Spring junta todos los marcados en este Set
    private Set<Long> categoryIds = new HashSet<>();

    public EventRequest toRequest() {
        return new EventRequest(name, date, description, venueId, categoryIds);
    }

    public static EventForm fromResponse(EventResponse response) {
        EventForm form = new EventForm();
        form.setName(response.name());
        form.setDate(response.date());
        form.setDescription(response.description());
        form.setVenueId(response.venueId());
        form.setCategoryIds(response.categories().stream()
                .map(CategoryResponse::id)
                .collect(Collectors.toCollection(HashSet::new)));
        return form;
    }
}
