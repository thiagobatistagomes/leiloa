package com.thiago.leiloa_api.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.thiago.leiloa_api.domain.category.Category;
import com.thiago.leiloa_api.domain.item.Item;
import com.thiago.leiloa_api.domain.item.ItemStatus;
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

    // Criar novos itens
    public ItemResponseDTO create(ItemCreateDTO dto) {

        User user = getAuthenticatedUser();

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
        item.setSeller(user);
        item.setStatus(ItemStatus.ACTIVE);

        return toResponseDTO(itemRepository.save(item));
    }

    // Listar itens do usuário autenticado
    public List<ItemResponseDTO> listMyItems() {

        User user = getAuthenticatedUser();

        return itemRepository
                .findAllBySellerAndStatus(user, ItemStatus.ACTIVE)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public ItemResponseDTO getMyItemById(UUID itemId) {

        User user = getAuthenticatedUser();

        Item item = itemRepository
                .findByIdAndSeller(itemId, user)
                .orElseThrow(() ->
                        new EntityNotFoundException("Item não encontrado")
                );

        if (item.getStatus() == ItemStatus.DELETED) {
            throw new EntityNotFoundException("Item não encontrado");
        }

        return toResponseDTO(item);
    }

    // Atualizar itens
    public ItemResponseDTO update(UUID itemId, ItemUpdateDTO dto) {

        User user = getAuthenticatedUser();

        Item item = itemRepository
                .findByIdAndSeller(itemId, user)
                .orElseThrow(() ->
                        new EntityNotFoundException("Item não encontrado")
                );

        if (item.getStatus() != ItemStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Item não pode ser editado no estado atual"
            );
        }

        Category category = categoryRepository
                .findById(dto.getCategoryId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Categoria não encontrada")
                );

        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setImageUrl(dto.getImageUrl());
        item.setCategory(category);

        return toResponseDTO(itemRepository.save(item));
    }

    // Deletar itens
    public void delete(UUID itemId) {

        User user = getAuthenticatedUser();

        Item item = itemRepository
                .findByIdAndSeller(itemId, user)
                .orElseThrow(() ->
                        new EntityNotFoundException("Item não encontrado")
                );

        if (item.getStatus() == ItemStatus.IN_AUCTION) {
            throw new IllegalStateException(
                    "Item está em leilão ativo e não pode ser removido"
            );
        }

        if (item.getStatus() == ItemStatus.DELETED) {
            return;
        }

        item.setStatus(ItemStatus.DELETED);
        itemRepository.save(item);
    }

    // Mapper
    private ItemResponseDTO toResponseDTO(Item item) {

        return new ItemResponseDTO(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getImageUrl(),
                item.getCategory().getId(),
                item.getCategory().getName(),
                item.getSeller().getId(),
                item.getSeller().getName(),
                item.getStatus(),
                item.getCreatedAt()
        );
    }

    // Utilitário para obter o usuário autenticado
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

    // Listar itens públicos ativos com paginação
    public Page<ItemResponseDTO> listPublicActiveItems(
        UUID categoryId,
        String name,
        Pageable pageable
    ) {

        if (categoryId != null && !categoryRepository.existsById(categoryId)) {
           throw new EntityNotFoundException("Categoria não encontrada");
        }

        // Categoria + nome
        if (categoryId != null && name != null && !name.isBlank()) {
           return itemRepository
                .findByStatusAndCategoryIdAndNameContainingIgnoreCase(
                        ItemStatus.ACTIVE,
                        categoryId,
                        name,
                        pageable
                )
                .map(this::toResponseDTO);
        }

        // Apenas categoria
        if (categoryId != null) {
           return itemRepository
                .findByStatusAndCategoryId(
                        ItemStatus.ACTIVE,
                        categoryId,
                        pageable
                )
                .map(this::toResponseDTO);
        }

        // Apenas nome
        if (name != null && !name.isBlank()) {
           return itemRepository
                .findByStatusAndNameContainingIgnoreCase(
                        ItemStatus.ACTIVE,
                        name,
                        pageable
                )
                .map(this::toResponseDTO);
    }

        // Sem filtros
        return itemRepository
            .findByStatus(ItemStatus.ACTIVE, pageable)
            .map(this::toResponseDTO);
    }




}


