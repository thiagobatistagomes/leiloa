package com.thiago.leiloa_api.dto.device;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserDeviceAdminDetailDTO(
    UUID id,
    UUID userId,
    String userEmail,
    String deviceType,
    String deviceToken,
    String userAgent,
    LocalDateTime createdAt,
    LocalDateTime lastUsedAt
) {}