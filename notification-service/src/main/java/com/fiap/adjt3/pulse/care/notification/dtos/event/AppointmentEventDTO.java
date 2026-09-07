package com.fiap.adjt3.pulse.care.notification.dtos.event;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fiap.adjt3.pulse.care.notification.enums.AppointmentEventType;

public record AppointmentEventDTO(
        AppointmentEventType eventType,
        UUID appointmentId,
        UUID patientId,
        String patientName,
        String patientEmail,
        String doctorName,
        LocalDateTime appointmentDateTime,
        String specialty,
        String location) {
}
