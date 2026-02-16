package com.thiago.leiloa_api.dto.address;

public record AddressUpdateDTO(
    String label,
    String street,
    String number,
    String complement,
    String district,
    String city,
    String state,
    String country,
    String postalCode,
    Boolean isDefault
) {}
