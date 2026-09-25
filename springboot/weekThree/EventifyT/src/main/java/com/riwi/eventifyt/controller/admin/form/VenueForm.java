package com.riwi.eventifyt.controller.admin.form;

import com.riwi.eventifyt.DTO.VenueRequest;
import com.riwi.eventifyt.DTO.VenueResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VenueForm {

    @NotBlank(message = "El nombre del lugar es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String name;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 200, message = "La dirección no puede superar 200 caracteres")
    private String address;

    @NotNull(message = "La capacidad es obligatoria")
    @Positive(message = "La capacidad debe ser mayor que 0")
    private Integer capacity;

    public VenueRequest toRequest() {
        return new VenueRequest(name, address, capacity);
    }

    public static VenueForm fromResponse(VenueResponse response) {
        VenueForm form = new VenueForm();
        form.setName(response.name());
        form.setAddress(response.address());
        form.setCapacity(response.capacity());
        return form;
    }
}