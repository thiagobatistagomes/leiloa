package com.thiago.leiloa_api.dto.notification;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationFilterDTO(
    UUID userId,
    Boolean unread,            
    LocalDateTime from,
    LocalDateTime to
) {}