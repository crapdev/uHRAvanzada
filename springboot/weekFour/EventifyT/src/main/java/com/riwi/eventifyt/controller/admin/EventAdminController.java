package com.riwi.eventifyt.controller.admin;

import com.riwi.eventifyt.DTO.EventFilter;
import com.riwi.eventifyt.DTO.EventResponse;
import com.riwi.eventifyt.DTO.EventSummaryDTO;
import com.riwi.eventifyt.controller.admin.form.EventForm;
import com.riwi.eventifyt.service.CategoryService;
import com.riwi.eventifyt.service.EventService;
import com.riwi.eventifyt.service.VenueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class EventAdminController {

    private static final String LIST_VIEW = "admin/events/list";
    private static final String FORM_VIEW = "admin/events/form";
    private static final String REDIRECT_TO_LIST = "redirect:/admin/events";
    static final int PAGE_SIZE = 20;

    private final EventService eventService;
    private final VenueService venueService;
    private final CategoryService categoryService;

    // Los filtros llegan como query params (?city=bog&category=rock&page=1) y Spring arma el record EventFilter
    @GetMapping
    public String list(@ModelAttribute("filter") EventFilter filter,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) {
        Slice<EventSummaryDTO> events = eventService.search(filter, page, PAGE_SIZE);
        model.addAttribute("events", events);
        // Enlaces Anterior/Siguiente que conservan los filtros activos en la URL
        model.addAttribute("prevUrl", events.hasPrevious() ? pageUrl(filter, events.getNumber() - 1) : null);
        model.addAttribute("nextUrl", events.hasNext() ? pageUrl(filter, events.getNumber() + 1) : null);
        return LIST_VIEW;
    }

    // ---------- Create ----------

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("eventForm", new EventForm());
        addFormOptions(model);
        return FORM_VIEW;
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("eventForm") EventForm eventForm,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            addFormOptions(model);
            return FORM_VIEW;
        }
        eventService.create(eventForm.toRequest());
        redirectAttributes.addFlashAttribute("successMessage", "Evento creado correctamente");
        return REDIRECT_TO_LIST;
    }

    // ---------- Update ----------

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        EventResponse event = eventService.findById(id);   // 404 view if not found
        model.addAttribute("eventForm", EventForm.fromResponse(event));
        model.addAttribute("eventId", id);
        addFormOptions(model);
        return FORM_VIEW;
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("eventForm") EventForm eventForm,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("eventId", id);   // keep the form in "edit mode"
            addFormOptions(model);
            return FORM_VIEW;
        }
        eventService.update(id, eventForm.toRequest());
        redirectAttributes.addFlashAttribute("successMessage", "Evento actualizado correctamente");
        return REDIRECT_TO_LIST;
    }

    // ---------- Delete (soft delete) ----------

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        eventService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Evento eliminado correctamente");
        return REDIRECT_TO_LIST;
    }

    /* -- Privates -- */

    // Opciones del <select> de lugares y de los checkboxes de categorias
    private void addFormOptions(Model model) {
        model.addAttribute("venues", venueService.listAll());
        model.addAttribute("categories", categoryService.listAll());
    }

    // Construye /admin/events?city=...&category=...&page=N incluyendo solo los filtros con valor
    static String pageUrl(EventFilter filter, int page) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/admin/events");
        addParam(builder, "name", filter.name());
        addParam(builder, "city", filter.city());
        addParam(builder, "category", filter.category());
        addParam(builder, "minCapacity", filter.minCapacity());
        addParam(builder, "from", filter.from());
        addParam(builder, "to", filter.to());
        builder.queryParam("page", page);
        return builder.encode().toUriString();
    }

    private static void addParam(UriComponentsBuilder builder, String name, Object value) {
        if (value != null) {
            builder.queryParam(name, value);
        }
    }
}
