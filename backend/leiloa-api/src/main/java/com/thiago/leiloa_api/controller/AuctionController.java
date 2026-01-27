package com.thiago.leiloa_api.controller;

import java.util.List;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thiago.leiloa_api.domain.auction.AuctionStatus;
import com.thiago.leiloa_api.dto.auction.AuctionDetailResponseDTO;
import com.thiago.leiloa_api.dto.auction.AuctionFilterDTO;
import com.thiago.leiloa_api.dto.auction.AuctionListResponseDTO;
import com.thiago.leiloa_api.dto.auction.AuctionResponseDTO;
import com.thiago.leiloa_api.dto.auction.CreateAuctionDTO;
import com.thiago.leiloa_api.service.AuctionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auctions")
@RequiredArgsConstructor
@Validated
@Tag(name = "Auctions", description = "Endpoints relacionados a leilões")
public class AuctionController {

    private final AuctionService auctionService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Criar um leilão",
        description = "Cria um novo leilão para um item pertencente ao usuário autenticado."
    )
    @ApiResponse(
        responseCode = "201",
        description = "Leilão criado com sucesso",
        content = @Content(schema = @Schema(implementation = AuctionResponseDTO.class))
    )
    @ApiResponse(
        responseCode = "400",
        description = "Dados inválidos ou regra de negócio violada"
    )
    @ApiResponse(
        responseCode = "401",
        description = "Usuário não autenticado"
    )
    @ApiResponse(
        responseCode = "403",
        description = "Usuário não autorizado"
    )
    public ResponseEntity<AuctionResponseDTO> createAuction(
            @RequestBody @Valid CreateAuctionDTO dto
    ) {
        AuctionResponseDTO response = auctionService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping
    @Operation(
        summary = "Listar leilões públicos",
        description = """
            Retorna uma lista paginada de leilões públicos.
            
            Por padrão, retorna apenas leilões com status ACTIVE e SCHEDULED.
            É possível filtrar por status, categoria e nome do item.
            """
    )
    @ApiResponse(
        responseCode = "200",
        description = "Lista de leilões retornada com sucesso"
    )
    public ResponseEntity<Page<AuctionListResponseDTO>> listPublic(
            @Parameter(
                description = "Status do leilão",
                example = "ACTIVE"
            )
            @RequestParam(required = false)
            List<AuctionStatus> status,

            @Parameter(
                description = "ID da categoria do item"
            )
            @RequestParam(required = false)
            UUID categoryId,

            @Parameter(
                description = "Texto para busca no nome do item"
            )
            @RequestParam(required = false)
            String search,

            @ParameterObject
            @PageableDefault(
                page = 0,
                size = 10,
                sort = "startDate",
                direction = Sort.Direction.ASC
            )
            Pageable pageable
    ) {

        AuctionFilterDTO filter = new AuctionFilterDTO(
                status,
                categoryId,
                search
        );

        return ResponseEntity.ok(auctionService.listPublicAuctions(filter, pageable));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Detalhar um leilão",
        description = "Retorna os detalhes públicos de um leilão ativo ou agendado."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Leilão encontrado."
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Leilão não encontrado"
        )
    })
    public ResponseEntity<AuctionDetailResponseDTO> getAuctionById(
        @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
            auctionService.getPublicAuction(id)
        );
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Cancelar um leilão", description = "Cancela um leilão pertencente ao usuário autenticado.")
    public ResponseEntity<Void> cancel(@PathVariable UUID id) {
        auctionService.cancel(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/finish")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Finalizar um leilão", description = "Finaliza um leilão (apenas administradores).")
    public ResponseEntity<Void> finish(@PathVariable UUID id) {
        auctionService.finish(id);
        return ResponseEntity.noContent().build();
    }



}

