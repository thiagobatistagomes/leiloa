package com.thiago.leiloa_api.dto.item;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemUpdateDTO {

    @NotBlank
    private String name;

    private String description;

    private String imageUrl;

    @NotNull
    private UUID categoryId;

}