package com.fiap.adjt3.pulse.care.scheduling.mappers;

import com.fiap.adjt3.pulse.care.scheduling.dtos.appointment.AppointmentRequestDTO;
import com.fiap.adjt3.pulse.care.scheduling.dtos.appointment.AppointmentResponseDTO;
import com.fiap.adjt3.pulse.care.scheduling.dtos.user.DoctorSummaryDTO;
import com.fiap.adjt3.pulse.care.scheduling.dtos.user.PatientSummaryDTO;
import com.fiap.adjt3.pulse.care.scheduling.enums.AppointmentStatus;
import com.fiap.adjt3.pulse.care.scheduling.models.Appointment;
import com.fiap.adjt3.pulse.care.scheduling.models.User;

public class AppointmentMapper {

  public static Appointment toEntity(AppointmentRequestDTO appointmentRequestDTO, User patient, User doctor) {
    return Appointment.builder()
        .patient(patient)
        .doctor(doctor)
        .appointmentDateTime(appointmentRequestDTO.appointmentDateTime())
        .specialty(appointmentRequestDTO.specialty())
        .observation(appointmentRequestDTO.observation())
        .location(appointmentRequestDTO.location())
        .status(AppointmentStatus.SCHEDULED)
        .build();
  }

  public static AppointmentResponseDTO toResponse(Appointment appointment) {
    PatientSummaryDTO patient = new PatientSummaryDTO(
        appointment.getPatient().getId(),
        appointment.getPatient().getName(),
        appointment.getPatient().getEmail(),
        appointment.getPatient().getPhone());

    DoctorSummaryDTO doctor = new DoctorSummaryDTO(
        appointment.getDoctor().getId(),
        appointment.getDoctor().getName(),
        appointment.getDoctor().getCrm());

    return new AppointmentResponseDTO(
        appointment.getId(),
        patient,
        doctor,
        appointment.getAppointmentDateTime(),
        appointment.getStatus(),
        appointment.getSpecialty(),
        appointment.getObservation(),
        appointment.getLocation(),
        appointment.getCreatedAt());
  }

}
