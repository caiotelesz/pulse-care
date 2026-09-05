package com.fiap.adjt3.pulse.care.scheduling.exceptions;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.fiap.adjt3.pulse.care.scheduling.dtos.error.ErrorResponseDTO;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponseDTO> handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
    log.warn("Resource not found: {}", ex.getMessage());
    return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
  }

  @ExceptionHandler(ResourceConflictException.class)
  public ResponseEntity<ErrorResponseDTO> handleResourceConflict(ResourceConflictException ex, WebRequest request) {
    log.warn("Resource conflict: {}", ex.getMessage());
    return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
  }

  @ExceptionHandler(InvalidRequestException.class)
  public ResponseEntity<ErrorResponseDTO> handleInvalidRequest(InvalidRequestException ex, WebRequest request) {
    log.warn("Invalid request: {}", ex.getMessage());
    return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
  }

  @ExceptionHandler(ForbiddenException.class)
  public ResponseEntity<ErrorResponseDTO> handleForbidden(ForbiddenException ex, WebRequest request) {
    log.warn("Access denied: {}", ex.getMessage());
    return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request);
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ErrorResponseDTO> handleAuthentication(AuthenticationException ex, WebRequest request) {
    log.warn("Authentication failed: {}", ex.getMessage());
    return buildResponse(HttpStatus.UNAUTHORIZED, "Credenciais inválidas", request);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponseDTO> handleValidation(MethodArgumentNotValidException ex, WebRequest request) {
    String message = ex.getBindingResult().getFieldErrors().stream()
        .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
        .collect(Collectors.joining(", "));

    log.warn("Validation failed: {}", message);

    return buildResponse(HttpStatus.BAD_REQUEST, message, request);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponseDTO> handleMessageNotReadable(HttpMessageNotReadableException ex,
      WebRequest request) {
    log.warn("Malformed request body: {}", ex.getMessage());
    return buildResponse(HttpStatus.BAD_REQUEST, "Corpo da requisição inválido ou mal formatado", request);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponseDTO> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
      WebRequest request) {
    String message = String.format("Valor inválido para o parâmetro '%s': %s", ex.getName(), ex.getValue());

    log.warn("Type mismatch: {}", message);

    return buildResponse(HttpStatus.BAD_REQUEST, message, request);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponseDTO> handleUnexpected(Exception ex, WebRequest request) {
    log.error("Unexpected error", ex);
    return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno no servidor", request);
  }

  private ResponseEntity<ErrorResponseDTO> buildResponse(HttpStatus status, String message, WebRequest request) {
    ErrorResponseDTO body = new ErrorResponseDTO(
        LocalDateTime.now(),
        status.value(),
        status.getReasonPhrase(),
        message,
        request.getDescription(false).replace("uri=", ""));

    return ResponseEntity.status(status).body(body);
  }
}
