package com.thiago.leiloa_api.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.thiago.leiloa_api.domain.category.Category;
import com.thiago.leiloa_api.domain.item.Item;
import com.thiago.leiloa_api.domain.item.ItemStatus;
import com.thiago.leiloa_api.domain.user.User;

public interface ItemRepository extends JpaRepository<Item, UUID> {

    Page<Item> findByStatus(ItemStatus status, Pageable pageable);

    Page<Item> findByStatusAndCategoryId(
            ItemStatus status,
            UUID categoryId,
            Pageable pageable
    );

    Page<Item> findByStatusAndNameContainingIgnoreCase(
            ItemStatus status,
            String name,
            Pageable pageable
    );

    Page<Item> findByStatusAndCategoryIdAndNameContainingIgnoreCase(
            ItemStatus status,
            UUID categoryId,
            String name,
            Pageable pageable
    );



    boolean existsByCategory(Category category);

    boolean existsBySeller(User seller);

    // Read Seller

    List<Item> findAllBySellerAndStatus(
            User seller,
            ItemStatus status
    );

    Optional<Item> findByIdAndSeller(
            UUID id,
            User seller
    );

    Optional<Item> findByIdAndSellerAndStatus(
            UUID id,
            User seller,
            ItemStatus status
    );

    // Read General

    List<Item> findAllByStatus(ItemStatus status);

    boolean existsByIdAndStatus(
            UUID id,
            ItemStatus status
    );
}
