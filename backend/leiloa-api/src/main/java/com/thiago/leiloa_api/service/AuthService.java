package com.thiago.leiloa_api.service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.List;

import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import com.thiago.leiloa_api.config.JwtService;

import com.thiago.leiloa_api.domain.notification.NotificationTypeCodes;
import com.thiago.leiloa_api.domain.role.Role;
import com.thiago.leiloa_api.domain.user.User;
import com.thiago.leiloa_api.domain.user.UserStatus;
import com.thiago.leiloa_api.dto.auth.AuthResponseDTO;
import com.thiago.leiloa_api.dto.auth.LoginDTO;
import com.thiago.leiloa_api.dto.auth.ReactivateAccountDTO;
import com.thiago.leiloa_api.dto.auth.RegisterUserDTO;
import com.thiago.leiloa_api.dto.auth.UpdatePasswordDTO;
import com.thiago.leiloa_api.dto.device.UserDeviceCreateDTO;
import com.thiago.leiloa_api.dto.notification.NotificationCreateDTO;
import com.thiago.leiloa_api.exception.BlockedUserException;
import com.thiago.leiloa_api.exception.InactiveUserException;
import com.thiago.leiloa_api.repository.RoleRepository;
import com.thiago.leiloa_api.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;



// Serviço de autenticação 

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserDeviceService userDeviceService;
    private final NotificationService notificationService;

    public AuthService(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            UserDeviceService userDeviceService,
            NotificationService notificationService
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userDeviceService = userDeviceService;
        this.notificationService = notificationService;
    }

    public AuthResponseDTO login(LoginDTO dto, HttpServletRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                dto.getEmail(),
                                dto.getPassword()
                        )
                );

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        User user = userDetails.getUser();

        // Verificar se o usuário está ativo
        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new InactiveUserException("Conta desativada pelo usuário. Deseja reativar?");
        }

        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new BlockedUserException("Conta banida pelo administrador");
        }

        String token = jwtService.generateToken(userDetails);

        // Atualizar data do último login
        user.setLastLoginAt(LocalDateTime.now());

        userRepository.save(user);

        String userAgent = request.getHeader("User-Agent");
        String deviceType = detectDeviceType(userAgent);
        String deviceToken = UUID.nameUUIDFromBytes(token.getBytes()).toString(); // Gerar um token único para o dispositivo

        userDeviceService.registerDevice(user, new UserDeviceCreateDTO(deviceType, userAgent, deviceToken));

        return buildAuthResponse(user, token);
    }

    // (já retorna o token ao registrar)
    public AuthResponseDTO register(RegisterUserDTO dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }

        Role roleUser = roleRepository
                .findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Role USER não encontrada"));

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setStatus(UserStatus.ACTIVE);
        user.setRoles(Set.of(roleUser));

        userRepository.save(user);

        List<User> admins = userRepository.findAllByRoles_Name("ROLE_ADMIN");

        admins.forEach(admin ->
            notificationService.createNotification(
                new NotificationCreateDTO(
                    admin.getId(),
                    NotificationTypeCodes.USER_REGISTERED,
                    "Novo usuário registrado",
                    "O usuário " + user.getName() + " acabou de se registrar.",
                    null
                )
            )
        );

        CustomUserDetails userDetails = new CustomUserDetails(user);

        String token = jwtService.generateToken(userDetails);

        return buildAuthResponse(user, token);
    }

    public User getAuthenticatedUser() {

        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getUser();
        }

        throw new AccessDeniedException("Usuário não autenticado");
    }

    // Atualizar a senha do usuário
    @Transactional
    public void updatePassword(UpdatePasswordDTO dto) {

        UUID userId = getAuthenticatedUserId();
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        if (!passwordEncoder.matches(dto.currentPassword(), user.getPassword())) {
            throw new BadCredentialsException("Senha atual incorreta");
        }

        if(passwordEncoder.matches(dto.newPassword(), user.getPassword())) {
            throw new IllegalArgumentException("A nova senha deve ser diferente da senha atual");
        }

        user.setPassword(passwordEncoder.encode(dto.newPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    // Reativar Conta
    @Transactional
    public AuthResponseDTO reactivateAccount(ReactivateAccountDTO dto) {

        // 1. Buscar usuário pelo e-mail
        User user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        // 2. Verificar se realmente está inativo
        if (user.getStatus() != UserStatus.INACTIVE) {
            throw new IllegalStateException("Esta conta não está inativa.");
        }

        // 3. Validar senha
        if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
            throw new IllegalArgumentException("Credenciais inválidas.");
        }

        // 4. Reativar usuário
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        // 5. Criar token e logar automaticamente
        CustomUserDetails userDetails = new CustomUserDetails(user);
        String token = jwtService.generateToken(userDetails);

        // atualizar data do último login
        user.setLastLoginAt(LocalDateTime.now());

        userRepository.save(user);

        return buildAuthResponse(user, token);
    }


    public UUID getAuthenticatedUserId() {
        return getAuthenticatedUser().getId();
    }

    // Método auxiliar para resposta
    private AuthResponseDTO buildAuthResponse(User user, String token) {

        Set<String> roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return new AuthResponseDTO(
                token,
                "Bearer",
                jwtService.getExpiration(),
                user.getId(),
                user.getEmail(),
                roles
        );
    }

    private String detectDeviceType(String userAgent) {
        if (userAgent == null) return "unknown";

        String ua = userAgent.toLowerCase();

        if (ua.contains("android") || ua.contains("iphone")) {
            return "mobile";
        }

        if (ua.contains("ipad") || ua.contains("tablet")) {
            return "tablet";
        }

        return "desktop";
    }
}
