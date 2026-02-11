package com.thiago.leiloa_api.dto.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.thiago.leiloa_api.domain.payment.PaymentStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyPaymentFilterDTO {
        List<PaymentStatus> statuses;
        BigDecimal minValue;
        BigDecimal maxValue;
        LocalDateTime dateFrom;
        LocalDateTime dateTo;
}
    

