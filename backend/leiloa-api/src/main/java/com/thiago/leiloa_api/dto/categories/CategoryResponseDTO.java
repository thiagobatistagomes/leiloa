package com.thiago.leiloa_api.dto.categories;


import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryResponseDTO {
    private UUID id;
    private String name;
}