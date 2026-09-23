package com.riwi.eventifyt.controller;

import com.riwi.eventifyt.DTO.EventRequest;
import com.riwi.eventifyt.DTO.EventResponse;
import com.riwi.eventifyt.domain.Event;
import com.riwi.eventifyt.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController //  Esta clase va a recibir peticiones HTTP y sus respuestan se devolveran en JSON     Combina @Controller y @ResponseBody
@RequestMapping("/api/events") //Define la ruta
@Tag(name = "Events", description = "Operaciones para registrar y consultar eventos") //con esto Swagger agrupa los endpoints como Events
public class EventController {
    private final EventService eventService;


    @GetMapping
    @Operation(summary = "Obtener eventos")
    public ResponseEntity<List<EventResponse>> listAll(){
        return ResponseEntity.ok(eventService.listAll());
    }

    @GetMapping
    @Operation(summary = "Obtener un evento por ID")
    public ResponseEntity<EventResponse> findById(@PathVariable Long id){
        return ResponseEntity.ok(eventService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Registrar un nuevo evento")
    public ResponseEntity<EventResponse> create(@Valid @RequestBody EventRequest request){ // el @Requestbody obtiene el json del cliente y lo convierte en un objeto java
        EventResponse created = eventService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping
    @Operation(summary = "Actualizar un evento existente")
    public ResponseEntity<EventResponse>   update(@PathVariable Long id, @Valid @RequestBody EventRequest request){
        return ResponseEntity.ok(eventService.update(id, request));
    }

    @DeleteMapping
    @Operation(summary = "Eliminar un evento")
    public ResponseEntity<void> delete (@PathVariable Long id){
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
