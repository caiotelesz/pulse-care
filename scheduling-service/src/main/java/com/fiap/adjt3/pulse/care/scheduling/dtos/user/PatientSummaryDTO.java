package com.fiap.adjt3.pulse.care.scheduling.dtos.user;

import java.util.UUID;

public record PatientSummaryDTO(
    UUID id,
    String name,
    String email,
    String phone) {
}
