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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thiago.leiloa_api.dto.item.ItemCreateDTO;
import com.thiago.leiloa_api.dto.item.ItemResponseDTO;
import com.thiago.leiloa_api.dto.item.ItemUpdateDTO;
import com.thiago.leiloa_api.service.ItemService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/*
 * @Tag
 * Agrupa todos os endpoints deste controller no Swagger UI,
 * facilitando navegação e leitura da documentação.
 */
@Tag(
    name = "Items",
    description = "Operações públicas e privadas relacionadas aos itens de leilão"
)
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    /*
     * @Operation
     * Define o resumo e a descrição detalhada do endpoint no Swagger.
     * É a principal fonte de documentação semântica da rota.
     */
    @Operation(
        summary = "Listar itens ativos disponíveis para leilão",
        description = """
            Retorna uma lista paginada de itens ativos e públicos.

            É possível filtrar os resultados opcionalmente por categoria
            e/ou por parte do nome do item.

            A paginação segue o padrão do Spring (page, size, sort).
            """
    )
    @GetMapping("/public")
    public ResponseEntity<Page<ItemResponseDTO>> listActiveItems(

            /*
             * @Parameter
             * Documenta um parâmetro individual da requisição,
             * descrevendo seu propósito e exemplo de uso.
             */
            @Parameter(
                description = "ID da categoria para filtrar os itens",
                example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @RequestParam(required = false) UUID categoryId,

            @Parameter(
                description = "Nome ou parte do nome do item para busca textual",
                example = "notebook"
            )
            @RequestParam(required = false) String name,

            /*
             * @ParameterObject
             * Indica ao Swagger que este objeto (Pageable) deve ser
             * expandido automaticamente em parâmetros de query.
             *
             * @PageableDefault
             * Define valores padrão visíveis na documentação.
             */
            @ParameterObject
            @PageableDefault(
                page = 0,
                size = 10,
                sort = "createdAt",
                direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {
        return ResponseEntity.ok(
            itemService.listPublicActiveItems(categoryId, name, pageable)
        );
    }

    /*
     * @SecurityRequirement
     * Informa ao Swagger que este endpoint exige autenticação
     * via esquema bearerAuth (JWT).
     */
    @Operation(
        summary = "Cadastrar novo item para leilão",
        description = """
            Permite que um usuário autenticado cadastre um novo item.

            O item será criado como ACTIVE e associado automaticamente
            ao usuário autenticado.
            """
    )
    @SecurityRequirement(name = "bearerAuth")

    /*
     * @ApiResponses
     * Documenta explicitamente os possíveis códigos HTTP
     * retornados pela rota.
     */
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Item criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos no corpo da requisição"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão para criar itens")
    })
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ItemResponseDTO> create(

        /*
         * @io.swagger.v3.oas.annotations.parameters.RequestBody
         * Documenta o corpo da requisição no Swagger,
         * sem interferir na validação do Spring.
         */
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Dados necessários para criar um novo item",
            required = true
        )
        @RequestBody @Valid ItemCreateDTO dto
    ) {
        ItemResponseDTO response = itemService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
        summary = "Listar itens do usuário autenticado",
        description = """
            Retorna todos os itens ativos cadastrados pelo usuário autenticado.

            Apenas itens com status ACTIVE são retornados.
            """
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de itens retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão para acessar este recurso")
    })
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ItemResponseDTO>> listMyItems() {
        return ResponseEntity.ok(itemService.listMyItems());
    }

    @Operation(
        summary = "Buscar item do usuário autenticado por ID",
        description = """
            Retorna os detalhes de um item específico pertencente
            ao usuário autenticado.
            """
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Item encontrado"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
        @ApiResponse(responseCode = "404", description = "Item não encontrado")
    })
    @GetMapping("/me/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ItemResponseDTO> getMyItemById(
        @Parameter(
            description = "ID do item",
            example = "550e8400-e29b-41d4-a716-446655440000"
        )
        @PathVariable UUID id
    ) {
        return ResponseEntity.ok(itemService.getMyItemById(id));
    }

    @Operation(
        summary = "Atualizar item do usuário autenticado",
        description = """
            Permite atualizar os dados de um item pertencente
            ao usuário autenticado.

            Apenas itens em estado ACTIVE podem ser editados.
            """
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Item atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
        @ApiResponse(responseCode = "404", description = "Item não encontrado"),
        @ApiResponse(responseCode = "409", description = "Item não pode ser editado no estado atual")
    })
    @PutMapping("/me/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ItemResponseDTO> update(
        @PathVariable UUID id,
        @RequestBody @Valid ItemUpdateDTO dto
    ) {
        return ResponseEntity.ok(itemService.update(id, dto));
    }

    @Operation(
        summary = "Remover item do usuário autenticado",
        description = """
            Remove logicamente (soft delete) um item pertencente
            ao usuário autenticado.

            Itens em leilão ativo não podem ser removidos.
            """
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Item removido com sucesso"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
        @ApiResponse(responseCode = "404", description = "Item não encontrado"),
        @ApiResponse(responseCode = "409", description = "Item em leilão ativo")
    })
    @DeleteMapping("/me/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        itemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
