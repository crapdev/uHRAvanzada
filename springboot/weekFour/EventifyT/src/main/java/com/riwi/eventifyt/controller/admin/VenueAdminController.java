package com.riwi.eventifyt.controller.admin;

import com.riwi.eventifyt.DTO.VenueResponse;
import com.riwi.eventifyt.controller.admin.form.VenueForm;
import com.riwi.eventifyt.service.VenueService;
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
        model.addAttribute("venueForm", new VenueForm());
        return FORM_VIEW;
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("venueForm") VenueForm venueForm,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return FORM_VIEW;
        }
        venueService.create(venueForm.toRequest());
        redirectAttributes.addFlashAttribute("successMessage", "Lugar creado correctamente");
        return REDIRECT_TO_LIST;
    }

    // ---------- Update ----------

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        VenueResponse venue = venueService.findById(id);
        model.addAttribute("venueForm", VenueForm.fromResponse(venue));
        model.addAttribute("venueId", id);
        return FORM_VIEW;
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("venueForm") VenueForm venueForm,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("venueId", id);
            return FORM_VIEW;
        }
        venueService.update(id, venueForm.toRequest());
        redirectAttributes.addFlashAttribute("successMessage", "Lugar actualizado correctamente");
        return REDIRECT_TO_LIST;
    }

    // ---------- Delete ----------

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        venueService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Lugar eliminado correctamente");
        return REDIRECT_TO_LIST;
    }
}