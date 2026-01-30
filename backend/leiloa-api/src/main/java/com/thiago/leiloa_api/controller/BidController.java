package com.thiago.leiloa_api.controller;


import java.util.UUID;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thiago.leiloa_api.dto.bid.BidPublicResponseDTO;
import com.thiago.leiloa_api.dto.bid.BidResponseDTO;
import com.thiago.leiloa_api.dto.bid.CreateBidDTO;
import com.thiago.leiloa_api.service.AuthService;
import com.thiago.leiloa_api.service.BidService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/bids")
@RequiredArgsConstructor
@Validated
@Tag(name = "Bids", description = "Endpoints relacionados a lances em leilões")
public class BidController {

    private final BidService bidService;
    private final AuthService authService;


    @PostMapping("/place-bid")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Realizar um lance",
        description = "Cria um novo lance para um leilão ativo. O valor deve respeitar o incremento mínimo e ser maior que o lance atual."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "201", description = "Lance criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Regra de negócio violada")
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    @ApiResponse(responseCode = "403", description = "Usuário sem permissão")
    @ApiResponse(responseCode = "404", description = "Leilão não encontrado")
    public ResponseEntity<BidResponseDTO> placeBid(
            @RequestBody @Valid CreateBidDTO dto
    ) {
        BidResponseDTO response = bidService.placeBid(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping("/public/{auctionId}")
    @Operation(
        summary = "Listar lances públicos de um leilão",
        description = "Retorna a lista pública de lances de um leilão, ordenados do mais recente para o mais antigo."
    )
    @ApiResponse(responseCode = "200", description = "Lista de lances retornada com sucesso")
    @ApiResponse(responseCode = "404", description = "Leilão não encontrado")
    public ResponseEntity<Page<BidPublicResponseDTO>> listBids(
            @PathVariable UUID auctionId,

            @PageableDefault(
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            @ParameterObject
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                bidService.listByAuction(auctionId, pageable)
        );
    }


    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Histórico de lances do usuário autenticado",
        description = "Retorna todos os lances realizados pelo usuário autenticado."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Histórico de lances retornado com sucesso")
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    @ApiResponse(responseCode = "403", description = "Usuário sem permissão")
    public ResponseEntity<Page<BidResponseDTO>> myBids(
            @PageableDefault(
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            @ParameterObject
            Pageable pageable
    ) {
        UUID userId = authService.getAuthenticatedUserId();
        return ResponseEntity.ok(
                bidService.listMyBids(userId, pageable)
        );
    }


    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Listar todos os lances (auditoria)",
        description = "Endpoint administrativo para auditoria de todos os lances do sistema."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Lista de lances retornada com sucesso")
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    @ApiResponse(responseCode = "403", description = "Acesso restrito a administradores")
    public ResponseEntity<Page<BidResponseDTO>> listAll(
            @PageableDefault(
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            @ParameterObject
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                bidService.listAll(pageable)
        );
    }
}

