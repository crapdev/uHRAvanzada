package com.riwi.eventifyt.controller.api;

import com.riwi.eventifyt.DTO.PageResponse;
import com.riwi.eventifyt.DTO.VenueCreateDTO;
import com.riwi.eventifyt.DTO.VenueResponseDTO;
import com.riwi.eventifyt.service.VenueService;
import com.riwi.eventifyt.validation.ValidationGroups.OnCreate;
import com.riwi.eventifyt.validation.ValidationGroups.OnUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/venues")
@Tag(name = "Venues", description = "Gestión de lugares de Eventify")
@ApiResponse(responseCode = "500", description = "Error inesperado del servidor",
        content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
public class VenueController {

    private final VenueService venueService;

    @Operation(summary = "Obtener lugares paginados", description = "Soporta ?page=0&size=10&sort=name,asc")
    @GetMapping
    @ApiResponse(responseCode = "200", description = "Página de lugares")
    public ResponseEntity<PageResponse<VenueResponseDTO>> listAll(
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(venueService.listAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un lugar por ID")
    @ApiResponse(responseCode = "200", description = "Lugar encontrado")
    @ApiResponse(responseCode = "404", description = "El lugar no existe",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<VenueResponseDTO> findById(@PathVariable Long id){
        return ResponseEntity.ok(venueService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Registrar un nuevo lugar", description = "No envíes `id`. El nombre no se puede repetir.")
    @ApiResponse(responseCode = "201", description = "Lugar creado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos (incluye el mapa `errors` campo → mensaje)",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "Ya existe un lugar con ese nombre",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<VenueResponseDTO> create(@Validated(OnCreate.class) @RequestBody VenueCreateDTO dto){
        VenueResponseDTO created = venueService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Editar un lugar", description = "El `id` del cuerpo es obligatorio y debe coincidir con el de la URL.")
    @ApiResponse(responseCode = "200", description = "Lugar actualizado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o el id del cuerpo no coincide con el de la URL",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "El lugar no existe",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "Ya existe otro lugar con ese nombre",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<VenueResponseDTO> update(@PathVariable Long id,
                                                   @Validated(OnUpdate.class) @RequestBody VenueCreateDTO dto){
        return ResponseEntity.ok(venueService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un lugar", description = "No se puede eliminar un lugar que tenga eventos vinculados.")
    @ApiResponse(responseCode = "204", description = "Lugar eliminado")
    @ApiResponse(responseCode = "400", description = "El lugar tiene eventos vinculados",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "El lugar no existe",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<Void> delete(@PathVariable Long id){
        venueService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
