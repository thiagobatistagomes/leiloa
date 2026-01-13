package com.thiago.leiloa_api.dto.categories;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCategoryDTO {
    
    @NotBlank(message= "Nome da categoria é obrigatório")
    private String name;
}
