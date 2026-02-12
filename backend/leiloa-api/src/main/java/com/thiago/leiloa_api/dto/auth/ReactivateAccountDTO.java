package com.thiago.leiloa_api.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ReactivateAccountDTO(

        @Email @NotBlank
        String email,

        @NotBlank
        String password
) {}

