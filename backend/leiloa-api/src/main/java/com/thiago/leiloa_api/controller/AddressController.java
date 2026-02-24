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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.thiago.leiloa_api.dto.address.AddressRequestDTO;
import com.thiago.leiloa_api.dto.address.AddressResponseDTO;
import com.thiago.leiloa_api.dto.address.AddressUpdateDTO;
import com.thiago.leiloa_api.service.AddressService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
@Valid
@Tag(name = "Addresses", description = "Endpoints relacionados a endereços de usuários")
public class AddressController {

    private final AddressService addressService;

    
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Criar um novo endereço para um usuário.",
        description = "Cria um novo endereço associado ao usuário especificado pelo userId."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Endereço criado com sucesso."
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Requisição inválida."
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Não autorizado."
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Usuário não encontrado."
        )
    })
    public ResponseEntity<AddressResponseDTO> create(
        @Valid @RequestBody AddressRequestDTO dto
    ) {
        AddressResponseDTO response = addressService.create(
            dto
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

   
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Listar endereços de um usuário.",
        description = "Retorna todos os endereços associados ao usuário autenticado."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Endereços listados com sucesso."
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Requisição inválida."
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Não autorizado."
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Usuário não encontrado."
        )
    })
    public ResponseEntity<Page<AddressResponseDTO>> list(
        @ParameterObject
        @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
        Pageable pageable
    ) {
        return ResponseEntity.ok(addressService.listByUser(pageable));
    }

 
    @GetMapping("/{addressId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Obter detalhes de um endereço.",
        description = "Retorna os detalhes de um endereço específico associado ao usuário."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Endereço obtido com sucesso."
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Requisição inválida."
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Não autorizado."
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Endereço ou usuário não encontrado."
        )
    })
    public ResponseEntity<AddressResponseDTO> get(
        @PathVariable UUID addressId
    ) {
        return ResponseEntity.ok(addressService.get(addressId));
    }

  
    @PutMapping("/{addressId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Atualizar um endereço existente.",
        description = "Atualiza os detalhes de um endereço específico associado ao usuário."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Endereço atualizado com sucesso."
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Requisição inválida."
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Não autorizado."
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Endereço ou usuário não encontrado."
        )
    })
    public ResponseEntity<AddressResponseDTO> update(
        @PathVariable UUID addressId,
        @Valid @RequestBody AddressUpdateDTO dto
    ) {
        return ResponseEntity.ok(
            addressService.update(addressId, dto)
        );
    }


    @DeleteMapping("/{addressId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Excluir um endereço.",
        description = "Remove um endereço específico associado ao usuário."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Endereço excluído com sucesso."
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Requisição inválida."
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Não autorizado."
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Endereço ou usuário não encontrado."
        )
    })
    public ResponseEntity<Void> delete(
        @PathVariable UUID addressId
    ) {
        addressService.delete(addressId);
        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/{addressId}/default")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Definir um endereço como padrão.",
        description = "Marca um endereço específico como o endereço padrão do usuário."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Endereço definido como padrão com sucesso."
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Requisição inválida."
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Não autorizado."
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Endereço ou usuário não encontrado."
        )
    })
    public ResponseEntity<AddressResponseDTO> setDefault(
        @PathVariable UUID addressId
    ) {
        return ResponseEntity.ok(addressService.setDefault(addressId));
    }
}
