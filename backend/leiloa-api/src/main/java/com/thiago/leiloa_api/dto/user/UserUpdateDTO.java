package com.thiago.leiloa_api.dto.user;

import jakarta.validation.constraints.NotBlank;

public record UserUpdateDTO(
    @NotBlank(message = "O nome não pode ser vazio.")
    String name
) {}

