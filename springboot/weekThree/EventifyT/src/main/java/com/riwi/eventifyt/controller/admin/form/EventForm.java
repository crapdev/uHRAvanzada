package com.riwi.eventifyt.controller.admin.form;

import com.riwi.eventifyt.DTO.EventRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class EventForm {

    @NotBlank(message = "El nombre del evento es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String name;

    @NotNull(message = "La fecha es obligatoria")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)   // <input type="date"> sends yyyy-MM-dd
    private LocalDate date;

    @Size(max = 500, message = "La descripción no puede superar 500 caracteres")
    private String description;

    // Converts the web form into the DTO the service already understands
    public EventRequest toRequest() {
        return new EventRequest(name, date, description);
    }
}