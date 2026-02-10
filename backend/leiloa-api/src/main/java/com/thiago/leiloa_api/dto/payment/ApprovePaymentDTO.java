package com.thiago.leiloa_api.dto.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.thiago.leiloa_api.domain.payment.Payment;
import com.thiago.leiloa_api.domain.payment.PaymentStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovePaymentDTO {

    private UUID id;
    private UUID auctionId;
    private UUID winnerId;
    private BigDecimal value;
    private PaymentStatus status;
    private LocalDateTime paidAt;

    public static ApprovePaymentDTO fromEntity(Payment p) {
        return ApprovePaymentDTO.builder()
                .id(p.getId())
                .auctionId(p.getAuction().getId())
                .winnerId(p.getWinner().getId())
                .value(p.getValue())
                .status(p.getStatus())
                .paidAt(p.getPaidAt())
                .build();
    }
}
