package com.thiago.leiloa_api.dto.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.thiago.leiloa_api.domain.payment.Payment;
import com.thiago.leiloa_api.domain.payment.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyPendingPaymentResponseDTO {

    private UUID id;
    private UUID auctionId;
    private BigDecimal value;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;
    private PaymentStatus status;

    public static MyPendingPaymentResponseDTO fromEntity(Payment p) {
        return MyPendingPaymentResponseDTO.builder()
                .id(p.getId())
                .auctionId(p.getAuction().getId())
                .value(p.getValue())
                .createdAt(p.getCreatedAt())
                .expiredAt(p.getExpiredAt())
                .status(p.getStatus())
                .build();
    }
}

