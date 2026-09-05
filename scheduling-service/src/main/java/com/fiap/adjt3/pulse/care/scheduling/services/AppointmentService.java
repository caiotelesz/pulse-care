package com.fiap.adjt3.pulse.care.scheduling.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fiap.adjt3.pulse.care.scheduling.dtos.appointment.AppointmentRequestDTO;
import com.fiap.adjt3.pulse.care.scheduling.dtos.appointment.AppointmentResponseDTO;
import com.fiap.adjt3.pulse.care.scheduling.enums.UserRole;
import com.fiap.adjt3.pulse.care.scheduling.exceptions.ForbiddenException;
import com.fiap.adjt3.pulse.care.scheduling.exceptions.InvalidRequestException;
import com.fiap.adjt3.pulse.care.scheduling.exceptions.ResourceNotFoundException;
import com.fiap.adjt3.pulse.care.scheduling.mappers.AppointmentMapper;
import com.fiap.adjt3.pulse.care.scheduling.models.Appointment;
import com.fiap.adjt3.pulse.care.scheduling.models.User;
import com.fiap.adjt3.pulse.care.scheduling.repositories.UserRepository;
import com.fiap.adjt3.pulse.care.scheduling.repositories.AppointmentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {

  private final AppointmentRepository appointmentRepository;
  private final UserRepository userRepository;

  public AppointmentResponseDTO createAppointment(AppointmentRequestDTO request) {
    log.info("Criando consulta para o paciente {} com o médico {} em {}", request.patientId(), request.doctorId(),
        request.appointmentDateTime());

    User patient = findUserOrThrow(request.patientId(), "Paciente não encontrado");
    User doctor = findUserOrThrow(request.doctorId(), "Médico não encontrado");

    validateRoles(patient, doctor);

    Appointment appointment = AppointmentMapper.toEntity(request, patient, doctor);
    Appointment savedAppointment = appointmentRepository.save(appointment);

    return AppointmentMapper.toResponse(savedAppointment);
  }

  public AppointmentResponseDTO updateAppointment(UUID id, AppointmentRequestDTO request) {
    log.info("Atualizando consulta com id: {}", id);

    Appointment appointment = findAppointmentOrThrow(id);

    User patient = findUserOrThrow(request.patientId(), "Paciente não encontrado");
    User doctor = findUserOrThrow(request.doctorId(), "Médico não encontrado");

    validateRoles(patient, doctor);

    appointment.setPatient(patient);
    appointment.setDoctor(doctor);
    appointment.setAppointmentDateTime(request.appointmentDateTime());
    appointment.setSpecialty(request.specialty());
    appointment.setObservation(request.observation());
    appointment.setLocation(request.location());

    Appointment updatedAppointment = appointmentRepository.save(appointment);

    return AppointmentMapper.toResponse(updatedAppointment);
  }

  public AppointmentResponseDTO getAppointmentById(UUID id) {
    log.info("Buscando uma consulta pelo id: {}", id);

    Appointment appointment = findAppointmentOrThrow(id);

    User currentUser = getAuthenticatedUser();
    if (currentUser.getRole() == UserRole.PATIENT && !currentUser.getId().equals(appointment.getPatient().getId())) {
      log.warn("Paciente {} tentou visualizar consulta de outro paciente ({})", currentUser.getId(), id);
      throw new ForbiddenException("Você só pode visualizar as suas próprias consultas");
    }

    return AppointmentMapper.toResponse(appointment);
  }

  public List<AppointmentResponseDTO> getPatientAppointments(UUID patientId, boolean onlyFuture) {
    log.info("Buscando consultas do paciente com id: {} (somente futuras: {})", patientId, onlyFuture);

    User currentUser = getAuthenticatedUser();
    if (currentUser.getRole() == UserRole.PATIENT && !currentUser.getId().equals(patientId)) {
      log.warn("Paciente {} tentou visualizar consultas do paciente {}", currentUser.getId(), patientId);
      throw new ForbiddenException("Você só pode visualizar as suas próprias consultas");
    }

    List<Appointment> appointments = onlyFuture
        ? appointmentRepository.findByPatientIdAndAppointmentDateTimeAfterOrderByAppointmentDateTime(patientId,
            LocalDateTime.now())
        : appointmentRepository.findByPatientId(patientId);

    return appointments.stream()
        .map(AppointmentMapper::toResponse)
        .toList();
  }

  public List<AppointmentResponseDTO> listDoctorAppointmentsByDateRange(UUID doctorId, LocalDateTime start,
      LocalDateTime end) {
    log.info("Buscando consultas para o médico com id: {} entre {} e {}", doctorId, start, end);

    return appointmentRepository.findByDoctorIdAndAppointmentDateTimeBetweenOrderByAppointmentDateTime(doctorId, start,
        end)
        .stream()
        .map(AppointmentMapper::toResponse)
        .toList();
  }

  private User findUserOrThrow(UUID id, String errorMessage) {
    return userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(errorMessage));
  }

  private Appointment findAppointmentOrThrow(UUID id) {
    return appointmentRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada"));
  }

  private User getAuthenticatedUser() {
    String email = SecurityContextHolder.getContext().getAuthentication().getName();

    return userRepository.findByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException("Usuário autenticado não encontrado"));
  }

  private void validateRoles(User patient, User doctor) {
    if (patient.getRole() != UserRole.PATIENT) {
      log.warn("O id informado em patientId ({}) não pertence a um paciente", patient.getId());
      throw new InvalidRequestException("O id informado em patientId não pertence a um paciente");
    }

    if (doctor.getRole() != UserRole.DOCTOR) {
      log.warn("O id informado em doctorId ({}) não pertence a um médico", doctor.getId());
      throw new InvalidRequestException("O id informado em doctorId não pertence a um médico");
    }
  }
}
