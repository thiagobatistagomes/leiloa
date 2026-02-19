package com.thiago.leiloa_api.service;

import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.thiago.leiloa_api.domain.payment.Payment;
import com.thiago.leiloa_api.domain.payment.PaymentAddress;
import com.thiago.leiloa_api.domain.address.Address;
import com.thiago.leiloa_api.domain.user.User;
import com.thiago.leiloa_api.dto.payment_address.PaymentAddressDTO;
import com.thiago.leiloa_api.dto.payment_address.SetPaymentAddressRequestDTO;
import com.thiago.leiloa_api.domain.payment.PaymentStatus;
import com.thiago.leiloa_api.repository.AddressRepository;
import com.thiago.leiloa_api.repository.PaymentAddressRepository;
import com.thiago.leiloa_api.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentAddressService {

    private final PaymentRepository paymentRepository;
    private final AddressRepository addressRepository;
    private final PaymentAddressRepository paymentAddressRepository;
    private final AuthService authService;

    @Transactional
    public PaymentAddressDTO setPaymentAddress(UUID paymentId, SetPaymentAddressRequestDTO dto) {

        User user = authService.getAuthenticatedUser();

        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new IllegalStateException("Pagamento não encontrado."));

        if (!payment.getWinner().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Este pagamento não pertence a você.");
        }

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException("Endereço só pode ser definido para pagamentos pendentes.");
        }

        if (paymentAddressRepository.existsByPaymentId(paymentId)) {
            throw new IllegalStateException("O endereço deste pagamento já foi definido.");
        }

        Address address = addressRepository.findByIdAndUserId(dto.addressId(), user.getId())
            .orElseThrow(() -> new IllegalArgumentException("Endereço não encontrado para este usuário."));

        // Criar snapshot completo
        PaymentAddress snapshot = new PaymentAddress();
        snapshot.setPayment(payment);
        snapshot.setStreet(address.getStreet());
        snapshot.setNumber(address.getNumber());
        snapshot.setComplement(address.getComplement());
        snapshot.setDistrict(address.getDistrict());
        snapshot.setCity(address.getCity());
        snapshot.setState(address.getState());
        snapshot.setCountry(address.getCountry());
        snapshot.setPostalCode(address.getPostalCode());

        snapshot.setReferenceNote(dto.referenceNote());

        PaymentAddress saved = paymentAddressRepository.save(snapshot);

        return PaymentAddressDTO.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public PaymentAddressDTO getPaymentAddress(UUID paymentId) {
        PaymentAddress pa = paymentAddressRepository.findByPaymentId(paymentId)
            .orElseThrow(() -> new IllegalStateException("Endereço deste pagamento não foi definido."));

        return PaymentAddressDTO.fromEntity(pa);
    }
}


