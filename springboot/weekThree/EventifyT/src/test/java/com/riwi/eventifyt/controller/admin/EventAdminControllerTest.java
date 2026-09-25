package com.riwi.eventifyt.controller.admin;

import com.riwi.eventifyt.DTO.EventRequest;
import com.riwi.eventifyt.DTO.EventResponse;
import com.riwi.eventifyt.exception.ResourceNotFoundException;
import com.riwi.eventifyt.service.EventService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Loads only the web layer for this controller (no DB, no JPA)
@WebMvcTest(EventAdminController.class)
class EventAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean   // replaces the real EventService in the Spring context with a mock
    private EventService eventService;

    // ---------- Scenario 1 + 4: list with data ----------

    @Test
    void list_WithEvents_RendersTableAndModelContainsEvents() throws Exception {
        List<EventResponse> events = List.of(
                new EventResponse(1L, "Conferencia Java", LocalDate.of(2026, 10, 20), "Charla"),
                new EventResponse(2L, "Taller Spring", LocalDate.of(2026, 11, 5), "Práctica"));
        when(eventService.listAll()).thenReturn(events);

        mockMvc.perform(get("/admin/events"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/events/list"))
                .andExpect(model().attributeExists("events"))
                .andExpect(model().attribute("events", hasSize(2)))
                .andExpect(content().string(containsString("<table")))
                .andExpect(content().string(containsString("Conferencia Java")));
    }

    // ---------- Scenario 2: empty catalog ----------

    @Test
    void list_Empty_ShowsFriendlyMessageInsteadOfTable() throws Exception {
        when(eventService.listAll()).thenReturn(List.of());

        mockMvc.perform(get("/admin/events"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/events/list"))
                .andExpect(model().attribute("events", hasSize(0)))
                .andExpect(content().string(containsString("Actualmente no hay eventos programados")))
                .andExpect(content().string(not(containsString("<table"))));
    }

    // ---------- Create ----------

    @Test
    void showCreateForm_ReturnsFormWithEmptyEventForm() throws Exception {
        mockMvc.perform(get("/admin/events/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/events/form"))
                .andExpect(model().attributeExists("eventForm"))
                .andExpect(model().attributeDoesNotExist("eventId"));
    }

    // Scenario 3: save and redirect (Post-Redirect-Get)
    @Test
    void create_ValidForm_SavesAndRedirectsToList() throws Exception {
        mockMvc.perform(post("/admin/events")
                        .param("name", "Conferencia Java")
                        .param("date", "2026-10-20")
                        .param("description", "Charla"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/events"))
                .andExpect(flash().attribute("successMessage", "Evento creado correctamente"));

        verify(eventService).create(
                new EventRequest("Conferencia Java", LocalDate.of(2026, 10, 20), "Charla"));
    }

    @Test
    void create_InvalidForm_ReturnsFormWithErrorsAndDoesNotSave() throws Exception {
        mockMvc.perform(post("/admin/events")
                        .param("name", "")
                        .param("date", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/events/form"))
                .andExpect(model().attributeHasFieldErrors("eventForm", "name", "date"));

        verify(eventService, never()).create(any());
    }

    // ---------- Update ----------

    @Test
    void showEditForm_ExistingEvent_ReturnsPrefilledForm() throws Exception {
        when(eventService.findById(1L)).thenReturn(
                new EventResponse(1L, "Conferencia Java", LocalDate.of(2026, 10, 20), "Charla"));

        mockMvc.perform(get("/admin/events/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/events/form"))
                .andExpect(model().attribute("eventId", 1L))
                .andExpect(model().attribute("eventForm", hasProperty("name", is("Conferencia Java"))));
    }

    @Test
    void showEditForm_NonExistingEvent_ReturnsNotFoundView() throws Exception {
        when(eventService.findById(99L))
                .thenThrow(new ResourceNotFoundException("No se encontro el evento con el id 99"));

        mockMvc.perform(get("/admin/events/99/edit"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("admin/error/not-found"))
                .andExpect(model().attribute("errorMessage", "No se encontro el evento con el id 99"));
    }

    @Test
    void update_ValidForm_UpdatesAndRedirectsToList() throws Exception {
        mockMvc.perform(post("/admin/events/1")
                        .param("name", "Nombre editado")
                        .param("date", "2026-12-01")
                        .param("description", "Nueva descripción"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/events"))
                .andExpect(flash().attribute("successMessage", "Evento actualizado correctamente"));

        verify(eventService).update(1L,
                new EventRequest("Nombre editado", LocalDate.of(2026, 12, 1), "Nueva descripción"));
    }

    @Test
    void update_InvalidForm_KeepsEditModeAndDoesNotUpdate() throws Exception {
        mockMvc.perform(post("/admin/events/1")
                        .param("name", "")
                        .param("date", "2026-12-01"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/events/form"))
                .andExpect(model().attribute("eventId", 1L))
                .andExpect(model().attributeHasFieldErrors("eventForm", "name"));

        verify(eventService, never()).update(any(), any());
    }

    // ---------- Delete ----------

    @Test
    void delete_ExistingEvent_DeletesAndRedirectsToList() throws Exception {
        mockMvc.perform(post("/admin/events/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/events"))
                .andExpect(flash().attribute("successMessage", "Evento eliminado correctamente"));

        verify(eventService).delete(1L);
    }
}