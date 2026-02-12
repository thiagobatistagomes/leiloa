package com.thiago.leiloa_api.dto.user;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.thiago.leiloa_api.domain.role.Role;
import com.thiago.leiloa_api.domain.user.User;
import com.thiago.leiloa_api.domain.user.UserStatus;

public record UserAuditResponseDTO(
    UUID id,
    String name,
    String email,
    UserStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime lastLoginAt,
    String phoneNumber,
    Set<String> roles

    
) {
    public static UserAuditResponseDTO fromEntity(User user) {
        return new UserAuditResponseDTO(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getStatus(),
            user.getCreatedAt(),
            user.getUpdatedAt(),
            user.getLastLoginAt(),
            user.getPhoneNumber(),
            user.getRoles().stream().map(Role::getName).collect(Collectors.toSet())
        );
    }

}

