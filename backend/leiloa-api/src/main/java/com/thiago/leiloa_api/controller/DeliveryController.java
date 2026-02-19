package com.thiago.leiloa_api.controller;

import java.time.LocalDateTime;
import java.util.UUID;


import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Sort;

import com.thiago.leiloa_api.domain.delivery.DeliveryStatus;
import com.thiago.leiloa_api.dto.delivery.*;
import com.thiago.leiloa_api.service.DeliveryService;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Delivery", description = "Endpoints para gerenciar entregas dos leilões.")
@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

  
    @GetMapping("/{id}/allowed-statuses")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Obter os status de entrega permitidos para um determinado status atual.",
        description = "Retorna uma lista dos status de entrega que são permitidos para transição a partir do status atual da entrega. Isso é útil para validar as mudanças de status e garantir que apenas transições válidas sejam realizadas."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lista de status permitidos retornada com sucesso."
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Requisição inválida, como ID de entrega malformado."
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Entrega não encontrada para o ID fornecido."
        )
    })
    public ResponseEntity<AllowedStatusesDTO> getAllowedStatuses(@PathVariable UUID id) {
        return ResponseEntity.ok(deliveryService.getAllowedStatuses(id));
    }

 
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Obter os detalhes de uma entrega por ID.",
        description = "Retorna os detalhes completos de uma entrega específica, incluindo informações como status, código de rastreamento, timestamps de status, e outros dados relevantes. O administrador ou usuário podem acessar essas informações."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Detalhes da entrega retornados com sucesso."
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Entrega não encontrada para o ID fornecido."
        )
    })
    public ResponseEntity<DeliveryResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(deliveryService.findById(id));
    }


    @GetMapping("/payment/{paymentId}/details")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Obter detalhes de uma entrega associada a um pagamento.",
        description = "Retorna os detalhes de uma entrega associada a um pagamento específico. O administrador ou usuário podem acessar essas informações."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Detalhes da entrega retornados com sucesso."
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Entrega não encontrada para o ID do pagamento fornecido."
        )
    })
    public ResponseEntity<DeliveryResponseDTO> findByPayment(@PathVariable UUID paymentId) {
        return ResponseEntity.ok(deliveryService.findByPayment(paymentId));
    }


 
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Atualizar o status de uma entrega.",
        description = "Permite que um administrador atualize o status de uma entrega. O status deve ser um dos status permitidos para a transição a partir do status atual da entrega. O sistema valida a transição e atualiza os timestamps correspondentes ao novo status."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Status da entrega atualizado com sucesso."
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Requisição inválida, como status de entrega inválido ou transição de status não permitida."
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Entrega não encontrada para o ID fornecido."
        )
    })
    public ResponseEntity<DeliveryResponseDTO> updateStatus(
            @PathVariable UUID id,
            @RequestBody DeliveryUpdateStatusDTO dto) {

        return ResponseEntity.ok(deliveryService.updateStatus(id, dto));
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Buscar entregas com filtros.",
        description = "Retorna uma página de entregas filtradas por critérios específicos. O administrador pode buscar entregas com base em filtros como status, data de criação, etc."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Entregas encontradas com sucesso."
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Requisição inválida, como parâmetros de filtro malformados."
        )
    })
    public ResponseEntity<Page<DeliveryListResponseDTO>> search(
            @RequestParam(required = false) DeliveryStatus status,
            @RequestParam(required = false) String deliveryMethod,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdAtStart,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdAtEnd,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime updatedAtStart,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime updatedAtEnd,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            @ParameterObject
            Pageable pageable) {

         DeliveryFilterDTO filter = new DeliveryFilterDTO(
                status,
                deliveryMethod,
                createdAtStart,
                createdAtEnd,
                updatedAtStart,
                updatedAtEnd
        );
        return ResponseEntity.ok(deliveryService.search(filter, pageable));
    }
}

