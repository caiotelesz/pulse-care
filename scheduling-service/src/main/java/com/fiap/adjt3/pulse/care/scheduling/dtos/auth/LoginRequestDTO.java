package com.fiap.adjt3.pulse.care.scheduling.dtos.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
    @NotBlank(message = "Email é obrigatório") @Email(message = "Email inválido") String email,

    @NotBlank(message = "Senha é obrigatória") String password) {
}
