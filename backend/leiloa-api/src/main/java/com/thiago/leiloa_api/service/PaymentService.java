package com.thiago.leiloa_api.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thiago.leiloa_api.domain.auction.Auction;
import com.thiago.leiloa_api.domain.auction.AuctionStatus;
import com.thiago.leiloa_api.domain.delivery.Delivery;
import com.thiago.leiloa_api.domain.delivery.DeliveryStatus;
import com.thiago.leiloa_api.domain.item.Item;
import com.thiago.leiloa_api.domain.item.ItemStatus;
import com.thiago.leiloa_api.domain.notification.NotificationTypeCodes;
import com.thiago.leiloa_api.domain.payment.Payment;
import com.thiago.leiloa_api.domain.payment.PaymentAddress;
import com.thiago.leiloa_api.domain.payment.PaymentStatus;
import com.thiago.leiloa_api.domain.user.User;
import com.thiago.leiloa_api.dto.notification.NotificationCreateDTO;
import com.thiago.leiloa_api.dto.payment.*;
import com.thiago.leiloa_api.repository.*;

import com.thiago.leiloa_api.specification.PaymentSpecification;
import com.thiago.leiloa_api.specification.UserPaymentSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final PaymentAddressRepository paymentAddressRepository;
    private final AuctionRepository auctionRepository;
    private final ItemRepository itemRepository;
    private final DeliveryRepository deliveryRepository;
    private final NotificationService notificationService;

    private static final Duration PAYMENT_DEADLINE = Duration.ofHours(48);

    // 1. Criar pagamento automaticamente após o leilão

    @Transactional
    public CreatePaymentForAuctionDTO createPaymentForAuction(Auction auction) {

        if (auction.getStatus() != AuctionStatus.WAITING_PAYMENT) {
            throw new IllegalStateException("Pagamento só pode ser criado para leilões aguardando pagamento.");
        }

        if (auction.getWinnerId() == null) {
            throw new IllegalStateException("Leilão não possui vencedor.");
        }

        Payment existing = paymentRepository.findByAuctionId(auction.getId());
        if (existing != null) {
            return CreatePaymentForAuctionDTO.fromEntity(existing);
        }

        User winner = userRepository.findById(auction.getWinnerId())
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

    // 2. Pagamentos pendentes do usuário logado
    @Transactional(readOnly = true)
    public List<MyPendingPaymentResponseDTO> listMyPendingPayments() {

        User user = authService.getAuthenticatedUser();

        return paymentRepository
                .findByWinner_IdAndStatus(user.getId(), PaymentStatus.PENDING)
                .stream()
                .map(MyPendingPaymentResponseDTO::fromEntity)
                .toList();
    }

    // 2.1 Histórico do usuário (exceto pendentes)
    @Transactional(readOnly = true)
    public Page<PaymentResponseDTO> findMyPayments(MyPaymentFilterDTO filter, Pageable pageable) {

        User user = authService.getAuthenticatedUser();

        return paymentRepository
                .findAll(UserPaymentSpecification.filter(user.getId(), filter), pageable)
                .map(payment -> {
                    PaymentAddress pa = paymentAddressRepository.findByPaymentId(payment.getId()).orElse(null);
                    return PaymentResponseDTO.fromEntity(payment, pa);
                });
    }


    // 3. Aprovar pagamento
    @Transactional
    public ApprovePaymentDTO approvePayment(UUID paymentId) {

        User user = authService.getAuthenticatedUser();

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalStateException("Pagamento não encontrado."));

        if (!payment.getWinner().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Apenas o vencedor pode realizar o pagamento.");
        }

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException("Pagamento não está pendente.");
        }

        // Verifica se endereço foi definido
        PaymentAddress pa = paymentAddressRepository
                .findByPaymentId(payment.getId())
                .orElseThrow(() -> new IllegalStateException("Defina um endereço antes de finalizar o pagamento."));

        // Marca como pago
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setPaidAt(LocalDateTime.now());

        // Notificar vendedor
        notificationService.createNotification(new NotificationCreateDTO(
                payment.getAuction().getOwner(),
                NotificationTypeCodes.PAYMENT_APPROVED,
                "Pagamento aprovado",
                "O pagamento do leilão '" + payment.getAuction().getItem().getName() + "' foi aprovado.",
                Map.of("paymentId", payment.getId().toString())
        ));

        // Atualiza auction e item
        Auction auction = payment.getAuction();
        auction.setStatus(AuctionStatus.SOLD);

        Item item = auction.getItem();
        item.setStatus(ItemStatus.SOLD);

        auctionRepository.save(auction);
        itemRepository.save(item);

        // Criação automática da entrega
        if (!deliveryRepository.existsByPayment_Id(payment.getId())) {

            Delivery delivery = DeliveryFactory.create(payment, pa);
            deliveryRepository.save(delivery);
        }

        return ApprovePaymentDTO.fromEntity(payment);
    }

    // 4. Expirar pagamentos após 48h
    @Transactional
    public int expirePendingPayments() {

        LocalDateTime limit = LocalDateTime.now().minus(PAYMENT_DEADLINE);

        var pendings = paymentRepository.findPendingPaymentsCreatedBefore(limit);

        pendings.forEach(payment -> {
            payment.setStatus(PaymentStatus.EXPIRED);
            payment.setExpiredAt(LocalDateTime.now());

            notificationService.createNotification(new NotificationCreateDTO(
                payment.getAuction().getOwner(), 
                NotificationTypeCodes.PAYMENT_EXPIRED,
                "Pagamento expirado",
                "O pagamento do leilão '" + payment.getAuction().getItem().getName() +
                    "' expirou e o vencedor não realizou o pagamento.",
                Map.of("paymentId", payment.getId().toString())
            ));

            Auction auction = payment.getAuction();
            auction.setStatus(AuctionStatus.EXPIRED);

            auctionRepository.save(auction);
        });

        return pendings.size();
    }

    // 5. Cancelamento manual
    @Transactional
    public CancelPaymentDTO cancelPayment(UUID paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalStateException("Pagamento não encontrado."));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException("Somente pagamentos pendentes podem ser cancelados.");
        }

        payment.setStatus(PaymentStatus.CANCELLED);

        Auction auction = payment.getAuction();
        UUID ownerId = auction.getOwner();
        String itemName = auction.getItem().getName();

        notificationService.createNotification(new NotificationCreateDTO(
            ownerId,
            NotificationTypeCodes.PAYMENT_CANCELLED,
            "Pagamento cancelado",
            "O administrador cancelou o pagamento do seu leilão: " + itemName,
            Map.of("paymentId", payment.getId().toString())
        ));

        return CancelPaymentDTO.fromEntity(payment);
    }


    // 6. Auditoria admin
    @Transactional(readOnly = true)
    public PaymentResponseDTO getByAuction(UUID auctionId) {
        Payment payment = paymentRepository.findByAuctionId(auctionId);
        PaymentAddress pa = payment != null
                ? paymentAddressRepository.findByPaymentId(payment.getId()).orElse(null)
                : null;
        return payment != null ? PaymentResponseDTO.fromEntity(payment, pa) : null;
    }

    @Transactional(readOnly = true)
    public PaymentResponseDTO getById(UUID id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Pagamento não encontrado."));

        PaymentAddress pa = paymentAddressRepository.findByPaymentId(payment.getId()).orElse(null);

        return PaymentResponseDTO.fromEntity(payment, pa);
    }

    @Transactional(readOnly = true)
    public Page<PaymentResponseDTO> search(PaymentFilterDTO filter, Pageable pageable) {
        return paymentRepository
                .findAll(PaymentSpecification.filter(filter), pageable)
                .map(payment -> {
                    PaymentAddress pa = paymentAddressRepository.findByPaymentId(payment.getId()).orElse(null);
                    return PaymentResponseDTO.fromEntity(payment, pa);
                });
    }

 
    // FACTORY DE DELIVERY
    public static class DeliveryFactory {
        public static Delivery create(Payment payment, PaymentAddress address) {
            Delivery d = new Delivery();
            d.setPayment(payment);
            d.setAddress(address);
            d.setStatus(DeliveryStatus.PENDING);
            d.setDeliveryMethod("STANDARD");
            return d;
        }
    }
}
