package com.thiago.leiloa_api.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.thiago.leiloa_api.domain.payment.PaymentAddress;

public interface PaymentAddressRepository extends JpaRepository<PaymentAddress, UUID> {
    Optional<PaymentAddress> findByPaymentId(UUID id);
    boolean existsByPaymentId(UUID id);
}

