package com.thiago.leiloa_api.dto.payment_address;

import java.util.UUID;

public record SetPaymentAddressRequestDTO(
    UUID addressId,
    String referenceNote
) {}


