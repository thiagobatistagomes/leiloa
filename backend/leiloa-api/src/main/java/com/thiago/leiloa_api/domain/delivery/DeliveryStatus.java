package com.thiago.leiloa_api.domain.delivery;

import java.util.EnumSet;
import java.util.Set;

public enum DeliveryStatus {
    PENDING,
    PROCESSING,
    SHIPPED,
    IN_TRANSIT,
    OUT_FOR_DELIVERY,
    DELIVERED,
    RETURN_REQUESTED,
    RETURNED;

    public Set<DeliveryStatus> nextAllowedStatuses() {
        return switch (this) {
            case PENDING -> EnumSet.of(PROCESSING);
            case PROCESSING -> EnumSet.of(SHIPPED);
            case SHIPPED -> EnumSet.of(IN_TRANSIT);
            case IN_TRANSIT -> EnumSet.of(OUT_FOR_DELIVERY);
            case OUT_FOR_DELIVERY -> EnumSet.of(DELIVERED);
            case DELIVERED -> EnumSet.of(RETURN_REQUESTED);
            case RETURN_REQUESTED -> EnumSet.of(RETURNED);
            case RETURNED -> EnumSet.noneOf(DeliveryStatus.class); // final
        };
    }
}


