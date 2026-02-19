package com.thiago.leiloa_api.dto.payment;

import java.math.BigDecimal;
import java.util.UUID;

import com.thiago.leiloa_api.domain.payment.Payment;
import com.thiago.leiloa_api.domain.payment.PaymentAddress;
import com.thiago.leiloa_api.domain.payment.PaymentStatus;
import com.thiago.leiloa_api.dto.payment_address.PaymentAddressDTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponseDTO {

    private UUID id;
    private UUID auctionId;
    private UUID winnerId;
    private BigDecimal value;
    private PaymentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private LocalDateTime expiredAt;

    private PaymentAddressDTO address;

    public static PaymentResponseDTO fromEntity(Payment p, PaymentAddress pa) {
        return PaymentResponseDTO.builder()
                .id(p.getId())
                .auctionId(p.getAuction().getId())
                .winnerId(p.getWinner().getId())
                .value(p.getValue())
                .status(p.getStatus())
                .createdAt(p.getCreatedAt())
                .paidAt(p.getPaidAt())
                .expiredAt(p.getExpiredAt())
                .address(pa != null ? PaymentAddressDTO.fromEntity(pa) : null)
                .build();
    }
}


