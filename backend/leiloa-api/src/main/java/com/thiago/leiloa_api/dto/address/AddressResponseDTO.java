package com.thiago.leiloa_api.dto.address;

import java.time.LocalDateTime;
import java.util.UUID;

import com.thiago.leiloa_api.domain.address.Address;

public record AddressResponseDTO(
    UUID id,
    UUID userId,
    String label,
    String street,
    String number,
    String complement,
    String district,
    String city,
    String state,
    String country,
    String postalCode,
    Boolean isDefault,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static AddressResponseDTO fromEntity(Address address) {
        return new AddressResponseDTO(
            address.getId(),
            address.getUser().getId(),
            address.getLabel(),
            address.getStreet(),
            address.getNumber(),
            address.getComplement(),
            address.getDistrict(),
            address.getCity(),
            address.getState(),
            address.getCountry(),
            address.getPostalCode(),
            address.getIsDefault(),
            address.getCreatedAt(),
            address.getUpdatedAt()
        );
    }
}

