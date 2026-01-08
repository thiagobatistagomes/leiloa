package com.thiago.leiloa_api.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.thiago.leiloa_api.domain.role.Role;
import com.thiago.leiloa_api.repository.RoleRepository;

// Inserir roles padrão apenas se não existirem no banco de dados

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initRoles(RoleRepository roleRepository) {
        return args -> {

            if (roleRepository.count() == 0) {

                Role userRole = new Role(
                    null,
                    "USER",
                    "Usuário padrão do sistema",
                    null
                );

                Role adminRole = new Role(
                    null,
                    "ADMIN",
                    "Administrador do sistema",
                    null
                );

                roleRepository.saveAll(List.of(userRole, adminRole));
            }
        };
    }
}

