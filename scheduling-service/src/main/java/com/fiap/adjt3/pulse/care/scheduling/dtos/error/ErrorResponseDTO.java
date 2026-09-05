package com.fiap.adjt3.pulse.care.scheduling.dtos.error;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
    LocalDateTime timestamp,
    int status,
    String error,
    String message,
    String path) {
}
