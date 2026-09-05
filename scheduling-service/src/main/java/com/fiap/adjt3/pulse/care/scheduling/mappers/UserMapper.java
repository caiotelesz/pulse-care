package com.fiap.adjt3.pulse.care.scheduling.mappers;

import com.fiap.adjt3.pulse.care.scheduling.dtos.user.UserResponseDTO;
import com.fiap.adjt3.pulse.care.scheduling.enums.UserRole;
import com.fiap.adjt3.pulse.care.scheduling.models.User;
import com.fiap.adjt3.pulse.care.scheduling.dtos.user.UserRequestDTO;

public class UserMapper {
  public static User toEntity(UserRequestDTO userRequestDTO) {
    boolean hasCrm = userRequestDTO.role() == UserRole.DOCTOR || userRequestDTO.role() == UserRole.NURSE;

    return User.builder()
        .name(userRequestDTO.name())
        .email(userRequestDTO.email())
        .password(userRequestDTO.password())
        .cpf(userRequestDTO.cpf())
        .phone(userRequestDTO.phone())
        .role(userRequestDTO.role())
        .crm(hasCrm ? userRequestDTO.crm() : null)
        .build();
  }

  public static UserResponseDTO toResponse(User user) {
    return new UserResponseDTO(
        user.getId(),
        user.getName(),
        user.getEmail(),
        user.getCpf(),
        user.getPhone(),
        user.getRole(),
        user.getCrm(),
        user.getCreatedAt());
  }
}
