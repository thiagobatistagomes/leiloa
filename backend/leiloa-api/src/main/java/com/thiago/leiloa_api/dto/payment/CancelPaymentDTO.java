package com.thiago.leiloa_api.dto.payment;

import java.math.BigDecimal;
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
public class CancelPaymentDTO {

    private UUID id;
    private UUID auctionId;
    private UUID winnerId;
    private BigDecimal value;
    private PaymentStatus status;

    public static CancelPaymentDTO fromEntity(Payment p) {
        return CancelPaymentDTO.builder()
                .id(p.getId())
                .auctionId(p.getAuction().getId())
                .winnerId(p.getWinner().getId())
                .value(p.getValue())
                .status(p.getStatus())
                .build();
    }
}
