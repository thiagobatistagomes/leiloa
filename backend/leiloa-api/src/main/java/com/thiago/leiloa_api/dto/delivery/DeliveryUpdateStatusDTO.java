package com.thiago.leiloa_api.dto.delivery;

import com.thiago.leiloa_api.domain.delivery.DeliveryStatus;

import lombok.Getter;

@Getter
public class DeliveryUpdateStatusDTO {
    private DeliveryStatus status;
}

