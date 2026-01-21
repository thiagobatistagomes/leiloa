package com.thiago.leiloa_api.dto.item;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemUpdateDTO {

    private String name;

    private String description;

    private String imageUrl;

    private UUID categoryId;

}