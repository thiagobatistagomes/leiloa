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

            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseThrow(() ->
                            new IllegalStateException("ROLE_ADMIN não encontrada. Execute as migrations.")
                    );

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

