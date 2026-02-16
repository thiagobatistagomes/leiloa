package com.thiago.leiloa_api.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.thiago.leiloa_api.domain.address.Address;
import com.thiago.leiloa_api.domain.user.User;
import com.thiago.leiloa_api.dto.address.AddressRequestDTO;
import com.thiago.leiloa_api.dto.address.AddressResponseDTO;
import com.thiago.leiloa_api.dto.address.AddressUpdateDTO;
import com.thiago.leiloa_api.repository.AddressRepository;
import com.thiago.leiloa_api.repository.UserRepository;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final AuthService authService;

    public AddressService(
        AddressRepository addressRepository,
        UserRepository userRepository,
        AuthService authService
    ) {
        this.addressRepository = addressRepository;
        this.authService = authService;
    }

    
    @Transactional
    public AddressResponseDTO create(AddressRequestDTO dto) {

        User user = authService.getAuthenticatedUser();

         boolean exists = addressRepository
            .existsByUserIdAndStreetAndNumberAndDistrictAndCityAndStateAndPostalCode(
                user.getId(),
                dto.street(),
                dto.number(),
                dto.district(),
                dto.city(),
                dto.state(),
                dto.postalCode()
            );

        if (exists) {
            throw new IllegalArgumentException("Endereço já cadastrado.");
        }


        boolean setAsDefault = 
            dto.isDefault() != null && dto.isDefault() ||
            !addressRepository.existsByUserIdAndIsDefaultTrue(user.getId());

        if (setAsDefault) {
            unsetDefaultForUser(user.getId());
        }

        Address address = new Address();
        address.setUser(user);
        address.setLabel(dto.label());
        address.setStreet(dto.street());
        address.setNumber(dto.number());
        address.setComplement(dto.complement());
        address.setDistrict(dto.district());
        address.setCity(dto.city());
        address.setState(dto.state());
        address.setCountry(dto.country());
        address.setPostalCode(dto.postalCode());
        address.setIsDefault(setAsDefault);

        Address saved = addressRepository.save(address);
        return AddressResponseDTO.fromEntity(saved);
    }

 
    @Transactional
    public AddressResponseDTO update(UUID addressId, AddressUpdateDTO dto) {
        
        User user = authService.getAuthenticatedUser();
        UUID userId = user.getId();

        Address address = addressRepository.findByIdAndUserId(addressId, userId)
            .orElseThrow(() -> new IllegalArgumentException("Endereço não encontrado."));

        if (dto.label() != null) address.setLabel(dto.label());
        if (dto.street() != null) address.setStreet(dto.street());
        if (dto.number() != null) address.setNumber(dto.number());
        if (dto.complement() != null) address.setComplement(dto.complement());
        if (dto.district() != null) address.setDistrict(dto.district());
        if (dto.city() != null) address.setCity(dto.city());
        if (dto.state() != null) address.setState(dto.state());
        if (dto.country() != null) address.setCountry(dto.country());
        if (dto.postalCode() != null) address.setPostalCode(dto.postalCode());

        if (dto.isDefault() != null && dto.isDefault()) {
            unsetDefaultForUser(userId);
            address.setIsDefault(true);
        }

        Address updated = addressRepository.save(address);
        return AddressResponseDTO.fromEntity(updated);
    }


    @Transactional
    public void delete(UUID addressId) {

        User user = authService.getAuthenticatedUser();
        UUID userId = user.getId();

        Address address = addressRepository.findByIdAndUserId(addressId, userId)
            .orElseThrow(() -> new IllegalArgumentException("Endereço não encontrado."));

        addressRepository.delete(address);

        // regra: se deletou o endereço default, escolher outro
        if (address.getIsDefault()) {
            List<Address> others = addressRepository.findByUserId(userId);
            if (!others.isEmpty()) {
                Address newDefault = others.get(0);
                newDefault.setIsDefault(true);
                addressRepository.save(newDefault);
            }
        }
    }

 
    @Transactional(readOnly = true)
    public AddressResponseDTO get(UUID addressId) {

        User user = authService.getAuthenticatedUser();
        UUID userId = user.getId();

        Address address = addressRepository.findByIdAndUserId(addressId, userId)
            .orElseThrow(() -> new IllegalArgumentException("Endereço não encontrado."));

        return AddressResponseDTO.fromEntity(address);
    }


    @Transactional(readOnly = true)
    public Page<AddressResponseDTO> listByUser(Pageable pageable) {
        
        User user = authService.getAuthenticatedUser();
        UUID userId = user.getId();

        Page<Address> addresses = addressRepository.findByUserId(userId, pageable);
        return addresses.map(AddressResponseDTO::fromEntity);
    }


    @Transactional
    public AddressResponseDTO setDefault(UUID addressId) {

        User user = authService.getAuthenticatedUser();
        UUID userId = user.getId();

        Address address = addressRepository.findByIdAndUserId(addressId, userId)
            .orElseThrow(() -> new IllegalArgumentException("Endereço não encontrado."));

        unsetDefaultForUser(userId);
        address.setIsDefault(true);

        Address saved = addressRepository.save(address);
        return AddressResponseDTO.fromEntity(saved);
    }

 
    private void unsetDefaultForUser(UUID userId) {
        List<Address> defaults = addressRepository.findByUserIdAndIsDefaultTrue(userId);
        defaults.forEach(addr -> addr.setIsDefault(false));
        if (!defaults.isEmpty()) {
            addressRepository.saveAll(defaults);
        }
    }

}
