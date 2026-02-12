package com.thiago.leiloa_api.dto.user;

import java.time.LocalDateTime;

import com.thiago.leiloa_api.domain.user.UserStatus;

public record UserFilterDTO(
    String name,
    String email,
    UserStatus status,
    String role,
    LocalDateTime createdFrom,
    LocalDateTime createdTo,
    LocalDateTime lastLoginFrom,
    LocalDateTime lastLoginTo
) {}
