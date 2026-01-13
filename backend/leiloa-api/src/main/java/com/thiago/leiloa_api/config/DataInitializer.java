package com.thiago.leiloa_api.config;

import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.thiago.leiloa_api.domain.role.Role;
import com.thiago.leiloa_api.domain.user.User;
import com.thiago.leiloa_api.domain.user.UserStatus;
import com.thiago.leiloa_api.repository.RoleRepository;
import com.thiago.leiloa_api.repository.UserRepository;


@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {

            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseGet(() -> roleRepository.save(
                            new Role(null, "ROLE_USER", "Usuário padrão", null)
                    ));

            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseGet(() -> roleRepository.save(
                            new Role(null, "ROLE_ADMIN", "Administrador do sistema", null)
                    ));

            if (userRepository.findByEmail("admin@leiloa.com").isEmpty()) {
                User admin = new User();
                admin.setName("Administrador");
                admin.setEmail("admin@leiloa.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setStatus(UserStatus.ACTIVE);
                admin.setRoles(Set.of(adminRole));

                userRepository.save(admin);
            }
        };
    }
}
