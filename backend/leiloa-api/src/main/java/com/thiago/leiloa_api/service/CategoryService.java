package com.thiago.leiloa_api.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.thiago.leiloa_api.domain.category.Category;
import com.thiago.leiloa_api.dto.categories.CategoryResponseDTO;
import com.thiago.leiloa_api.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // Listar todas as categorias
    public List<CategoryResponseDTO> findAll() {
        return categoryRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // Buscar categoria por ID
    public CategoryResponseDTO findById(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada"));

        return toDTO(category);
    }

    // Criar nova categoria (Somente ADMIN)
    public CategoryResponseDTO create(String name) {
        Category category = new Category();
        category.setName(name);

        Category saved = categoryRepository.save(category);
        return toDTO(saved);
    }

    // Converter Category para CategoryResponseDTO
    private CategoryResponseDTO toDTO(Category category) {
        return new CategoryResponseDTO(
                category.getId(),
                category.getName()
        );
    }
}
