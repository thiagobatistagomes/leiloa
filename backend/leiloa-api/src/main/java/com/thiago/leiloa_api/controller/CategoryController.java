package com.thiago.leiloa_api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thiago.leiloa_api.dto.categories.CategoryResponseDTO;
import com.thiago.leiloa_api.dto.categories.CreateCategoryDTO;
import com.thiago.leiloa_api.service.CategoryService;

import lombok.RequiredArgsConstructor;



@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // USER e ADMIN
    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> listAll() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    // USER e ADMIN
    @GetMapping("/{id}")
    public CategoryResponseDTO findById(@PathVariable UUID id) {
        return categoryService.findById(id);
    }

    // ADMIN
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponseDTO> create(
            @RequestBody @Validated CreateCategoryDTO dto
    ) {
        CategoryResponseDTO response = categoryService.create(dto.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
