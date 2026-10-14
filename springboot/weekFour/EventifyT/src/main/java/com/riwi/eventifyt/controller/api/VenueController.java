package com.riwi.eventifyt.controller.api;

import com.riwi.eventifyt.DTO.PageResponse;
import com.riwi.eventifyt.DTO.VenueRequest;
import com.riwi.eventifyt.DTO.VenueResponse;
import com.riwi.eventifyt.service.VenueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/venues")
@Tag(name = "Venues", description = "Gestion de lugares de Eventify")
public class VenueController {

    private final VenueService venueService;

    /*@GetMapping
    @Operation(summary = "Obtener lugares")
    public ResponseEntity<List<VenueResponse>> listAll(){
        return ResponseEntity.ok(venueService.listAll());
    }*/

    @Operation(summary = "Obtener lugares paginados", description = "Soporta ?page=0&size=10&sort=name,asc")
    @GetMapping
    public ResponseEntity<PageResponse<VenueResponse>> listAll(
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(venueService.listAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener lugares")
    public ResponseEntity<VenueResponse> findById(@PathVariable Long id){
        return ResponseEntity.ok(venueService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Registrar un nuevo lugar")
    public ResponseEntity<VenueResponse> create(@Valid @RequestBody VenueRequest request){
        VenueResponse created = venueService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Editar un lugar")
    public ResponseEntity<VenueResponse> update (@PathVariable Long id, @Valid @RequestBody VenueRequest request){
        return ResponseEntity.ok(venueService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un lugar")
    public ResponseEntity<Void> delete (@PathVariable Long id){
        venueService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
