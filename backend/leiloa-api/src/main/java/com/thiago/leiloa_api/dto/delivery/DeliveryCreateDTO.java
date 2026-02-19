package com.thiago.leiloa_api.dto.delivery;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeliveryCreateDTO {
    private UUID paymentId;
    private UUID paymentAddressId;
    private String deliveryMethod; 
}
