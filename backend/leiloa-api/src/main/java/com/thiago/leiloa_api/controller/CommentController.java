package com.thiago.leiloa_api.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thiago.leiloa_api.domain.comment.CommentStatus;
import com.thiago.leiloa_api.dto.comment.CommentResponseDTO;
import com.thiago.leiloa_api.dto.comment.CommentTreeDTO;
import com.thiago.leiloa_api.dto.comment.CreateCommentDTO;
import com.thiago.leiloa_api.service.CommentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@Validated
@Tag(name = "Comments", description = "Endpoints relacionados a comentários em leilões")
public class CommentController {

    private final CommentService commentService;

   
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Criar um comentário",
        description = "Permite que um usuário autenticado crie um comentário em um leilão ativo ou agendado."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Comentário criado com sucesso",
            content = @Content(schema = @Schema(implementation = CommentResponseDTO.class))
        ),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou regra de negócio violada"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão para criar comentários")
    })
    public ResponseEntity<CommentResponseDTO> createComment(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Dados necessários para criar um novo comentário",
            required = true
        )
        @RequestBody @Valid CreateCommentDTO dto
    ) {
        CommentResponseDTO response = commentService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Listar meus comentários",
        description = "Retorna uma lista paginada dos comentários criados pelo usuário autenticado, com opção de filtro por leilão."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lista de comentários retornada com sucesso",
            content = @Content(schema = @Schema(implementation = CommentResponseDTO.class))
        ),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão para acessar este recurso"),
        @ApiResponse(responseCode = "404", description = "Leilão não encontrado, se filtro por leilão for aplicado")
    })
    public Page<CommentResponseDTO> getMyComments(
            @Parameter(
                description = "ID do usuário autenticado (obtido do token)",
                example = "550e8400-e29b-41d4-a716-446655440000"
            )
            UUID userId,
            @Parameter(
                description = "ID opcional do leilão para filtrar os comentários",
                example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @RequestParam(required = false) UUID auctionId,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            @ParameterObject
            Pageable pageable
    ) {
        return commentService.getMyComments(auctionId, pageable);
    }

    @GetMapping("/auction/{auctionId}")
    @Operation(
        summary = "Listar comentários públicos de um leilão",
        description = "Retorna uma lista paginada dos comentários públicos de um leilão específico."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lista de comentários públicos retornada com sucesso",
            content = @Content(schema = @Schema(implementation = CommentResponseDTO.class))
        ),
        @ApiResponse(responseCode = "404", description = "Leilão não encontrado")
    })
    public Page<CommentResponseDTO> getPublicCommentsByAuction(
            @PathVariable UUID auctionId,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            @ParameterObject
            Pageable pageable
    ) {
        return commentService.getPublicCommentsByAuction(auctionId, pageable);
    }

   

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Pesquisar comentários com múltiplos filtros (Admin)",
        description = "Permite que administradores pesquisem comentários usando múltiplos filtros, como ID do leilão, ID do usuário, status do comentário, texto de busca e intervalo de datas."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lista de comentários retornada com sucesso",
            content = @Content(schema = @Schema(implementation = CommentResponseDTO.class))
        ),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão para acessar este recurso")
    })
    public Page<CommentResponseDTO> adminSearch(
            @RequestParam(required = false) UUID auctionId,
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) List<CommentStatus> status,
            @RequestParam(required = false) UUID parentId,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @PageableDefault(page = 0, size = 10, sort= "createdAt", direction = Sort.Direction.DESC)
            @ParameterObject Pageable pageable
    ) {
        return commentService.adminSearch(auctionId, userId, status, parentId, query, startDate, endDate, pageable);
    }


    @GetMapping("/{commentId}/replies")
    @Operation(
        summary = "Listar respostas públicas de um comentário",
        description = "Retorna uma lista paginada das respostas públicas associadas a um comentário específico."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lista de respostas públicas retornada com sucesso",
            content = @Content(schema = @Schema(implementation = CommentResponseDTO.class))
        ),
        @ApiResponse(responseCode = "404", description = "Comentário não encontrado")
    })
    public Page<CommentResponseDTO> getPublicReplies(
            @PathVariable UUID commentId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.ASC)
            @ParameterObject
            Pageable pageable
    ) {
        return commentService.getPublicReplies(commentId, pageable);
    }


    // Lista hierárquica de comentários
    @GetMapping("/auction/{auctionId}/tree")
    @Operation(
        summary = "Listar hierarquicamente todos os comentários visíveis de um leilão",
        description = "Retorna uma lista hierárquica dos comentários visíveis do leilão (ex: comentário + resposta + resposta da resposta etc.)"
    )
    @ApiResponse(responseCode = "200", description = "Lista de respostas hierárquicas retornada com sucesso")
    @ApiResponse(responseCode = "404", description = "Leilão não encontrado")
    public ResponseEntity<List<CommentTreeDTO>> getTree(@PathVariable UUID auctionId) {
        return ResponseEntity.ok(commentService.getCommentTree(auctionId));
    }

  
    @PutMapping("edit/{id}")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Atualizar conteúdo de comentário",
        description = "Permite que um usuário autenticado atualize o conteúdo de um comentário criado por ele mesmo."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Comentário atualizado com sucesso",
            content = @Content(schema = @Schema(implementation = CommentResponseDTO.class))
        ),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou regra de negócio violada"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão para editar este comentário"),
        @ApiResponse(responseCode = "404", description = "Comentário não encontrado")
    })
    public ResponseEntity<CommentResponseDTO> updateContent(
        @PathVariable UUID id,
        @Parameter(
            description = "Novo conteúdo do comentário",
            example = "Este é o novo conteúdo do meu comentário atualizado."
        )
        @RequestBody String newContent
    ) {
        CommentResponseDTO response = commentService.updateContent(id, newContent);
        return ResponseEntity.ok(response);
    }


    // AÇÕES DO USUÁRIO: OCULTAR, REEXIBIR, EXCLUIR COMENTÁRIOS

    @PatchMapping("/{id}/hide")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Ocultar comentário",
        description = "Oculta um comentário criado pelo usuário."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Comentário ocultado com sucesso",
            content = @Content(schema = @Schema(implementation = CommentResponseDTO.class))
        ),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão para ocultar este comentário"),
        @ApiResponse(responseCode = "404", description = "Comentário não encontrado")
    })
    public ResponseEntity<CommentResponseDTO> hide(
        @PathVariable UUID id
    ) {
        return ResponseEntity.ok(CommentResponseDTO.fromEntity(commentService.hide(id)));
    }

    @PatchMapping("/{id}/unhide")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Reexibir comentário",
        description = "Torna visível novamente um comentário ocultado pelo usuário."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Comentário reexibido com sucesso",
            content = @Content(schema = @Schema(implementation = CommentResponseDTO.class))
        ),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão para reexibir este comentário"),
        @ApiResponse(responseCode = "404", description = "Comentário não encontrado")
    })
    public ResponseEntity<CommentResponseDTO> unhide(
        @PathVariable UUID id
    ) {
        return ResponseEntity.ok(CommentResponseDTO.fromEntity(commentService.unhide(id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Excluir comentário",
    description = "Exclui permanentemente (soft delete) um comentário criado pelo usuário."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Comentário excluído com sucesso",
            content = @Content(schema = @Schema(implementation = CommentResponseDTO.class))
        ),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão para excluir este comentário"),
        @ApiResponse(responseCode = "404", description = "Comentário não encontrado")
    })
    public ResponseEntity<CommentResponseDTO> deleteUser(
        @PathVariable UUID id
    ) {
        return ResponseEntity.ok(CommentResponseDTO.fromEntity(commentService.deleteUser(id)));
    }


    // AÇÕES DO ADMINISTRADOR: BLOQUEAR, DESBLOQUEAR COMENTÁRIOS

    @PatchMapping("/{id}/block")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Bloquear comentário",
        description = "Bloqueia um comentário (apenas administradores)."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Comentário bloqueado com sucesso",
            content = @Content(schema = @Schema(implementation = CommentResponseDTO.class))
        ),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão para bloquear este comentário"),
        @ApiResponse(responseCode = "404", description = "Comentário não encontrado")
    })
    public ResponseEntity<CommentResponseDTO> block(
        @PathVariable UUID id
    ) {
        return ResponseEntity.ok(CommentResponseDTO.fromEntity(commentService.block(id)));
    }

    @PatchMapping("/{id}/unblock")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Desbloquear comentário",
        description = "Remove o bloqueio de um comentário (apenas administradores)."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Comentário desbloqueado com sucesso",
            content = @Content(schema = @Schema(implementation = CommentResponseDTO.class))
        ),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão para desbloquear este comentário"),
        @ApiResponse(responseCode = "404", description = "Comentário não encontrado")
    })
    public ResponseEntity<CommentResponseDTO> unblock(
        @PathVariable UUID id
    ) {
        return ResponseEntity.ok(CommentResponseDTO.fromEntity(commentService.unblock(id)));
    }

}
