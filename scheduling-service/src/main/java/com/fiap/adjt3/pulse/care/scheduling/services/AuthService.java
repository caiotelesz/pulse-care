package com.fiap.adjt3.pulse.care.scheduling.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.fiap.adjt3.pulse.care.scheduling.dtos.auth.LoginRequestDTO;
import com.fiap.adjt3.pulse.care.scheduling.dtos.auth.LoginResponseDTO;
import com.fiap.adjt3.pulse.care.scheduling.security.JwtService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;

  public LoginResponseDTO login(LoginRequestDTO request) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.email(), request.password()));

    String role = authentication.getAuthorities().stream()
        .findFirst()
        .map(GrantedAuthority::getAuthority)
        .orElse("");

    String token = jwtService.generateToken(request.email(), role);

    log.info("User authenticated successfully: {}", request.email());

    return new LoginResponseDTO(token, "Bearer");
  }
}
