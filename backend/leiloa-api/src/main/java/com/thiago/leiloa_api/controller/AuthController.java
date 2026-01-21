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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

// Controlador responsável por autenticação (login e registro)

@Tag(
    name = "Authentication",
    description = "Endpoints responsáveis por autenticação e registro de usuários"
)
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
        summary = "Autenticar usuário (login)",
        description = """
            Realiza a autenticação de um usuário já cadastrado no sistema.
            
            Em caso de sucesso, retorna um token JWT que deve ser enviado
            no header Authorization das demais requisições protegidas.
            
            Exemplo de header:
            Authorization: Bearer <token>
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Credenciais inválidas")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Credenciais do usuário (email e senha)",
            required = true
        )
        @RequestBody @Valid LoginDTO request
    ) {
        AuthResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Registrar novo usuário",
        description = """
            Realiza o cadastro de um novo usuário no sistema.
            
            Após o registro bem-sucedido, o usuário já recebe um token JWT
            válido para autenticação nas demais rotas protegidas.
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuário registrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou usuário já existente")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Dados necessários para registrar um novo usuário",
            required = true
        )
        @RequestBody @Valid RegisterUserDTO request
    ) {
        AuthResponseDTO response = authService.register(request);
        return ResponseEntity.ok(response);
    }
}

