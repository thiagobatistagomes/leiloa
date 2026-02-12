package com.thiago.leiloa_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thiago.leiloa_api.dto.auth.AuthResponseDTO;
import com.thiago.leiloa_api.dto.auth.LoginDTO;
import com.thiago.leiloa_api.dto.auth.ReactivateAccountDTO;
import com.thiago.leiloa_api.dto.auth.RegisterUserDTO;
import com.thiago.leiloa_api.dto.auth.UpdatePasswordDTO;
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

    
    @PostMapping("/login")
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

    
    @PostMapping("/register")
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

    
    @PatchMapping("/update-password")
    @Operation(
            summary = "Atualizar senha do usuário",
            description = "Atualiza a senha de um usuário autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Senha atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Senha atual incorreta ou nova senha inválida"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
    public ResponseEntity<Void> updatePassword(
            @Valid @RequestBody UpdatePasswordDTO dto
    ) {
        authService.updatePassword(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reactivate")
    @Operation(
            summary = "Reativar conta inativa",
            description = "Reativa uma conta com status INACTIVE após validar credenciais."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conta reativada e usuário autenticado"),
            @ApiResponse(responseCode = "400", description = "Credenciais inválidas"),
            @ApiResponse(responseCode = "401", description = "Conta não está inativa")
    })
    public ResponseEntity<AuthResponseDTO> reactivateAccount(
            @Valid @RequestBody ReactivateAccountDTO dto
    ) {
        return ResponseEntity.ok(authService.reactivateAccount(dto));
    }

}

