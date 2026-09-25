package com.riwi.eventifyt.controller.admin;

import com.riwi.eventifyt.DTO.VenueRequest;
import com.riwi.eventifyt.DTO.VenueResponse;
import com.riwi.eventifyt.service.VenueService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VenueAdminController.class)
class VenueAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VenueService venueService;

    @Test
    void list_WithVenues_RendersTableAndModelContainsVenues() throws Exception {
        when(venueService.listAll()).thenReturn(List.of(
                new VenueResponse(1L, "Centro de Convenciones", "Calle 50 # 10-20", 500)));

        mockMvc.perform(get("/admin/venues"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/venues/list"))
                .andExpect(model().attribute("venues", hasSize(1)))
                .andExpect(content().string(containsString("Centro de Convenciones")));
    }

    @Test
    void list_Empty_ShowsFriendlyMessageInsteadOfTable() throws Exception {
        when(venueService.listAll()).thenReturn(List.of());

        mockMvc.perform(get("/admin/venues"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Actualmente no hay lugares registrados")))
                .andExpect(content().string(not(containsString("<table"))));
    }

    @Test
    void create_ValidForm_SavesAndRedirectsToList() throws Exception {
        mockMvc.perform(post("/admin/venues")
                        .param("name", "Teatro Amira")
                        .param("address", "Carrera 54 # 52-10")
                        .param("capacity", "800"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/venues"))
                .andExpect(flash().attribute("successMessage", "Lugar creado correctamente"));

        verify(venueService).create(new VenueRequest("Teatro Amira", "Carrera 54 # 52-10", 800));
    }

    @Test
    void create_InvalidForm_ReturnsFormWithErrorsAndDoesNotSave() throws Exception {
        mockMvc.perform(post("/admin/venues")
                        .param("name", "")
                        .param("address", "")
                        .param("capacity", "0"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/venues/form"))
                .andExpect(model().attributeHasFieldErrors("venueForm", "name", "address", "capacity"));

        verify(venueService, never()).create(any());
    }
}