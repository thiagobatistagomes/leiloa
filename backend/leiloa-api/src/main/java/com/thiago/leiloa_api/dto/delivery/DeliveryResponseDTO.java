package com.thiago.leiloa_api.dto.delivery;

import java.time.LocalDateTime;
import java.util.UUID;

import com.thiago.leiloa_api.domain.delivery.Delivery;
import com.thiago.leiloa_api.domain.delivery.DeliveryStatus;
import com.thiago.leiloa_api.dto.payment_address.PaymentAddressDTO;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeliveryResponseDTO {

    private UUID id;
    private UUID paymentId;
    private DeliveryStatus status;
    private String deliveryMethod;
    private String trackingCode;

    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime returnRequestedAt;
    private LocalDateTime returnedAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private PaymentAddressDTO address;

    public static DeliveryResponseDTO fromEntity(Delivery d) {
        return DeliveryResponseDTO.builder()
                .id(d.getId())
                .paymentId(d.getPayment().getId())
                .status(d.getStatus())
                .deliveryMethod(d.getDeliveryMethod())
                .trackingCode(d.getTrackingCode())
                .shippedAt(d.getShippedAt())
                .deliveredAt(d.getDeliveredAt())
                .returnRequestedAt(d.getReturnRequestedAt())
                .returnedAt(d.getReturnedAt())
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .address(PaymentAddressDTO.fromEntity(d.getAddress()))
                .build();
    }
}
