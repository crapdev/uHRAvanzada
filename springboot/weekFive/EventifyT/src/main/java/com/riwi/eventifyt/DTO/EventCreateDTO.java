package com.riwi.eventifyt.DTO;

import com.riwi.eventifyt.validation.NoPastEvents;
import com.riwi.eventifyt.validation.ValidationGroups.OnCreate;
import com.riwi.eventifyt.validation.ValidationGroups.OnUpdate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

// DTO de ENTRADA: lo que el cliente (API o formulario del panel) envia para crear/editar un evento.
// Es una clase (no un record) porque el formulario de Thymeleaf necesita getters y setters.
@Data                 // Lombok: getters, setters, equals, hashCode y toString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para crear o actualizar un evento")
public class EventCreateDTO {

    // Grupos de validacion: al CREAR no se debe enviar; al ACTUALIZAR es obligatorio
    @Null(groups = OnCreate.class, message = "No envíes el id al crear un evento")
    @NotNull(groups = OnUpdate.class, message = "El id es obligatorio al actualizar")
    @Schema(description = "Id del evento. No se envía al crear; obligatorio al actualizar (debe coincidir con el de la URL)",
            example = "1")
    private Long id;

    @NotBlank(message = "El nombre del evento es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    @Schema(description = "Nombre del evento", example = "Concierto de Jazz", minLength = 3, maxLength = 100)
    private String name;

    @NotNull(message = "La fecha es obligatoria")
    @NoPastEvents(groups = OnCreate.class)        // nuestra validacion personalizada (solo al crear)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) // el <input type="date"> envia yyyy-MM-dd
    @Schema(description = "Fecha del evento (yyyy-MM-dd). Al crear no puede ser anterior a hoy", example = "2026-12-15")
    private LocalDate date;

    @Size(max = 500, message = "La descripción no puede superar 500 caracteres")
    @Schema(description = "Descripción del evento", example = "Noche de jazz en vivo", maxLength = 500)
    private String description;

    @NotNull(message = "El lugar es obligatorio")
    @Schema(description = "Id del lugar donde se realiza (obligatorio)", example = "1")
    private Long venueId;

    @Schema(description = "Ids de las categorías del evento", example = "[1, 3, 5]")
    private Set<Long> categoryIds = new HashSet<>();
}
