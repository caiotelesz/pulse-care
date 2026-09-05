package com.fiap.adjt3.pulse.care.scheduling.dtos.appointment;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fiap.adjt3.pulse.care.scheduling.dtos.user.DoctorSummaryDTO;
import com.fiap.adjt3.pulse.care.scheduling.dtos.user.PatientSummaryDTO;
import com.fiap.adjt3.pulse.care.scheduling.enums.AppointmentStatus;

public record AppointmentResponseDTO(
        UUID id,
        PatientSummaryDTO patient,
        DoctorSummaryDTO doctor,
        LocalDateTime appointmentDateTime,
        AppointmentStatus status,
        String specialty,
        String observation,
        String location,
        LocalDateTime createdAt) {
}
