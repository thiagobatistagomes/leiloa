package com.thiago.leiloa_api.dto.delivery;

import java.time.LocalDateTime;

import com.thiago.leiloa_api.domain.delivery.DeliveryStatus;




public record DeliveryFilterDTO(
        DeliveryStatus status,
        String deliveryMethod,
        LocalDateTime createdAtStart,
        LocalDateTime createdAtEnd,
        LocalDateTime updatedAtStart,
        LocalDateTime updatedAtEnd
) {}


