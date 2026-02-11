package com.thiago.leiloa_api.scheduler;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.thiago.leiloa_api.domain.auction.Auction;
import com.thiago.leiloa_api.domain.auction.AuctionStatus;
import com.thiago.leiloa_api.repository.AuctionRepository;
import com.thiago.leiloa_api.service.PaymentService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuctionScheduler {

    private final AuctionRepository auctionRepository;
    private final PaymentService paymentService;

    @Transactional
    @Scheduled(fixedRate = 60_000) // a cada 1 minuto
    public void updateAuctionStatuses() {

        LocalDateTime now = LocalDateTime.now();

        // SCHEDULED → ACTIVE
        List<Auction> toActivate =
            auctionRepository.findByStatusAndStartDateLessThanEqual(
                AuctionStatus.SCHEDULED,
                now
            );

        toActivate.forEach(a -> a.setStatus(AuctionStatus.ACTIVE));

        // ACTIVE → WAITING_PAYMENT (finalizar leilão)
        List<Auction> toFinish =
            auctionRepository.findByStatusAndEndDateLessThanEqual(
                AuctionStatus.ACTIVE,
                now
            );

        toFinish.forEach(a -> {
            a.waitForPayment();
            paymentService.createPaymentForAuction(a); // Criar pagamento automaticamente para o leilão finalizado
        });
    }
}

