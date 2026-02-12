package com.thiago.leiloa_api.specification;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;

import com.thiago.leiloa_api.domain.role.Role;
import com.thiago.leiloa_api.domain.user.User;
import com.thiago.leiloa_api.domain.user.UserStatus;

import jakarta.persistence.criteria.Join;

public class UserSpecification {

    // NAME
    public static Specification<User> nameContains(String name) {
        if (name == null || name.isBlank()) return null;

        return (root, query, cb) ->
            cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    // EMAIL
    public static Specification<User> emailContains(String email) {
        if (email == null || email.isBlank()) return null;

        return (root, query, cb) ->
            cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }

    // STATUS
    public static Specification<User> hasStatus(UserStatus status) {
        if (status == null) return null;

        return (root, query, cb) ->
            cb.equal(root.get("status"), status);
    }

    // ROLE (JOIN)
    public static Specification<User> hasRole(String role) {
        if (role == null || role.isBlank()) return null;

        return (root, query, cb) -> {
            Join<User, Role> join = root.join("roles");
            return cb.equal(cb.lower(join.get("name")), role.toLowerCase());
        };
    }

    // CREATED BETWEEN
    public static Specification<User> createdBetween(LocalDateTime from, LocalDateTime to) {
        if (from == null && to == null) return null;

        return (root, query, cb) -> {
            if (from != null && to != null) {
                return cb.between(root.get("createdAt"), from, to);
            }
            if (from != null) {
                return cb.greaterThanOrEqualTo(root.get("createdAt"), from);
            }
            return cb.lessThanOrEqualTo(root.get("createdAt"), to);
        };
    }

    // LAST LOGIN BETWEEN
    public static Specification<User> lastLoginBetween(LocalDateTime from, LocalDateTime to) {
        if (from == null && to == null) return null;

        return (root, query, cb) -> {
            if (from != null && to != null) {
                return cb.between(root.get("lastLoginAt"), from, to);
            }
            if (from != null) {
                return cb.greaterThanOrEqualTo(root.get("lastLoginAt"), from);
            }
            return cb.lessThanOrEqualTo(root.get("lastLoginAt"), to);
        };
    }
}
