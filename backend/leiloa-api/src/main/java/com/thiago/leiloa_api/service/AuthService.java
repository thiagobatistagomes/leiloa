package com.thiago.leiloa_api.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.thiago.leiloa_api.config.JwtService;
import com.thiago.leiloa_api.domain.role.Role;
import com.thiago.leiloa_api.domain.user.User;
import com.thiago.leiloa_api.domain.user.UserStatus;
import com.thiago.leiloa_api.dto.auth.AuthResponseDTO;
import com.thiago.leiloa_api.dto.auth.LoginDTO;
import com.thiago.leiloa_api.dto.auth.RegisterUserDTO;
import com.thiago.leiloa_api.repository.RoleRepository;
import com.thiago.leiloa_api.repository.UserRepository;

// Serviço de autenticação 

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // LOGIN
    public AuthResponseDTO login(LoginDTO dto) {

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

        String token = jwtService.generateToken(userDetails);

        return buildAuthResponse(user, token);
    }

    // REGISTER (já retorna o token ao registrar)
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
        user.setStatus(UserStatus.ACTIVE);
        user.setRoles(Set.of(roleUser));

        userRepository.save(user);

        CustomUserDetails userDetails = new CustomUserDetails(user);

        String token = jwtService.generateToken(userDetails);

        return buildAuthResponse(user, token);
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
}
