package com.fiap.adjt3.pulse.care.scheduling.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

@Service
public class JwtService {

  private static final String ISSUER = "pulse-care-scheduling";
  private static final long EXPIRATION_HOURS = 2;

  @Value("${api.security.token.secret}")
  private String secret;

  public String generateToken(String email, String role) {
    return JWT.create()
        .withIssuer(ISSUER)
        .withSubject(email)
        .withClaim("role", role)
        .withIssuedAt(Instant.now())
        .withExpiresAt(Instant.now().plus(EXPIRATION_HOURS, ChronoUnit.HOURS))
        .sign(algorithm());
  }

  public String validateTokenAndGetEmail(String token) {
    DecodedJWT decodedJWT = JWT.require(algorithm())
        .withIssuer(ISSUER)
        .build()
        .verify(token);

    return decodedJWT.getSubject();
  }

  private Algorithm algorithm() {
    return Algorithm.HMAC256(secret);
  }
}
