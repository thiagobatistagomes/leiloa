package com.thiago.leiloa_api.service;

import java.time.LocalDateTime;
import java.util.UUID;



import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import com.thiago.leiloa_api.specification.UserSpecification;



import com.thiago.leiloa_api.domain.user.User;
import com.thiago.leiloa_api.domain.user.UserStatus;
import com.thiago.leiloa_api.dto.user.UserAuditResponseDTO;
import com.thiago.leiloa_api.dto.user.UserFilterDTO;
import com.thiago.leiloa_api.dto.user.UserResponseDTO;
import com.thiago.leiloa_api.dto.user.UserUpdateDTO;
import com.thiago.leiloa_api.dto.user.UpdatePhoneDTO;
import com.thiago.leiloa_api.repository.UserRepository;


import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthService authService;

    @Transactional
    public UserResponseDTO deactivateMyAccount() {

        User user = authService.getAuthenticatedUser();

        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new RuntimeException("Sua conta já está desativada.");
        }

        user.setStatus(UserStatus.INACTIVE);
        user.setStatusChangedBy(user.getId());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        return UserResponseDTO.fromEntity(user);
    }

    // Método para trocar número de telefone
    @Transactional
    public UserResponseDTO updatePhoneNumber(UpdatePhoneDTO dto) {

        User user = authService.getAuthenticatedUser();

        if (dto.phoneNumber().equals(user.getPhoneNumber())) {
            throw new RuntimeException("O novo telefone deve ser diferente do atual.");
        }


        user.setPhoneNumber(dto.phoneNumber());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        return UserResponseDTO.fromEntity(user);
    }

    @Transactional
    public UserResponseDTO updateMe(UserUpdateDTO dto) {

        User user = authService.getAuthenticatedUser();

        if (dto.name() != null && !dto.name().equals(user.getName())) {
            user.setName(dto.name());
        } else {
            throw new RuntimeException("O novo nome deve ser diferente do atual.");
        }

        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        return UserResponseDTO.fromEntity(user);
    }


    @Transactional
    public UserResponseDTO blockUser(UUID userId) {

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new RuntimeException("Usuário já está bloqueado.");
        }

        user.setStatus(UserStatus.BLOCKED);
        user.setStatusChangedBy(authService.getAuthenticatedUser().getId());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        return UserResponseDTO.fromEntity(user);
    }

    @Transactional
    public UserResponseDTO activateUser(UUID userId) {

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (user.getStatus() == UserStatus.ACTIVE) {
            throw new RuntimeException("Usuário já está ativo.");
        }

        user.setStatus(UserStatus.ACTIVE);
        user.setStatusChangedBy(authService.getAuthenticatedUser().getId());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        return UserResponseDTO.fromEntity(user);
    }

    // Método para auditoria de usuários (ADMIN)
    @Transactional(readOnly = true)
    public Page<UserAuditResponseDTO> auditUsers(UserFilterDTO filter, Pageable pageable) {
        Specification<User> spec = Specification.<User>where(null)
            .and(UserSpecification.nameContains(filter.name()))
            .and(UserSpecification.emailContains(filter.email()))
            .and(UserSpecification.hasStatus(filter.status()))
            .and(UserSpecification.hasRole(filter.role()))
            .and(UserSpecification.createdBetween(filter.createdFrom(), filter.createdTo()))
            .and(UserSpecification.lastLoginBetween(filter.lastLoginFrom(), filter.lastLoginTo()));


        Page<User> page = userRepository.findAll(spec, pageable);

        return page.map(UserAuditResponseDTO::fromEntity);
    }

    


}

