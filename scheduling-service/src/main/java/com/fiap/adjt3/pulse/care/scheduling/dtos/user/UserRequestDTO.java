package com.fiap.adjt3.pulse.care.scheduling.dtos.user;

import com.fiap.adjt3.pulse.care.scheduling.enums.UserRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(
    @NotBlank(message = "Nome é obrigatório") String name,

    @NotBlank(message = "Email é obrigatório") @Email(message = "Email inválido") String email,

    @NotBlank(message = "Senha é obrigatória") @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres") String password,

    @NotBlank(message = "CPF é obrigatório") @Pattern(regexp = "\\d{11}", message = "CPF deve ter 11 dígitos") String cpf,

    @NotBlank(message = "Telefone é obrigatório") String phone,

    @NotNull(message = "Role é obrigatória") UserRole role,

    String crm) {
}
