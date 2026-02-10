package com.thiago.leiloa_api.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thiago.leiloa_api.domain.auction.Auction;
import com.thiago.leiloa_api.domain.auction.AuctionStatus;
import com.thiago.leiloa_api.domain.user.User;

import com.thiago.leiloa_api.repository.PaymentRepository;
import com.thiago.leiloa_api.repository.UserRepository;
import com.thiago.leiloa_api.specification.PaymentSpecification;

import lombok.RequiredArgsConstructor;
import com.thiago.leiloa_api.domain.payment.Payment;
import com.thiago.leiloa_api.domain.payment.PaymentStatus;
import com.thiago.leiloa_api.dto.payment.PaymentResponseDTO;
import com.thiago.leiloa_api.dto.payment.CreatePaymentForAuctionDTO;
import com.thiago.leiloa_api.dto.payment.PaymentFilterDTO;
import com.thiago.leiloa_api.dto.payment.ApprovePaymentDTO;
import com.thiago.leiloa_api.dto.payment.CancelPaymentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;



@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    private static final Duration PAYMENT_DEADLINE = Duration.ofHours(48);

    // 1. Criar pagamento automaticamente após o leilão ser finalizado
    @Transactional
    public CreatePaymentForAuctionDTO createPaymentForAuction(Auction auction) {

        if (auction.getStatus() != AuctionStatus.FINISHED) {
            throw new IllegalStateException("Pagamento só pode ser criado para leilões FINALIZADOS.");
        }

        UUID winnerId = auction.getWinnerId();
        if (winnerId == null) {
            throw new IllegalStateException("Leilão finalizado sem vencedor — nenhum pagamento será criado.");
        }

        if (paymentRepository.existsByAuctionId(auction.getId())) {
            Payment existing = paymentRepository.findByAuctionId(auction.getId());
            return CreatePaymentForAuctionDTO.fromEntity(existing);
        }

        User winner = userRepository
                .findById(winnerId)
                .orElseThrow(() -> new IllegalStateException("Vencedor não encontrado."));

        Payment payment = new Payment();
        payment.setAuction(auction);
        payment.setWinner(winner);
        payment.setValue(auction.getWinningBid());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(LocalDateTime.now());

        Payment saved = paymentRepository.save(payment);

        return CreatePaymentForAuctionDTO.fromEntity(saved);
    }


    // 2. Simular pagamento (usuário paga)
    @Transactional
    public ApprovePaymentDTO approvePayment(UUID paymentId, UUID userId) {

        Payment payment = paymentRepository
                .findById(paymentId)
                .orElseThrow(() -> new IllegalStateException("Pagamento não encontrado."));

        if (!payment.getWinner().getId().equals(userId)) {
            throw new IllegalArgumentException("Apenas o vencedor pode realizar o pagamento.");
        }

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException("Pagamento não está mais pendente.");
        }

        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setPaidAt(LocalDateTime.now());

        return ApprovePaymentDTO.fromEntity(payment);
    }


    // 3. Expirar pagamentos pendentes após 48h
    @Transactional
    public int expirePendingPayments() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime limit = now.minus(PAYMENT_DEADLINE);

        var pendings = paymentRepository.findPendingPaymentsCreatedBefore(limit);

        pendings.forEach(payment -> {
            payment.setStatus(PaymentStatus.EXPIRED);
            payment.setExpiredAt(now);
        });

        return pendings.size();
    }

 
    // 4. Cancelamento manual pelo admin
    @Transactional
    public CancelPaymentDTO cancelPayment(UUID paymentId) {

        Payment payment = paymentRepository
                .findById(paymentId)
                .orElseThrow(() -> new IllegalStateException("Pagamento não encontrado."));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException("Somente pagamentos pendentes podem ser cancelados.");
        }

        payment.setStatus(PaymentStatus.CANCELLED);

        return CancelPaymentDTO.fromEntity(payment);
    }

    // 5. Auditoria (admin)
    public PaymentResponseDTO getByAuction(UUID auctionId) {
        Payment payment = paymentRepository.findByAuctionId(auctionId);
        return payment != null ? PaymentResponseDTO.fromEntity(payment) : null;
    }

    public PaymentResponseDTO getById(UUID id) {
        Payment payment = paymentRepository
                .findById(id)
                .orElseThrow(() -> new IllegalStateException("Pagamento não encontrado."));

        return PaymentResponseDTO.fromEntity(payment);
    }

    public Page<PaymentResponseDTO> search(PaymentFilterDTO filter, Pageable pageable) {
        return paymentRepository
                .findAll(PaymentSpecification.filter(filter), pageable)
                .map(PaymentResponseDTO::fromEntity);
    }

}
