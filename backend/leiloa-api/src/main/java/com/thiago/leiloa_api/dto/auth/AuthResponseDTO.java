package com.thiago.leiloa_api.dto.auth;

import java.util.Set;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponseDTO {

    private String token;
    private String type;
    private Long expiresIn;

    private UUID userId;
    private String email;
    private Set<String> roles;
}
