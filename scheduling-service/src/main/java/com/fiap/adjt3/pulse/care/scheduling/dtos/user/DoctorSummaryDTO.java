package com.fiap.adjt3.pulse.care.scheduling.dtos.user;

import java.util.UUID;

public record DoctorSummaryDTO(
    UUID id,
    String name,
    String crm) {

}
