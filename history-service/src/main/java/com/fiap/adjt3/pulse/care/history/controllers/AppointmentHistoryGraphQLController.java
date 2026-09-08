package com.fiap.adjt3.pulse.care.history.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.fiap.adjt3.pulse.care.history.models.AppointmentHistoryEntry;
import com.fiap.adjt3.pulse.care.history.repositories.AppointmentHistoryRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AppointmentHistoryGraphQLController {

  private final AppointmentHistoryRepository appointmentHistoryRepository;

  @QueryMapping
  public List<AppointmentHistoryEntry> patientAppointmentHistory(@Argument UUID patientId) {
    return appointmentHistoryRepository.findByPatientIdOrderByRecordedAtDesc(patientId);
  }
}
