package com.riwi.eventifyt.controller.api;

import com.riwi.eventifyt.DTO.EventResponseDTO;
import com.riwi.eventifyt.exception.ResourceNotFoundException;
import com.riwi.eventifyt.service.EventService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Solo la capa web de la API + GlobalExceptionHandler (sin base de datos)
@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    private final LocalDate futureDate = LocalDate.now().plusDays(30);

    // Escenario 1: el detalle es un EventResponseDTO plano (sin ids de relaciones)
    @Test
    void findById_Existing_ReturnsFlatResponseDTO() throws Exception {
        when(eventService.findById(1L)).thenReturn(new EventResponseDTO(1L, "Concierto de ROCK", futureDate,
                "Gran noche", "Movistar Arena", "Bogotá", List.of("Conciertos", "Rock")));

        mockMvc.perform(get("/api/events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.venueName").value("Movistar Arena"))
                .andExpect(jsonPath("$.categoryNames[1]").value("Rock"))
                .andExpect(jsonPath("$.venue").doesNotExist())
                .andExpect(jsonPath("$.venueId").doesNotExist());
    }

    // Escenario 5: recurso inexistente → 404 con ProblemDetail
    @Test
    void findById_NonExisting_Returns404ProblemDetail() throws Exception {
        when(eventService.findById(9999L)).thenThrow(new ResourceNotFoundException("El evento con ID 9999 no existe"));

        mockMvc.perform(get("/api/events/9999"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.type").value("https://eventify.com/errors/not-found"))
                .andExpect(jsonPath("$.title").value("Recurso no encontrado"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("El evento con ID 9999 no existe"))
                .andExpect(jsonPath("$.instance").value("/api/events/9999"));
    }

    // Escenarios 2 y 4: nombre vacio + fecha pasada → 400 con el mapa de errores por campo
    @Test
    void create_EmptyNameAndPastDate_Returns400WithFieldErrors() throws Exception {
        String body = """
                {"name": "", "date": "%s", "venueId": 1}
                """.formatted(LocalDate.now().minusDays(1));

        mockMvc.perform(post("/api/events").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Datos inválidos"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.date").value("No se pueden registrar eventos con fecha anterior a hoy"));

        verify(eventService, never()).create(any());
    }

    // Escenario 4: un error de FORMATO es distinto al error de negocio de @NoPastEvents
    @Test
    void create_MalformedDate_Returns400FormatError() throws Exception {
        String body = """
                {"name": "Concierto", "date": "no-es-fecha", "venueId": 1}
                """;

        mockMvc.perform(post("/api/events").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Formato inválido"));
    }

    // Grupos de validacion: al CREAR no se envia id...
    @Test
    void create_WithId_Returns400() throws Exception {
        String body = """
                {"id": 5, "name": "Concierto", "date": "%s", "venueId": 1}
                """.formatted(futureDate);

        mockMvc.perform(post("/api/events").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.id").value("No envíes el id al crear un evento"));
    }

    // ...y al ACTUALIZAR es obligatorio
    @Test
    void update_WithoutId_Returns400() throws Exception {
        String body = """
                {"name": "Concierto", "date": "%s", "venueId": 1}
                """.formatted(futureDate);

        mockMvc.perform(put("/api/events/1").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.id").value("El id es obligatorio al actualizar"));
    }

    @Test
    void create_ValidBody_Returns201() throws Exception {
        when(eventService.create(any())).thenReturn(new EventResponseDTO(10L, "Concierto", futureDate,
                null, "Movistar Arena", "Bogotá", List.of()));
        String body = """
                {"name": "Concierto", "date": "%s", "venueId": 1, "categoryIds": [1, 3, 5]}
                """.formatted(futureDate);

        mockMvc.perform(post("/api/events").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10));
    }

    // Red de seguridad: un error inesperado → 500 sin mostrar el stack trace
    @Test
    void anyUnexpectedError_Returns500ProblemDetail() throws Exception {
        when(eventService.findById(1L)).thenThrow(new IllegalStateException("detalle interno secreto"));

        mockMvc.perform(get("/api/events/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.title").value("Error interno"))
                .andExpect(jsonPath("$.detail").value("Ocurrió un error inesperado. Intenta de nuevo más tarde"));
    }
}
