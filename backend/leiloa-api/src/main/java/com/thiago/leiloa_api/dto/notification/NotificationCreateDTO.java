package com.thiago.leiloa_api.dto.notification;

import java.util.UUID;
import java.util.Map;

public record NotificationCreateDTO(
    UUID userId,
    String typeCode,
    String title,
    String message,
    Map<String, Object> data
) {}