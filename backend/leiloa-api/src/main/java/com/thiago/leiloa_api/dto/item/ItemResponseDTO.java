package com.thiago.leiloa_api.dto.item;


import java.time.LocalDateTime;
import java.util.UUID;

import com.thiago.leiloa_api.domain.item.ItemStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ItemResponseDTO {
    private UUID id;
    private String name;
    private String description;
    private String imageUrl;

    private UUID categoryId;
    private String categoryName;

    private UUID sellerId;
    private String sellerName;

    private ItemStatus status;

    private LocalDateTime createdAt;

}