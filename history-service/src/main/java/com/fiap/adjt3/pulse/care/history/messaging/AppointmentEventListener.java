package com.fiap.adjt3.pulse.care.history.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.fiap.adjt3.pulse.care.history.dtos.event.AppointmentEventDTO;
import com.fiap.adjt3.pulse.care.history.models.AppointmentHistoryEntry;
import com.fiap.adjt3.pulse.care.history.repositories.AppointmentHistoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppointmentEventListener {

  private final AppointmentHistoryRepository appointmentHistoryRepository;

  @RabbitListener(queues = "${rabbitmq.queue.appointment-history}")
  public void handleAppointmentEvent(AppointmentEventDTO event) {
    log.info("Received {} event for appointment {}", event.eventType(), event.appointmentId());

    AppointmentHistoryEntry entry = AppointmentHistoryEntry.builder()
        .appointmentId(event.appointmentId())
        .eventType(event.eventType())
        .patientId(event.patientId())
        .patientName(event.patientName())
        .doctorName(event.doctorName())
        .appointmentDateTime(event.appointmentDateTime())
        .specialty(event.specialty())
        .location(event.location())
        .build();

    appointmentHistoryRepository.save(entry);

    log.info("Stored history entry for appointment {}", event.appointmentId());
  }
}
