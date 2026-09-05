package com.fiap.adjt3.pulse.care.scheduling.dtos.user;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fiap.adjt3.pulse.care.scheduling.enums.UserRole;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserResponseDTO(
        UUID id,
        String name,
        String email,
        String cpf,
        String phone,
        UserRole role,
        String crm,
        LocalDateTime createdAt) {

}
