package com.thiago.leiloa_api.controller;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.thiago.leiloa_api.dto.notification.NotificationFilterDTO;
import com.thiago.leiloa_api.dto.notification.NotificationResponseDTO;
import com.thiago.leiloa_api.service.AuthService;
import com.thiago.leiloa_api.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Tag(name = "Notifications", description = "Endpoints para gerenciamento de notificações")
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final AuthService authService;


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/unread")
    @Operation(summary = "Listar notificações não lidas", description = "Retorna uma página de notificações não lidas para o usuário autenticado.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Notificações não lidas listadas com sucesso"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão para acessar as notificações"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Page<NotificationResponseDTO>> listUnread(
            Pageable pageable,
            @RequestParam UUID userId
    ) {
        return ResponseEntity.ok(notificationService.listUnreads(userId, pageable));
    }

  
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/all")
    @Operation(summary = "Listar todas as notificações", description = "Retorna uma página de todas as notificações para o usuário autenticado.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Notificações listadas com sucesso"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão para acessar as notificações"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Page<NotificationResponseDTO>> listAll(
            NotificationFilterDTO filter,
            Pageable pageable
    ) {
        return ResponseEntity.ok(notificationService.listAll(filter, pageable));
    }



    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{id}/read")
    @Operation(summary = "Marcar notificação como lida", description = "Marca uma notificação específica como lida para o usuário autenticado.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Notificação marcada como lida com sucesso"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão para marcar a notificação como lida"),
        @ApiResponse(responseCode = "404", description = "Notificação não encontrada"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> read(
            @PathVariable UUID id,
            @RequestParam UUID userId
    ) {
        notificationService.markAsRead(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/read-all")
    @Operation(summary = "Marcar todas as notificações como lidas", description = "Marca todas as notificações como lidas para o usuário autenticado.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Notificações marcadas como lidas com sucesso"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão para marcar as notificações como lidas"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> readAll(@RequestParam UUID userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.noContent().build();
    }


    // o frontend deve armazenar o timestamp do último poll bem sucedido e enviá-lo na próxima requisição.
    @Operation(
        summary = "Polling de novas notificações",
        description = """
            Retorna notificações criadas APÓS o timestamp informado.
            
            Este endpoint implementa o sistema de *polling leve* utilizado pelo frontend
            para receber notificações em tempo quase real.

            - NÃO retorna notificações já lidas
            - NÃO retorna notificações já retornadas em polls anteriores
            - NÃO retorna todas as notificações: apenas as novas, criadas depois do timestamp enviado
            - NÃO interfere no método listUnreads (que serve para listar notificações já recebidas)
            
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de novas notificações",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = NotificationResponseDTO.class))
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Timestamp inválido"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Usuário não autenticado"
        )
    })
    @GetMapping("/poll")
    public List<NotificationResponseDTO> poll(
            @Parameter(
                description = "Timestamp ISO-8601. Exemplo: 2026-02-20T13:20:00",
                required = true,
                example = "2026-02-20T13:20:00"
            )
            @RequestParam("after") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime after
    ) {
        UUID userId = authService.getAuthenticatedUser().getId();
        return notificationService.poll(userId, after);
    }
}