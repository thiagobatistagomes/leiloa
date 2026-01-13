package com.thiago.leiloa_api.service;

import java.util.List;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.thiago.leiloa_api.domain.category.Category;
import com.thiago.leiloa_api.domain.item.Item;
import com.thiago.leiloa_api.domain.user.User;
import com.thiago.leiloa_api.dto.item.ItemCreateDTO;
import com.thiago.leiloa_api.dto.item.ItemResponseDTO;
import com.thiago.leiloa_api.dto.item.ItemUpdateDTO;
import com.thiago.leiloa_api.repository.CategoryRepository;
import com.thiago.leiloa_api.repository.ItemRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;

    public ItemResponseDTO create(ItemCreateDTO dto) {

        User authenticatedUser = getAuthenticatedUser();

        Category category = categoryRepository
                .findById(dto.getCategoryId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Categoria não encontrada")
                );

        Item item = new Item();
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setImageUrl(dto.getImageUrl());
        item.setCategory(category);
        item.setSeller(authenticatedUser);

        Item savedItem = itemRepository.save(item);

        return toResponseDTO(savedItem);
    }

    public List<ItemResponseDTO> listMyItems() {

        User authenticatedUser = getAuthenticatedUser();

        return itemRepository.findAllBySeller(authenticatedUser)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public ItemResponseDTO getMyItemById(UUID itemId) {

        User authenticatedUser = getAuthenticatedUser();

        Item item = itemRepository
                .findByIdAndSeller(itemId, authenticatedUser)
                .orElseThrow(() ->
                        new EntityNotFoundException("Item não encontrado")
                );

        return toResponseDTO(item);
    }

    public ItemResponseDTO update(UUID itemId, ItemUpdateDTO dto) {
        User user = getAuthenticatedUser();

        Item item = itemRepository
                .findByIdAndSeller(itemId, user)
                .orElseThrow(() ->
                        new EntityNotFoundException("Item não encontrado")
                );
        
        Category category = categoryRepository
                .findById(dto.getCategoryId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Categoria não encontrada")
                );

        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setImageUrl(dto.getImageUrl());
        item.setCategory(category);

        Item updated = itemRepository.save(item);

        return toResponseDTO(updated);
    }

    public void delete(UUID itemId) {

        User user = getAuthenticatedUser();

        Item item = itemRepository
                .findByIdAndSeller(itemId, user)
                .orElseThrow(() ->
                        new EntityNotFoundException("Item não encontrado")
                );

        // Futuramente verificar se o item está atualmente em um leilão antes de apagar
        // if(item.isInAuction()) {
        //     throw new IllegalStateException("Item está em um leilão ativo e não pode ser excluído");
        // }
        itemRepository.delete(item);
    }

    private ItemResponseDTO toResponseDTO(Item item) {

        return new ItemResponseDTO(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getImageUrl(),
                item.getCategory().getId(),
                item.getCategory().getName(),
                item.getSeller().getId(),
                item.getSeller().getName()
        );
    }

    private User getAuthenticatedUser() {

        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getUser();
        }

        throw new AccessDeniedException("Usuário não autenticado");
    }
}
