package com.fiap.adjt3.pulse.care.scheduling.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fiap.adjt3.pulse.care.scheduling.dtos.event.AppointmentEventDTO;
import com.fiap.adjt3.pulse.care.scheduling.enums.AppointmentEventType;
import com.fiap.adjt3.pulse.care.scheduling.models.Appointment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppointmentEventPublisher {

  private final RabbitTemplate rabbitTemplate;

  @Value("${rabbitmq.exchange.appointment}")
  private String appointmentExchange;

  @Value("${rabbitmq.routing.key.appointment}")
  private String appointmentRoutingKey;

  public void publish(Appointment appointment, AppointmentEventType eventType) {
    AppointmentEventDTO event = toEvent(appointment, eventType);

    log.info("Publishing {} event for appointment {}", eventType, appointment.getId());

    rabbitTemplate.convertAndSend(appointmentExchange, appointmentRoutingKey, event);
  }

  private AppointmentEventDTO toEvent(Appointment appointment, AppointmentEventType eventType) {
    return new AppointmentEventDTO(
        eventType,
        appointment.getId(),
        appointment.getPatient().getId(),
        appointment.getPatient().getName(),
        appointment.getPatient().getEmail(),
        appointment.getDoctor().getName(),
        appointment.getAppointmentDateTime(),
        appointment.getSpecialty(),
        appointment.getLocation());
  }
}
