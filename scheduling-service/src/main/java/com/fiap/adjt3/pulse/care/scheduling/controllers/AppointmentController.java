package com.fiap.adjt3.pulse.care.scheduling.controllers;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fiap.adjt3.pulse.care.scheduling.dtos.appointment.AppointmentRequestDTO;
import com.fiap.adjt3.pulse.care.scheduling.dtos.appointment.AppointmentResponseDTO;
import com.fiap.adjt3.pulse.care.scheduling.services.AppointmentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

  private final AppointmentService appointmentService;

  @PostMapping
  public ResponseEntity<AppointmentResponseDTO> createAppointment(@RequestBody @Valid AppointmentRequestDTO request) {
    AppointmentResponseDTO response = appointmentService.createAppointment(request);
    return ResponseEntity.created(URI.create("/v1/appointments/" + response.id())).body(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<AppointmentResponseDTO> getAppointmentById(@PathVariable UUID id) {
    AppointmentResponseDTO response = appointmentService.getAppointmentById(id);
    return ResponseEntity.ok(response);
  }

  @PutMapping("/{id}")
  public ResponseEntity<AppointmentResponseDTO> updateAppointment(@PathVariable UUID id,
      @RequestBody @Valid AppointmentRequestDTO request) {
    AppointmentResponseDTO response = appointmentService.updateAppointment(id, request);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/patient/{patientId}")
  public ResponseEntity<List<AppointmentResponseDTO>> getPatientAppointments(
      @PathVariable UUID patientId,
      @RequestParam(value = "onlyFuture", defaultValue = "false") boolean onlyFuture) {
    List<AppointmentResponseDTO> response = appointmentService.getPatientAppointments(patientId, onlyFuture);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/doctor/{doctorId}")
  public ResponseEntity<List<AppointmentResponseDTO>> listDoctorAppointmentsByDateRange(
      @PathVariable UUID doctorId,
      @RequestParam("start") LocalDateTime start,
      @RequestParam("end") LocalDateTime end) {
    List<AppointmentResponseDTO> response = appointmentService.listDoctorAppointmentsByDateRange(doctorId, start, end);

    return ResponseEntity.ok(response);
  }
}
