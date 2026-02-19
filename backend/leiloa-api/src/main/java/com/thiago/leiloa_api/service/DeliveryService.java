package com.thiago.leiloa_api.service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thiago.leiloa_api.domain.delivery.Delivery;
import com.thiago.leiloa_api.domain.delivery.DeliveryStatus;

import com.thiago.leiloa_api.dto.delivery.*;
import com.thiago.leiloa_api.repository.DeliveryRepository;

import com.thiago.leiloa_api.specification.DeliverySpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    // 1. Verificar status permitidos para transição
    @Transactional(readOnly = true)
    public AllowedStatusesDTO getAllowedStatuses(UUID deliveryId) {

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new IllegalStateException("Entrega não encontrada."));

        DeliveryStatus current = delivery.getStatus();
        Set<DeliveryStatus> allowed = current.nextAllowedStatuses();

        return new AllowedStatusesDTO(current, allowed);
    }


    // 2. Buscar delivery por ID
    @Transactional(readOnly = true)
    public DeliveryResponseDTO findById(UUID id) {
        Delivery d = deliveryRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Entrega não encontrada."));
        return DeliveryResponseDTO.fromEntity(d);
    }


    // 3. Buscar delivery por Payment ID (admin e auditoria)
    @Transactional(readOnly = true)
    public DeliveryResponseDTO findByPayment(UUID paymentId) {

        Delivery d = deliveryRepository.findByPayment_Id(paymentId)
                .orElseThrow(() -> new IllegalStateException("Entrega não encontrada para esse pagamento."));

        return DeliveryResponseDTO.fromEntity(d);
    }




    // 4. Atualizar status
    @Transactional
    public DeliveryResponseDTO updateStatus(UUID id, DeliveryUpdateStatusDTO dto) {

        Delivery d = deliveryRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Entrega não encontrada."));

        DeliveryStatus newStatus = dto.getStatus();
        DeliveryStatus current = d.getStatus();

        // 1. Verificar se a transição é permitida
        if (!current.nextAllowedStatuses().contains(newStatus)) {
            throw new IllegalStateException(
                    "Transição ilegal: não é permitido mudar de " + current + " para " + newStatus
            );
        }

        // 2. Aplicar timestamps específicos
        LocalDateTime now = LocalDateTime.now();

        switch (newStatus) {
            case PENDING -> d.setPendingAt(now);
            case PROCESSING -> d.setProcessingAt(now);
            case SHIPPED -> d.setShippedAt(now);
            case IN_TRANSIT -> d.setInTransitAt(now);
            case OUT_FOR_DELIVERY -> d.setOutForDeliveryAt(now);
            case DELIVERED -> d.setDeliveredAt(now);
            case RETURN_REQUESTED -> d.setReturnRequestedAt(now);
            case RETURNED -> d.setReturnedAt(now);
        }

        // 3. Atualizar status (Se for SHIPPED criará um tracking code automaticamente se não tiver)
        d.setStatus(newStatus);
        if (newStatus == DeliveryStatus.SHIPPED && (d.getTrackingCode() == null || d.getTrackingCode().isBlank())) {
            d.setTrackingCode(generateTrackingCode());
        }

        return DeliveryResponseDTO.fromEntity(deliveryRepository.save(d));
    }



    // 5. Busca com filtros (paginação)
    @Transactional(readOnly = true)
    public Page<DeliveryListResponseDTO> search(DeliveryFilterDTO filter, Pageable pageable) {

        return deliveryRepository
                .findAll(DeliverySpecification.filter(filter), pageable)
                .map(DeliveryListResponseDTO::fromEntity);
    }

    private String generateTrackingCode() {
        return "BR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

}

