package com.fiap.adjt3.pulse.care.scheduling.repositories;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fiap.adjt3.pulse.care.scheduling.enums.UserRole;
import com.fiap.adjt3.pulse.care.scheduling.models.User;

public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByEmail(String email);

  Optional<User> findByCpf(String cpf);

  List<User> findByRole(UserRole role);

  List<User> findByRoleIn(Collection<UserRole> roles);

  boolean existsByEmail(String email);

  boolean existsByCpf(String cpf);

  boolean existsByCrm(String crm);
}
