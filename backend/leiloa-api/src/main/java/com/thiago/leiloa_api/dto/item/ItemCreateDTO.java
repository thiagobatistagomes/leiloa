package com.thiago.leiloa_api.dto.item;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemCreateDTO {

    @NotBlank(message = "Nome do item é obrigatório")
    @Size(max = 100, message = "Nome do item deve ter no máximo 100 caracteres")
    private String name;

    private String description;

    @NotNull(message = "Categoria é obrigatória")
    private UUID categoryId;

    private String imageUrl;


}