package com.fiap.adjt3.pulse.care.scheduling.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.fiap.adjt3.pulse.care.scheduling.dtos.appointment.AppointmentResponseDTO;
import com.fiap.adjt3.pulse.care.scheduling.services.AppointmentService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AppointmentGraphQLController {

  private final AppointmentService appointmentService;

  @QueryMapping
  public List<AppointmentResponseDTO> patientAppointments(@Argument UUID patientId, @Argument boolean onlyFuture) {
    return appointmentService.getPatientAppointments(patientId, onlyFuture);
  }
}
