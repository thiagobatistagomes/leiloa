package com.thiago.leiloa_api.dto.item;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemCreateDTO {

    @NotBlank(message = "Nome do item é obrigatório")
    private String name;

    private String description;

    @NotNull(message = "Categoria é obrigatória")
    private UUID categoryId;

    private String imageUrl;


}