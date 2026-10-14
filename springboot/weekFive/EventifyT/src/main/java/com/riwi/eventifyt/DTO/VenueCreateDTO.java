package com.riwi.eventifyt.DTO;

import com.riwi.eventifyt.validation.ValidationGroups.OnCreate;
import com.riwi.eventifyt.validation.ValidationGroups.OnUpdate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO de ENTRADA para crear/editar un lugar (mismo patron que EventCreateDTO)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para crear o actualizar un lugar")
public class VenueCreateDTO {

    @Null(groups = OnCreate.class, message = "No envíes el id al crear un lugar")
    @NotNull(groups = OnUpdate.class, message = "El id es obligatorio al actualizar")
    @Schema(description = "Id del lugar. No se envía al crear; obligatorio al actualizar (debe coincidir con el de la URL)",
            example = "1")
    private Long id;

    @NotBlank(message = "El nombre del lugar es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    @Schema(description = "Nombre del lugar (no se puede repetir)", example = "Teatro Metropolitano", minLength = 3, maxLength = 100)
    private String name;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 200, message = "La dirección no puede superar 200 caracteres")
    @Schema(description = "Dirección del lugar", example = "Calle 41 # 57-30", maxLength = 200)
    private String address;

    @NotBlank(message = "La ciudad es obligatoria")
    @Size(max = 100, message = "La ciudad no puede superar 100 caracteres")
    @Schema(description = "Ciudad del lugar", example = "Medellín", maxLength = 100)
    private String city;

    @NotNull(message = "La capacidad es obligatoria")
    @Min(value = 10, message = "La capacidad mínima es 10 personas")
    @Max(value = 100000, message = "La capacidad máxima es 100000 personas")
    @Schema(description = "Capacidad de personas", example = "1500", minimum = "10", maximum = "100000")
    private Integer capacity;
}
