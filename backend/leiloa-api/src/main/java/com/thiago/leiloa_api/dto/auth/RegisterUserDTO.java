package com.thiago.leiloa_api.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterUserDTO {

    @NotBlank(message = "Nome é obrigatório")
    private String name;

    @NotBlank(message = "Número de telefone é obrigatório")
    @Size(min = 10, max = 15, message = "Número de telefone deve ter entre 10 e 15 caracteres")
    @Pattern(
        regexp = "^\\(?[1-9]{2}\\)?\\s?9\\d{4}-?\\d{4}$",
        message = "Telefone inválido (use padrão brasileiro com DDD)"
    )
    private String phoneNumber;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private String password;
    
}
