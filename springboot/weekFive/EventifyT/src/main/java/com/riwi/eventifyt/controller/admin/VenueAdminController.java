package com.riwi.eventifyt.controller.admin;

import com.riwi.eventifyt.DTO.VenueCreateDTO;
import com.riwi.eventifyt.exception.BusinessRuleViolationException;
import com.riwi.eventifyt.exception.DuplicateResourceException;
import com.riwi.eventifyt.service.VenueService;
import com.riwi.eventifyt.validation.ValidationGroups.OnCreate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/venues")
@RequiredArgsConstructor
public class VenueAdminController {

    private static final String LIST_VIEW = "admin/venues/list";
    private static final String FORM_VIEW = "admin/venues/form";
    private static final String REDIRECT_TO_LIST = "redirect:/admin/venues";

    private final VenueService venueService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("venues", venueService.listAll());
        return LIST_VIEW;
    }

    // ---------- Create ----------

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("venueForm", new VenueCreateDTO());
        return FORM_VIEW;
    }

    @PostMapping
    public String create(@Validated(OnCreate.class) @ModelAttribute("venueForm") VenueCreateDTO venueForm,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return FORM_VIEW;
        }
        try {
            venueService.create(venueForm);
        } catch (DuplicateResourceException exception) {
            // Mostramos el "nombre repetido" como un error mas, junto al campo nombre
            bindingResult.rejectValue("name", "duplicate", exception.getMessage());
            return FORM_VIEW;
        }
        redirectAttributes.addFlashAttribute("successMessage", "Lugar creado correctamente");
        return REDIRECT_TO_LIST;
    }

    // ---------- Update ----------

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("venueForm", venueService.findForEdit(id));
        model.addAttribute("venueId", id);
        return FORM_VIEW;
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("venueForm") VenueCreateDTO venueForm,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        model.addAttribute("venueId", id);   // keep the form in "edit mode" if we go back to it
        if (bindingResult.hasErrors()) {
            return FORM_VIEW;
        }
        try {
            venueService.update(id, venueForm);
        } catch (DuplicateResourceException exception) {
            bindingResult.rejectValue("name", "duplicate", exception.getMessage());
            return FORM_VIEW;
        }
        redirectAttributes.addFlashAttribute("successMessage", "Lugar actualizado correctamente");
        return REDIRECT_TO_LIST;
    }

    // ---------- Delete ----------

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            venueService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Lugar eliminado correctamente");
        } catch (BusinessRuleViolationException exception) {
            // Ej: el lugar tiene eventos vinculados → mensaje rojo en el listado
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return REDIRECT_TO_LIST;
    }
}
