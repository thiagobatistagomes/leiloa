package com.thiago.leiloa_api.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdatePhoneDTO(
        @NotBlank(message = "Número de telefone é obrigatório")
        @Size(min = 10, max = 15, message = "Número de telefone deve ter entre 10 e 15 caracteres")
        @Pattern(
            regexp = "^\\(?[1-9]{2}\\)?\\s?9\\d{4}-?\\d{4}$",
            message = "Telefone inválido (use padrão brasileiro com DDD)"
        )
        String phoneNumber
) {}
