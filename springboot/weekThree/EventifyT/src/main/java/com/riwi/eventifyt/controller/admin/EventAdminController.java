package com.riwi.eventifyt.controller.admin;

import com.riwi.eventifyt.controller.admin.form.EventForm;
import com.riwi.eventifyt.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class EventAdminController {

    private final EventService eventService;   // same service the REST API uses

    @GetMapping
    public String list(Model model) {
        model.addAttribute("events", eventService.listAll());
        return "admin/events/list";
    }

    // 1. Shows the empty form
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("eventForm", new EventForm());
        return "admin/events/form";
    }

    // 2. Receives the submitted form
    @PostMapping
    public String create(@Valid @ModelAttribute("eventForm") EventForm eventForm,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "admin/events/form";            // re-render the form with errors
        }

        eventService.create(eventForm.toRequest());
        redirectAttributes.addFlashAttribute("successMessage", "Evento creado correctamente");
        return "redirect:/admin/events";           // Post-Redirect-Get
    }
}