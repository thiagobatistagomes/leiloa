package com.thiago.leiloa_api.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import com.thiago.leiloa_api.dto.payment_address.PaymentAddressDTO;
import com.thiago.leiloa_api.dto.payment_address.SetPaymentAddressRequestDTO;
import com.thiago.leiloa_api.service.PaymentAddressService;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

@Tag(name = "Payment Address", description = "Endpoints para gerenciar o endereço de pagamento dos leilões.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/payments/{paymentId}/address")
public class PaymentAddressController {

    private final PaymentAddressService paymentAddressService;

    
    @PostMapping
    @Operation(
        summary = "Definir o endereço de pagamento para um pagamento pendente.",
        description = "Permite que o usuário defina um endereço de entrega para um pagamento que está pendente. O endereço é copiado do endereço cadastrado do usuário no momento da definição."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Endereço de pagamento definido com sucesso."
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Requisição inválida, como endereço não encontrado ou pagamento não pendente."
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Pagamento ou endereço não encontrado."
        )
    })
    public ResponseEntity<PaymentAddressDTO> setPaymentAddress(
            @PathVariable UUID paymentId,
            @RequestBody SetPaymentAddressRequestDTO dto
    ) {
        PaymentAddressDTO response = paymentAddressService.setPaymentAddress(paymentId, dto);
        return ResponseEntity.ok(response);
    }


    @GetMapping
    @Operation(
        summary = "Obter o endereço de pagamento de um pagamento.",
        description = "Retorna os detalhes do endereço de pagamento associado a um pagamento específico."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Endereço de pagamento obtido com sucesso."
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Pagamento ou endereço de pagamento não encontrado."
        )
    })
    public ResponseEntity<PaymentAddressDTO> getPaymentAddress(
            @PathVariable UUID paymentId
    ) {
        PaymentAddressDTO response = paymentAddressService.getPaymentAddress(paymentId);
        return ResponseEntity.ok(response);
    }
}
