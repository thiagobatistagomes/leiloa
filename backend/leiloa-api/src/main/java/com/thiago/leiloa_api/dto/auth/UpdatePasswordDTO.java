package com.thiago.leiloa_api.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatePasswordDTO(
        @NotBlank
        String currentPassword,

        @NotBlank
        @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
        String newPassword
) {}

