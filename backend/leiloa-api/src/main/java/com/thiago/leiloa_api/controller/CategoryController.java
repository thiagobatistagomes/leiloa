package com.thiago.leiloa_api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thiago.leiloa_api.dto.categories.CategoryResponseDTO;
import com.thiago.leiloa_api.dto.categories.CreateCategoryDTO;
import com.thiago.leiloa_api.service.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;



@Tag(
    name = "Categories",
    description = "Operações relacionadas às categorias de itens do sistema"
)
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(
        summary = "Listar todas as categorias",
        description = """
            Retorna a lista completa de categorias disponíveis no sistema.
            
            Este endpoint é público, pois as categorias são utilizadas
            para organização e busca de itens visíveis a todos os usuários.
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categorias listadas com sucesso")
    })
    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> listAll() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    @Operation(
        summary = "Buscar categoria por ID",
        description = """
            Retorna os dados de uma categoria específica a partir do seu ID.
            
            Endpoint público, utilizado principalmente para apoio
            à navegação e exibição de itens.
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categoria encontrada"),
        @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    })
    @GetMapping("/{id}")
    public CategoryResponseDTO findById(
        @Parameter(
            description = "ID da categoria",
            example = "550e8400-e29b-41d4-a716-446655440000"
        )
        @PathVariable UUID id
    ) {
        return categoryService.findById(id);
    }

    @Operation(
        summary = "Criar nova categoria",
        description = """
            Permite a criação de uma nova categoria no sistema.
            
            Esta operação é restrita a usuários com papel ADMIN,
            pois categorias afetam toda a organização do domínio.
            """
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Categoria criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão de ADMIN")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponseDTO> create(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Dados da nova categoria",
            required = true
        )
        @RequestBody @Validated CreateCategoryDTO dto
    ) {
        CategoryResponseDTO response = categoryService.create(dto.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
        summary = "Atualizar categoria existente",
        description = """
            Atualiza o nome de uma categoria já existente.
            
            Operação restrita a usuários ADMIN.
            """
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão de ADMIN"),
        @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponseDTO> update(
        @PathVariable UUID id,
        @RequestBody @Validated CreateCategoryDTO dto
    ) {
        return ResponseEntity.ok(categoryService.update(id, dto.getName()));
    }

    @Operation(
        summary = "Remover categoria",
        description = """
            Remove uma categoria do sistema.
            
            A exclusão só é permitida se a categoria não estiver
            associada a nenhum item.
            
            Operação restrita a usuários ADMIN.
            """
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Categoria removida com sucesso"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão de ADMIN"),
        @ApiResponse(responseCode = "404", description = "Categoria não encontrada"),
        @ApiResponse(responseCode = "409", description = "Categoria associada a itens")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

