package com.thiago.leiloa_api.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.thiago.leiloa_api.domain.payment.Payment;
import com.thiago.leiloa_api.domain.payment.PaymentStatus;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID>, JpaSpecificationExecutor<Payment> {

    // Verifica se já existe pagamento para um leilão (evita duplicidade)
    boolean existsByAuctionId(UUID auctionId);

    // Recupera pagamento único pelo id do leilão
    Payment findByAuctionId(UUID auctionId);

    // Busca pagamentos pendentes criados antes de determinado horário (para expiração)
    @Query("""
        SELECT p FROM Payment p
        WHERE p.status = 'PENDING'
        AND p.createdAt < :limit
    """)
    List<Payment> findPendingPaymentsCreatedBefore(LocalDateTime limit);

    // Auditoria — buscar por status
    List<Payment> findByStatus(PaymentStatus status);

    // Auditoria — buscar todos de um vencedor específico
    List<Payment> findByWinnerId(UUID winnerId);
}

