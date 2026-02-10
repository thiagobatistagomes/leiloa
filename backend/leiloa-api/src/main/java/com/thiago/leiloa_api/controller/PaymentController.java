package com.thiago.leiloa_api.controller;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.thiago.leiloa_api.domain.payment.PaymentStatus;
import com.thiago.leiloa_api.dto.payment.*;
import com.thiago.leiloa_api.service.PaymentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;


import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Validated
@Tag(name = "Payments", description = "Gerenciamento de pagamentos gerados após finalização de leilões")
public class PaymentController {

    private final PaymentService paymentService;


    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Pesquisar pagamentos com múltiplos filtros (Admin)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de pagamentos retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autenticado"),
        @ApiResponse(responseCode = "403", description = "Sem permissão")
    })
    public Page<PaymentResponseDTO> searchAdmin(
            @RequestParam(required = false) UUID auctionId,
            @RequestParam(required = false) UUID winnerId,
            @RequestParam(required = false) PaymentStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdAtStart,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdAtEnd,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime paidAtStart,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime paidAtEnd,
            @RequestParam(required = false) BigDecimal minValue,
            @RequestParam(required = false) BigDecimal maxValue,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            @ParameterObject Pageable pageable
    ) {

        PaymentFilterDTO filter = new PaymentFilterDTO();
        filter.setAuctionId(auctionId);
        filter.setWinnerId(winnerId);
        filter.setStatus(status);
        filter.setCreatedAtStart(createdAtStart);
        filter.setCreatedAtEnd(createdAtEnd);
        filter.setPaidAtStart(paidAtStart);
        filter.setPaidAtEnd(paidAtEnd);
        filter.setMinValue(minValue);
        filter.setMaxValue(maxValue);

        return paymentService.search(filter, pageable);
    }





    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Buscar pagamento por ID (Admin)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pagamento encontrado"),
        @ApiResponse(responseCode = "404", description = "Pagamento não encontrado")
    })
    public ResponseEntity<PaymentResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentService.getById(id));
    }


    @GetMapping("/auction/{auctionId}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Buscar pagamento de um leilão específico (Admin)")
    public ResponseEntity<PaymentResponseDTO> getByAuction(
            @PathVariable UUID auctionId
    ) {
        return ResponseEntity.ok(paymentService.getByAuction(auctionId));
    }


    @PostMapping("/{paymentId}/approve")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Realizar pagamento",
        description = "Permite que o usuário vencedor do leilão finalize o pagamento."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pagamento aprovado com sucesso",
                     content = @Content(schema = @Schema(implementation = ApprovePaymentDTO.class))),
        @ApiResponse(responseCode = "400", description = "Regra de negócio violada"),
        @ApiResponse(responseCode = "403", description = "Usuário não é o vencedor"),
        @ApiResponse(responseCode = "404", description = "Pagamento não encontrado")
    })
    public ResponseEntity<ApprovePaymentDTO> approvePayment(
            @PathVariable UUID paymentId,
            @Parameter(description = "ID do usuário autenticado", required = true)
            @RequestParam UUID userId
    ) {
        return ResponseEntity.ok(paymentService.approvePayment(paymentId, userId));
    }


    @PatchMapping("/{paymentId}/cancel")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Cancelar pagamento pendente (Admin)",
        description = "Permite que administradores cancelem pagamentos ainda pendentes."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pagamento cancelado",
                     content = @Content(schema = @Schema(implementation = CancelPaymentDTO.class))),
        @ApiResponse(responseCode = "400", description = "Pagamento não está pendente"),
        @ApiResponse(responseCode = "404", description = "Pagamento não encontrado")
    })
    public ResponseEntity<CancelPaymentDTO> cancelPayment(@PathVariable UUID paymentId) {
        return ResponseEntity.ok(paymentService.cancelPayment(paymentId));
    }


    @PostMapping("/expire-pending")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Executar manualmente o processo de expiração de pagamentos pendentes (Admin) caso necessário",
        description = "Expira pagamentos com mais de 48h pendentes."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Quantidade de pagamentos expirados retornada")
    })
    public ResponseEntity<Integer> expirePendingPayments() {
        return ResponseEntity.ok(paymentService.expirePendingPayments());
    }
}
