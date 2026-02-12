package com.thiago.leiloa_api.service;





import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.thiago.leiloa_api.domain.user.User;
import com.thiago.leiloa_api.domain.user.UserStatus;

// Adaptar a entidade User para o formato que o Spring Security entende ou seja implementando UserDetails

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    // Quais autoridades (roles) o usuário possui?
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authorities =
                user.getRoles()
                    .stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName()))
                    .collect(Collectors.toSet());

        return authorities;
    }

    // Qual a senha do usuário?
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // Qual é o username do usuário (email)?
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    // Apenas Usuários ACTIVE podem autenticar; INACTIVE podem solicitar reativação;

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.getStatus() != UserStatus.BLOCKED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus() != UserStatus.BLOCKED;
    }

    public User getUser() {
        return user;
    }
}
