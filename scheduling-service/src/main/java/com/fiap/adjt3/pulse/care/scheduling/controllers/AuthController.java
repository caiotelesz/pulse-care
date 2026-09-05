package com.fiap.adjt3.pulse.care.scheduling.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fiap.adjt3.pulse.care.scheduling.dtos.auth.LoginRequestDTO;
import com.fiap.adjt3.pulse.care.scheduling.dtos.auth.LoginResponseDTO;
import com.fiap.adjt3.pulse.care.scheduling.services.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO request) {
    return ResponseEntity.ok(authService.login(request));
  }
}
