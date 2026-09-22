package com.riwi.eventifyt.controller;

import com.riwi.eventifyt.domain.Event;
import com.riwi.eventifyt.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController //  Esta clase va a recibir peticiones HTTP y sus respuestan se devolveran en JSON     Combina @Controller y @ResponseBody
@RequestMapping("/api/events") //Define la ruta
@Tag(name = "Events", description = "Operaciones para registrar y consultar eventos") //con esto Swagger agrupa los endpoints como Events
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService){ this.eventService = eventService;}

    @PostMapping
    @Operation(summary = "Registrar un nuevo evento", description = "holaaa")
    public ResponseEntity<Event> create(@RequestBody Event event){ // el @Requestbody obtiene el json del cliente y lo convierte en un objeto java
        Event createdEvent = eventService.create(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    }

    @GetMapping
    @Operation(summary = "Obtener eventos", description = "holaaa")
    public ResponseEntity<List<Event>> listAll(){
        List<Event> events = eventService.listAll();
        return ResponseEntity.ok(events);
    }
}
