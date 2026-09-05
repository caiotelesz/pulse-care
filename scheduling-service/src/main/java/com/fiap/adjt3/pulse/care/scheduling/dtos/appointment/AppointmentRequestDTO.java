package com.fiap.adjt3.pulse.care.scheduling.dtos.appointment;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AppointmentRequestDTO(
        @NotNull(message = "Paciente é obrigatório") UUID patientId,

        @NotNull(message = "Médico é obrigatório") UUID doctorId,

        @NotNull(message = "Data e hora são obrigatórios") @Future(message = "Data da consulta deve ser no futuro") LocalDateTime appointmentDateTime,

        @NotBlank(message = "Especialidade é obrigatória") String specialty,

        String observation,

        @NotBlank(message = "Local é obrigatório") String location) {
}
