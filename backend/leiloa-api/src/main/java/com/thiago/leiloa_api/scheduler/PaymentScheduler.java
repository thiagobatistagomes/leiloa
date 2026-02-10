package com.thiago.leiloa_api.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.thiago.leiloa_api.service.PaymentService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentScheduler {

    private final PaymentService paymentService;

    @Transactional
    @Scheduled(fixedRate = 60_000)
    public void expirePendingPayments() {
        paymentService.expirePendingPayments();
    }
}

