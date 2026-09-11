package com.riwi.Eventify.controller;

import com.riwi.Eventify.model.Event;
import com.riwi.Eventify.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//con esto Swagger agrupa los endpoints como Events
@Tag(name = "Events", description = "Gestion de eventos de Eventify")
@RestController // Esta clase va a recibir peticiones HTTP y sus respuestan se devolveran en JSON
@RequestMapping("/api/events") //Define la ruta
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService){ this.eventService = eventService;}

    @Operation(summary = "Registrar un nuevo evento")
    @PostMapping
    public ResponseEntity<Event> create(@RequestBody Event event){
        Event createdEvent = eventService.create(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    }

    @Operation(summary = "Obtener eventos")
    @GetMapping
    public ResponseEntity<List<Event>> listAll(){
        List<Event> events = eventService.listAll();
        return ResponseEntity.ok(events);
    }
}
