package com.thiago.leiloa_api.dto.delivery;

import java.util.Set;
import com.thiago.leiloa_api.domain.delivery.DeliveryStatus;

public record AllowedStatusesDTO(
        DeliveryStatus currentStatus,
        Set<DeliveryStatus> allowedNextStatuses
) {}

