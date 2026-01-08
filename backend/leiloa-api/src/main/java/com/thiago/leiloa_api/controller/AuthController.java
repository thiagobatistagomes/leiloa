package com.thiago.leiloa_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thiago.leiloa_api.dto.auth.AuthResponseDTO;
import com.thiago.leiloa_api.dto.auth.LoginDTO;
import com.thiago.leiloa_api.dto.auth.RegisterUserDTO;
import com.thiago.leiloa_api.service.AuthService;

import jakarta.validation.Valid;

// Controlador responsável por autenticação (login e registro)

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @RequestBody @Valid LoginDTO request
    ) {
        AuthResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    // REGISTER
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(
            @RequestBody @Valid RegisterUserDTO request
    ) {
        AuthResponseDTO response = authService.register(request);
        return ResponseEntity.ok(response);
    }
}
