package com.riwi.eventifyt.controller.admin;

import com.riwi.eventifyt.DTO.CategoryResponseDTO;
import com.riwi.eventifyt.DTO.EventCreateDTO;
import com.riwi.eventifyt.DTO.EventFilter;
import com.riwi.eventifyt.DTO.EventSummaryDTO;
import com.riwi.eventifyt.DTO.VenueResponseDTO;
import com.riwi.eventifyt.exception.ResourceNotFoundException;
import com.riwi.eventifyt.service.CategoryService;
import com.riwi.eventifyt.service.EventService;
import com.riwi.eventifyt.service.VenueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
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
    @MockBean
    private VenueService venueService;
    @MockBean
    private CategoryService categoryService;

    // Fecha siempre futura (si fuera fija, algun dia seria "pasada" y fallaria @NoPastEvents)
    private final LocalDate futureDate = LocalDate.now().plusDays(30);

    private final List<EventSummaryDTO> summaries = List.of(
            new EventSummaryDTO(1L, "Concierto de ROCK", LocalDate.of(2026, 11, 21), "Movistar Arena", "Bogotá"),
            new EventSummaryDTO(2L, "Taller Spring", LocalDate.of(2026, 11, 5), "Teatro Colón", "Bogotá"));

    @BeforeEach
    void setUp() {
        when(venueService.listAll()).thenReturn(List.of(
                new VenueResponseDTO(1L, "Movistar Arena", "Diagonal 61C", "Bogotá", 14000)));
        when(categoryService.listAll()).thenReturn(List.of(
                new CategoryResponseDTO(1L, "Conciertos", "Música en vivo"),
                new CategoryResponseDTO(8L, "Rock", "Rock")));
    }

    // ---------- List ----------

    @Test
    void list_WithEvents_RendersTableWithVenueAndCity() throws Exception {
        when(eventService.search(any(), eq(0), anyInt()))
                .thenReturn(new SliceImpl<>(summaries, PageRequest.of(0, 20), false));

        mockMvc.perform(get("/admin/events"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/events/list"))
                .andExpect(model().attributeExists("events", "filter"))
                .andExpect(content().string(containsString("<table")))
                .andExpect(content().string(containsString("Concierto de ROCK")))
                .andExpect(content().string(containsString("Movistar Arena")));
    }

    @Test
    void list_Empty_ShowsFriendlyMessageInsteadOfTable() throws Exception {
        when(eventService.search(any(), anyInt(), anyInt())).thenReturn(new SliceImpl<>(List.of()));

        mockMvc.perform(get("/admin/events"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Actualmente no hay eventos programados")))
                .andExpect(content().string(not(containsString("<table"))));
    }

    // Escenario 3: los filtros se mantienen en los enlaces de navegación
    @Test
    void list_WithFiltersAndNextPage_BuildsPagerLinksKeepingFilters() throws Exception {
        when(eventService.search(any(), eq(1), anyInt()))
                .thenReturn(new SliceImpl<>(summaries, PageRequest.of(1, 20), true));

        mockMvc.perform(get("/admin/events")
                        .param("city", "bog")
                        .param("category", "rock")
                        .param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("prevUrl", "/admin/events?city=bog&category=rock&page=0"))
                .andExpect(model().attribute("nextUrl", "/admin/events?city=bog&category=rock&page=2"))
                .andExpect(content().string(containsString("Siguiente")));

        verify(eventService).search(new EventFilter(null, "bog", "rock", null, null, null), 1, EventAdminController.PAGE_SIZE);
    }

    @Test
    void list_FirstAndLastPage_DisablesBothLinks() throws Exception {
        when(eventService.search(any(), anyInt(), anyInt()))
                .thenReturn(new SliceImpl<>(summaries, PageRequest.of(0, 20), false));

        mockMvc.perform(get("/admin/events"))
                .andExpect(model().attribute("prevUrl", nullValue()))
                .andExpect(model().attribute("nextUrl", nullValue()));
    }

    @Test
    void pageUrl_EncodesSpecialCharacters() {
        String url = EventAdminController.pageUrl(new EventFilter(null, "Bogotá", null, 500, LocalDate.of(2026, 1, 1), null), 3);
        assertEquals("/admin/events?city=Bogot%C3%A1&minCapacity=500&from=2026-01-01&page=3", url);
    }

    // ---------- Create ----------

    @Test
    void showCreateForm_ReturnsFormWithVenueSelectAndCategoryCheckboxes() throws Exception {
        mockMvc.perform(get("/admin/events/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/events/form"))
                .andExpect(model().attributeExists("eventForm", "venues", "categories"))
                .andExpect(model().attributeDoesNotExist("eventId"))
                .andExpect(content().string(containsString("<select")))
                .andExpect(content().string(containsString("type=\"checkbox\"")));
    }

    // Save and redirect (Post-Redirect-Get)
    @Test
    void create_ValidForm_SavesWithVenueAndCategories() throws Exception {
        mockMvc.perform(post("/admin/events")
                        .param("name", "Conferencia Java")
                        .param("date", futureDate.toString())
                        .param("description", "Charla")
                        .param("venueId", "1")
                        .param("categoryIds", "1", "8"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/events"))
                .andExpect(flash().attribute("successMessage", "Evento creado correctamente"));

        verify(eventService).create(
                new EventCreateDTO(null, "Conferencia Java", futureDate, "Charla", 1L, Set.of(1L, 8L)));
    }

    @Test
    void create_InvalidForm_ReturnsFormWithErrorsAndDoesNotSave() throws Exception {
        mockMvc.perform(post("/admin/events")
                        .param("name", "")
                        .param("date", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/events/form"))
                .andExpect(model().attributeHasFieldErrors("eventForm", "name", "date", "venueId"))
                .andExpect(model().attributeExists("venues", "categories"));

        verify(eventService, never()).create(any());
    }

    // Escenario 3: el formulario vuelve con el error junto al campo, resaltado, y conserva lo escrito
    @Test
    void create_EmptyName_RendersInlineErrorAndKeepsTypedValues() throws Exception {
        mockMvc.perform(post("/admin/events")
                        .param("name", "")
                        .param("date", futureDate.toString())
                        .param("description", "Texto que no se debe perder")
                        .param("venueId", "1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("eventForm", "name"))
                // El HTML muestra el mensaje, la clase roja y conserva los demas valores
                .andExpect(content().string(containsString("El nombre del evento es obligatorio")))
                .andExpect(content().string(containsString("campo-invalido")))
                .andExpect(content().string(containsString("Texto que no se debe perder")))
                .andExpect(content().string(containsString("value=\"" + futureDate + "\"")));

        verify(eventService, never()).create(any());
    }

    // Escenario 4: @NoPastEvents tambien funciona en el formulario
    @Test
    void create_PastDate_ShowsNoPastEventsMessage() throws Exception {
        mockMvc.perform(post("/admin/events")
                        .param("name", "Evento del pasado")
                        .param("date", LocalDate.now().minusDays(1).toString())
                        .param("venueId", "1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrorCode("eventForm", "date", "NoPastEvents"))
                .andExpect(content().string(containsString("No se pueden registrar eventos con fecha anterior a hoy")));

        verify(eventService, never()).create(any());
    }

    // ---------- Update ----------

    @Test
    void showEditForm_ExistingEvent_ReturnsPrefilledForm() throws Exception {
        when(eventService.findForEdit(1L)).thenReturn(
                new EventCreateDTO(1L, "Conferencia Java", futureDate, "Charla", 1L, Set.of(8L)));

        mockMvc.perform(get("/admin/events/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/events/form"))
                .andExpect(model().attribute("eventId", 1L))
                .andExpect(model().attribute("eventForm", hasProperty("name", is("Conferencia Java"))))
                .andExpect(model().attribute("eventForm", hasProperty("venueId", is(1L))))
                .andExpect(model().attribute("eventForm", hasProperty("categoryIds", is(Set.of(8L)))));
    }

    @Test
    void showEditForm_NonExistingEvent_ReturnsNotFoundView() throws Exception {
        when(eventService.findForEdit(99L))
                .thenThrow(new ResourceNotFoundException("El evento con ID 99 no existe"));

        mockMvc.perform(get("/admin/events/99/edit"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("admin/error/not-found"))
                .andExpect(model().attribute("errorMessage", "El evento con ID 99 no existe"));
    }

    @Test
    void update_ValidForm_UpdatesAndRedirectsToList() throws Exception {
        mockMvc.perform(post("/admin/events/1")
                        .param("name", "Nombre editado")
                        .param("date", "2026-12-01")
                        .param("description", "Nueva descripción")
                        .param("venueId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/events"))
                .andExpect(flash().attribute("successMessage", "Evento actualizado correctamente"));

        // Spring copia el {id} de la URL en el campo id del DTO
        verify(eventService).update(1L,
                new EventCreateDTO(1L, "Nombre editado", LocalDate.of(2026, 12, 1), "Nueva descripción", 1L, Set.of()));
    }

    @Test
    void update_InvalidForm_KeepsEditModeAndDoesNotUpdate() throws Exception {
        mockMvc.perform(post("/admin/events/1")
                        .param("name", "")
                        .param("date", "2026-12-01")
                        .param("venueId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/events/form"))
                .andExpect(model().attribute("eventId", 1L))
                .andExpect(model().attributeHasFieldErrors("eventForm", "name"));

        verify(eventService, never()).update(any(), any());
    }

    // ---------- Delete ----------

    @Test
    void delete_ExistingEvent_SoftDeletesAndRedirectsToList() throws Exception {
        mockMvc.perform(post("/admin/events/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/events"))
                .andExpect(flash().attribute("successMessage", "Evento eliminado correctamente"));

        verify(eventService).delete(1L);
    }
}
