package com.fiap.adjt3.pulse.care.scheduling.controllers;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fiap.adjt3.pulse.care.scheduling.dtos.user.UserRequestDTO;
import com.fiap.adjt3.pulse.care.scheduling.dtos.user.UserResponseDTO;
import com.fiap.adjt3.pulse.care.scheduling.services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping
  public ResponseEntity<UserResponseDTO> createUser(@RequestBody @Valid UserRequestDTO request) {
    UserResponseDTO response = userService.createUser(request);
    return ResponseEntity.created(URI.create("/v1/users/" + response.id())).body(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserResponseDTO> getUserById(@PathVariable UUID id) {
    UserResponseDTO response = userService.getUserById(id);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/staff")
  public ResponseEntity<List<UserResponseDTO>> listStaff() {
    return ResponseEntity.ok(userService.listStaff());
  }
}
