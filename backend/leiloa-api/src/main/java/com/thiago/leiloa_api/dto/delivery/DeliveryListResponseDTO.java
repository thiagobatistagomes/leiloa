package com.thiago.leiloa_api.dto.delivery;

import java.time.LocalDateTime;
import java.util.UUID;

import com.thiago.leiloa_api.domain.delivery.Delivery;
import com.thiago.leiloa_api.domain.delivery.DeliveryStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeliveryListResponseDTO {
    
    private UUID id;
    private UUID paymentId;
    private DeliveryStatus status;
    private String deliveryMethod;
    private String trackingCode;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static DeliveryListResponseDTO fromEntity(Delivery d) {
        return DeliveryListResponseDTO.builder()
                .id(d.getId())
                .paymentId(d.getPayment().getId())
                .status(d.getStatus())
                .deliveryMethod(d.getDeliveryMethod())
                .trackingCode(d.getTrackingCode())
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .build();
    }
}

