package com.thiago.leiloa_api.repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.thiago.leiloa_api.domain.user.User;

public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);

    @Query("""
        SELECT u FROM User u
        JOIN FETCH u.roles
        WHERE u.email = :email
    """)
    Optional<User> findByEmailWithRoles(String email);

    boolean existsByEmail(String email);

    List<User> findAllByRoles_Name(String roleName);
}
