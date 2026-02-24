package com.thiago.leiloa_api.scheduler;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.thiago.leiloa_api.domain.auction.Auction;
import com.thiago.leiloa_api.domain.auction.AuctionStatus;
import com.thiago.leiloa_api.domain.notification.NotificationTypeCodes;
import com.thiago.leiloa_api.repository.AuctionRepository;
import com.thiago.leiloa_api.repository.BidRepository;
import com.thiago.leiloa_api.service.PaymentService;
import com.thiago.leiloa_api.service.NotificationService;
import com.thiago.leiloa_api.dto.notification.NotificationCreateDTO;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuctionScheduler {

    private final AuctionRepository auctionRepository;
    private final PaymentService paymentService;
    private final NotificationService notificationService;
    private final BidRepository bidRepository;

    @Transactional
    @Scheduled(fixedRate = 60_000) // a cada 1 minuto
    public void updateAuctionStatuses() {

        LocalDateTime now = LocalDateTime.now();


        // 1. Ativar leilões agendados
        List<Auction> toActivate =
            auctionRepository.findByStatusAndStartDateLessThanEqual(
                AuctionStatus.SCHEDULED,
                now
            );

        for (Auction auction : toActivate) {
            auction.setStatus(AuctionStatus.ACTIVE);

            notificationService.createNotification(new NotificationCreateDTO(
                auction.getOwner(),
                NotificationTypeCodes.AUCTION_PUBLISHED,
                "Seu leilão foi publicado",
                "O leilão do item '" + auction.getItem().getName() + "' agora está ativo.",
                Map.of("auctionId", auction.getId().toString())
            ));
        }



        // 2. Notificação de 30min restantes
        LocalDateTime from = now.plusMinutes(29);
        LocalDateTime to = now.plusMinutes(31);

        List<Auction> endingSoon =
            auctionRepository.findByStatusAndEndDateBetween(
                AuctionStatus.ACTIVE,
                from,
                to
            );

        for (Auction auction : endingSoon) {

            // Notificar o dono do leilão
            notificationService.createNotification(new NotificationCreateDTO(
                auction.getOwner(),
                NotificationTypeCodes.TIME_REMAINING,
                "Seu leilão está quase terminando",
                "Faltam aproximadamente 30 minutos para o término do leilão: "
                    + auction.getItem().getName(),
                Map.of("auctionId", auction.getId().toString())
            ));

            // Notificar todos os usuários que deram lance (exceto o dono para evitar duplicidade)
            List<UUID> bidderIds = bidRepository.findAllBiddersByAuctionId(auction.getId());
            for (UUID bidderId : bidderIds) {
                if (!bidderId.equals(auction.getOwner())) {
                    notificationService.createNotification(new NotificationCreateDTO(
                        bidderId,
                        NotificationTypeCodes.TIME_REMAINING,
                        "Leilão quase terminando",
                        "O leilão do item '" + auction.getItem().getName() + "' está prestes a acabar.",
                        Map.of("auctionId", auction.getId().toString())
                    ));
                }
            }
        }

        // 3. Finalizar leilões que terminaram
        List<Auction> toFinish =
            auctionRepository.findByStatusAndEndDateLessThanEqual(
                AuctionStatus.ACTIVE,
                now
            );

        for (Auction auction : toFinish) {

            auction.waitForPayment();

            // Notificar vendedor
            notificationService.createNotification(new NotificationCreateDTO(
                auction.getOwner(),
                NotificationTypeCodes.AUCTION_FINISHED,
                "Seu leilão terminou",
                "O leilão do item '" + auction.getItem().getName() +
                "' foi encerrado. Aguardando pagamento.",
                Map.of("auctionId", auction.getId().toString())
            ));

            // Criar automaticamente o pagamento
            var payment = paymentService.createPaymentForAuction(auction);

            // Notificar vencedor (se existir lance vencedor)
            if (auction.getWinnerId() != null) {
                notificationService.createNotification(new NotificationCreateDTO(
                    auction.getWinnerId(),
                    NotificationTypeCodes.PAYMENT_CREATED,
                    "Pagamento necessário",
                    "Você venceu o leilão '" + auction.getItem().getName() + 
                    "'. Um pagamento foi gerado.",
                    Map.of("paymentId", payment.getId().toString())
                ));
            }
        }
    }
}

