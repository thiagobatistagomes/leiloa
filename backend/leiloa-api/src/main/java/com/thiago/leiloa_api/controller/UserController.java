package com.thiago.leiloa_api.controller;



import java.util.UUID;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thiago.leiloa_api.dto.user.UpdatePhoneDTO;
import com.thiago.leiloa_api.dto.user.UserAuditResponseDTO;
import com.thiago.leiloa_api.dto.user.UserFilterDTO;
import com.thiago.leiloa_api.dto.user.UserResponseDTO;
import com.thiago.leiloa_api.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(
    name = "User Management",
    description = "Endpoints responsáveis pela gestão de usuários, incluindo desativação de conta, atualização de telefone e auditoria de usuários (admin)"
)
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PatchMapping("/me/deactivate")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Desativar minha conta",
        description = "Permite que o usuário autenticado desative sua própria conta. Usuários INACTIVE não podem mais fazer login."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Conta desativada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<UserResponseDTO> deactivateMyAccount() {
        return ResponseEntity.ok(userService.deactivateMyAccount());
    }

    @PutMapping("/me/phone")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Atualizar número de telefone",
        description = "Permite que o usuário autenticado atualize seu número de telefone."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Número de telefone atualizado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<UserResponseDTO> updatePhoneNumber(@Valid @RequestBody UpdatePhoneDTO dto) {
        return ResponseEntity.ok(userService.updatePhoneNumber(dto));
    }

    @PatchMapping("/{userId}/block")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Bloquear usuário",
        description = "Permite que um administrador bloqueie a conta de um usuário específico. Usuários BLOCKED não podem mais fazer login."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuário bloqueado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autenticado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<UserResponseDTO> blockUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.blockUser(userId));
    }

    @PatchMapping("/{userId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Reativar usuário",
        description = "Admin reativa um usuário, mudando seu status para ACTIVE."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuário reativado"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<UserResponseDTO> activateUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.activateUser(userId));
    }

    @GetMapping("/admin/audit")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Auditar usuários",
        description = "Permite que um administrador audite os usuários do sistema."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Auditoria de usuários retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autenticado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<Page<UserAuditResponseDTO>> auditUser(
        @ParameterObject @ModelAttribute UserFilterDTO filter,
        @ParameterObject
        @PageableDefault(
                page = 0,
                size = 10,
                sort = "createdAt",
                direction = Sort.Direction.ASC
        )
        Pageable pageable
    ) {
        return ResponseEntity.ok(userService.auditUsers(filter, pageable));
    }

}

