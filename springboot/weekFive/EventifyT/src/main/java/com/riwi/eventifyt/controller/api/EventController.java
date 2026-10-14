package com.riwi.eventifyt.controller.api;

import com.riwi.eventifyt.DTO.EventCreateDTO;
import com.riwi.eventifyt.DTO.EventFilter;
import com.riwi.eventifyt.DTO.EventResponseDTO;
import com.riwi.eventifyt.DTO.EventSummaryDTO;
import com.riwi.eventifyt.DTO.SliceResponse;
import com.riwi.eventifyt.service.EventService;
import com.riwi.eventifyt.validation.ValidationGroups.OnCreate;
import com.riwi.eventifyt.validation.ValidationGroups.OnUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RequiredArgsConstructor
@RestController //  Esta clase va a recibir peticiones HTTP y sus respuestan se devolveran en JSON     Combina @Controller y @ResponseBody
@RequestMapping("/api/events") //Define la ruta
@Tag(name = "Events", description = """
        Operaciones para registrar y consultar eventos.
        **Borrado lógico:** los eventos eliminados quedan inactivos en la base de datos y \
        ningún endpoint los vuelve a devolver.""") //con esto Swagger agrupa los endpoints como Events
// El 500 puede pasar en cualquier endpoint, por eso se documenta una sola vez para toda la clase
@ApiResponse(responseCode = "500", description = "Error inesperado del servidor",
        content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
public class EventController {
    private final EventService eventService;

    @GetMapping
    @Operation(summary = "Catálogo y búsqueda de eventos (Slice de EventSummaryDTO)",
            description = """
                    Devuelve un fragmento (`Slice`) de `EventSummaryDTO`: nombre, fecha, lugar y ciudad.
                    * Todos los filtros son opcionales y se combinan con **AND**.
                    * Los filtros de texto son **parciales e insensibles a mayúsculas** (`city=bog` encuentra *Bogotá*).
                    * Resultados **siempre ordenados por fecha descendente**.
                    * Solo se incluyen eventos **activos**: los eliminados (borrado lógico) nunca aparecen.
                    * No se calcula el total de registros: use `hasNext` / `hasPrevious` para navegar.""")
    @ApiResponse(responseCode = "200", description = "Fragmento de eventos")
    public ResponseEntity<SliceResponse<EventSummaryDTO>> search(
            @Parameter(description = "Texto contenido en el nombre del evento", example = "rock")
            @RequestParam(required = false) String name,
            @Parameter(description = "Texto contenido en la ciudad del lugar (relación Event → Venue)", example = "bog")
            @RequestParam(required = false) String city,
            @Parameter(description = "Texto contenido en el nombre de alguna de sus categorías (relación Event ↔ Category)", example = "rock")
            @RequestParam(required = false) String category,
            @Parameter(description = "Capacidad mínima del lugar donde se realiza el evento", example = "1000")
            @RequestParam(required = false) Integer minCapacity,
            @Parameter(description = "Fecha inicial del rango (inclusive), formato yyyy-MM-dd", example = "2026-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "Fecha final del rango (inclusive), formato yyyy-MM-dd", example = "2026-12-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @Parameter(description = "Número de página (empieza en 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de la página", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        EventFilter filter = new EventFilter(name, city, category, minCapacity, from, to);
        return ResponseEntity.ok(SliceResponse.from(eventService.search(filter, page, size)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un evento por ID (EventResponseDTO)",
            description = "Devuelve el lugar y las categorías como textos planos (`venueName`, `categoryNames`). "
                    + "Un evento eliminado lógicamente responde 404.")
    @ApiResponse(responseCode = "200", description = "Evento encontrado")
    @ApiResponse(responseCode = "404", description = "No existe o fue eliminado (inactivo)",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<EventResponseDTO> findById(@PathVariable Long id){
        return ResponseEntity.ok(eventService.findById(id));
    }

    // @Validated(OnCreate.class) = @Valid + "usa las reglas del grupo OnCreate" (ej. el id NO se envia)
    @PostMapping
    @Operation(summary = "Registrar un nuevo evento",
            description = "`venueId` es obligatorio. No envíes `id`. La fecha no puede ser anterior a hoy.")
    @ApiResponse(responseCode = "201", description = "Evento creado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos (incluye el mapa `errors` campo → mensaje)",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "El lugar o alguna categoría no existe",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<EventResponseDTO> create(@Validated(OnCreate.class) @RequestBody EventCreateDTO dto){ // el @Requestbody obtiene el json del cliente y lo convierte en un objeto java
        EventResponseDTO created = eventService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un evento existente",
            description = "Reemplaza datos, lugar y categorías. El `id` del cuerpo es obligatorio y debe coincidir con el de la URL.")
    @ApiResponse(responseCode = "200", description = "Evento actualizado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o el id del cuerpo no coincide con el de la URL",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "El evento, el lugar o alguna categoría no existe",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<EventResponseDTO> update(@PathVariable Long id,
                                                   @Validated(OnUpdate.class) @RequestBody EventCreateDTO dto){
        return ResponseEntity.ok(eventService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un evento (borrado lógico)",
            description = """
                    No borra la fila: ejecuta `UPDATE events SET active = false`.
                    Desde ese momento el evento desaparece del catálogo, las búsquedas y el detalle (404), \
                    pero se conserva en la base de datos para trazabilidad y auditoría.""")
    @ApiResponse(responseCode = "204", description = "Evento desactivado")
    @ApiResponse(responseCode = "404", description = "No existe o ya estaba eliminado",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<Void> delete(
            @Parameter(description = "Id del evento a desactivar", example = "1") @PathVariable Long id) {
        eventService.delete(id);
        return ResponseEntity.noContent().build(); // 204, sin body
    }
}
