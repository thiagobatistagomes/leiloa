package com.thiago.leiloa_api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thiago.leiloa_api.dto.item.ItemCreateDTO;
import com.thiago.leiloa_api.dto.item.ItemResponseDTO;
import com.thiago.leiloa_api.dto.item.ItemUpdateDTO;
import com.thiago.leiloa_api.service.ItemService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

// @PreAuthorize("hasRole('USER')") se usado aqui vale para todos os métodos
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    
    private final ItemService itemService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ItemResponseDTO> create(
        @RequestBody @Valid ItemCreateDTO dto
    ) {
        ItemResponseDTO response = itemService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ItemResponseDTO>> listMyItems() {
        return ResponseEntity.ok(itemService.listMyItems());
    }

    @GetMapping("/me/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ItemResponseDTO> getMyItemById(
        @PathVariable UUID id
    ) {
        return ResponseEntity.ok(itemService.getMyItemById(id));
    }

    @PutMapping("/me/{id}")
    @PreAuthorize("hasRole('USER')")
    public ItemResponseDTO update(
        @PathVariable UUID id,
        @RequestBody @Valid ItemUpdateDTO dto
    ) {
        return itemService.update(id, dto);
    }

    @DeleteMapping("/me/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        itemService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
