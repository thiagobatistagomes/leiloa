package com.thiago.leiloa_api.dto.notification;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationResponseDTO(
    UUID id,
    String typeCode,
    String title,
    String message,
    Object data,
    LocalDateTime createdAt,
    LocalDateTime readAt
) {}