package com.fiap.adjt3.pulse.care.scheduling.services;

import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fiap.adjt3.pulse.care.scheduling.dtos.user.UserRequestDTO;
import com.fiap.adjt3.pulse.care.scheduling.dtos.user.UserResponseDTO;
import com.fiap.adjt3.pulse.care.scheduling.enums.UserRole;
import com.fiap.adjt3.pulse.care.scheduling.exceptions.InvalidRequestException;
import com.fiap.adjt3.pulse.care.scheduling.exceptions.ResourceConflictException;
import com.fiap.adjt3.pulse.care.scheduling.exceptions.ResourceNotFoundException;
import com.fiap.adjt3.pulse.care.scheduling.mappers.UserMapper;
import com.fiap.adjt3.pulse.care.scheduling.models.User;
import com.fiap.adjt3.pulse.care.scheduling.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserResponseDTO createUser(UserRequestDTO request) {
    log.info("Creating user with email: {}", request.email());

    if (userRepository.existsByEmail(request.email())) {
      log.warn("Attempt to create user with an email already in use: {}", request.email());
      throw new ResourceConflictException("Email já cadastrado");
    }

    if (userRepository.existsByCpf(request.cpf())) {
      log.warn("Attempt to create user with a CPF already in use: {}", request.cpf());
      throw new ResourceConflictException("CPF já cadastrado");
    }

    boolean requiresCrm = request.role() == UserRole.DOCTOR || request.role() == UserRole.NURSE;
    if (requiresCrm) {
      if (request.crm() == null || request.crm().isBlank()) {
        log.warn("Attempt to create a {} without a CRM", request.role());
        throw new InvalidRequestException("CRM é obrigatório para médicos e enfermeiros");
      }

      if (userRepository.existsByCrm(request.crm())) {
        log.warn("Attempt to create user with a CRM already in use: {}", request.crm());
        throw new ResourceConflictException("CRM já cadastrado");
      }
    }

    User user = UserMapper.toEntity(request);
    user.setPassword(passwordEncoder.encode(request.password()));

    User savedUser = userRepository.save(user);

    log.info("User created successfully with ID: {}", savedUser.getId());

    return UserMapper.toResponse(savedUser);
  }

  public List<UserResponseDTO> listStaff() {
    log.info("Fetching medical staff (doctors and nurses)");

    return userRepository.findByRoleIn(List.of(UserRole.DOCTOR, UserRole.NURSE)).stream()
        .map(UserMapper::toResponse)
        .toList();
  }

  public UserResponseDTO getUserById(UUID id) {
    log.info("Fetching user with ID: {}", id);

    User user = findUserOrThrow(id);

    return UserMapper.toResponse(user);
  }

  private User findUserOrThrow(UUID id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
  }

}
