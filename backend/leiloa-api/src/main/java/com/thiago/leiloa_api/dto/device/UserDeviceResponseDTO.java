package com.thiago.leiloa_api.dto.device;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserDeviceResponseDTO(
    UUID id,
    String deviceType,
    String userAgent,
    LocalDateTime lastUsedAt,
    LocalDateTime createdAt
) {}
