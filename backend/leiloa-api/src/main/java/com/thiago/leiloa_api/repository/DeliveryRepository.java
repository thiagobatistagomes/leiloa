package com.thiago.leiloa_api.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.thiago.leiloa_api.domain.delivery.Delivery;
import com.thiago.leiloa_api.domain.payment.Payment;


public interface DeliveryRepository 
        extends JpaRepository<Delivery, UUID>, JpaSpecificationExecutor<Delivery> {

    Optional<Delivery> findByPayment_Id(UUID paymentId);

    boolean existsByPayment_Id(Payment paymentId);

    boolean existsByPayment_Id(UUID paymentId);
}
