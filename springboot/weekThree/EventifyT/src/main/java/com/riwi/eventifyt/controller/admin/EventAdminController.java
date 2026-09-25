package com.riwi.eventifyt.controller.admin;

import com.riwi.eventifyt.DTO.EventResponse;
import com.riwi.eventifyt.controller.admin.form.EventForm;
import com.riwi.eventifyt.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class EventAdminController {

    private static final String LIST_VIEW = "admin/events/list";
    private static final String FORM_VIEW = "admin/events/form";
    private static final String REDIRECT_TO_LIST = "redirect:/admin/events";

    private final EventService eventService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("events", eventService.listAll());
        return LIST_VIEW;
    }

    // ---------- Create ----------

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("eventForm", new EventForm());
        return FORM_VIEW;
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("eventForm") EventForm eventForm,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
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
            return FORM_VIEW;
        }
        eventService.update(id, eventForm.toRequest());
        redirectAttributes.addFlashAttribute("successMessage", "Evento actualizado correctamente");
        return REDIRECT_TO_LIST;
    }

    // ---------- Delete ----------

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        eventService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Evento eliminado correctamente");
        return REDIRECT_TO_LIST;
    }
}