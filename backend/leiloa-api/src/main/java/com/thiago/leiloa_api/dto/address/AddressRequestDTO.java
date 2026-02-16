package com.thiago.leiloa_api.dto.address;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;


public record AddressRequestDTO(

    @NotBlank
    String label,

    @NotBlank
    String street,

    @NotBlank
    String number,

    String complement,

    @NotBlank
    String district,

    @NotBlank
    String city,

    @NotBlank
    String state,

    @NotBlank
    String country,

    @NotBlank
    String postalCode,

    Boolean isDefault
) {
    public AddressRequestDTO withUserId(UUID userId) {
        return new AddressRequestDTO(
            this.label,
            this.street,
            this.number,
            this.complement,
            this.district,
            this.city,
            this.state,
            this.country,
            this.postalCode,
            this.isDefault
        );
    }
}
