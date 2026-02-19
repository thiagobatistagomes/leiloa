package com.thiago.leiloa_api.dto.payment_address;

import java.time.LocalDateTime;
import java.util.UUID;
import com.thiago.leiloa_api.domain.payment.PaymentAddress;

public record PaymentAddressDTO(
    UUID id,
    UUID paymentId,
    String street,
    String number,
    String complement,
    String district,
    String city,
    String state,
    String country,
    String postalCode,
    String referenceNote,
    LocalDateTime createdAt
) {
    public static PaymentAddressDTO fromEntity(PaymentAddress a) {
        return new PaymentAddressDTO(
            a.getId(),
            a.getPayment().getId(),
            a.getStreet(),
            a.getNumber(),
            a.getComplement(),
            a.getDistrict(),
            a.getCity(),
            a.getState(),
            a.getCountry(),
            a.getPostalCode(),
            a.getReferenceNote(),
            a.getCreatedAt()
        );
    }
}



