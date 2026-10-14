package com.riwi.eventifyt.controller.api;

import com.riwi.eventifyt.DTO.CategoryResponse;
import com.riwi.eventifyt.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/categories")
@Tag(name = "Categories", description = "Categorías temáticas para clasificar eventos")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Listar categorías",
            description = "Devuelve todas las categorías ordenadas por nombre. Sus ids se usan en `categoryIds` al crear/editar eventos.")
    public ResponseEntity<List<CategoryResponse>> listAll() {
        return ResponseEntity.ok(categoryService.listAll());
    }
}
