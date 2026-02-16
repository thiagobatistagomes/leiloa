package com.thiago.leiloa_api.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.thiago.leiloa_api.domain.address.Address;

public interface AddressRepository extends JpaRepository<Address, UUID> {

    // Lista todos os endereços do usuário
    Page<Address> findByUserId(UUID userId, Pageable pageable);
    List<Address> findByUserId(UUID userId);

    // Busca um endereço do usuário garantindo ownership
    Optional<Address> findByIdAndUserId(UUID id, UUID userId);

    // Remove todos os defaults do usuário (usado ao setar novo default)
    List<Address> findByUserIdAndIsDefaultTrue(UUID userId);

    // Verifica se usuário já tem endereço default
    boolean existsByUserIdAndIsDefaultTrue(UUID userId);

    // Verificação de endereços do usuário para evitar duplicidade
    boolean existsByUserIdAndStreetAndNumberAndDistrictAndCityAndStateAndPostalCode(
        UUID userId,
        String street,
        String number,
        String district,
        String city,
        String state,
        String postalCode
    );

}

