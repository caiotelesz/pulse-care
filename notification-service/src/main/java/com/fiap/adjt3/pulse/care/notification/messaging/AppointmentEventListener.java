package com.fiap.adjt3.pulse.care.notification.messaging;

import java.time.format.DateTimeFormatter;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.fiap.adjt3.pulse.care.notification.dtos.event.AppointmentEventDTO;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AppointmentEventListener {

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

  @RabbitListener(queues = "${rabbitmq.queue.appointment}")
  public void handleAppointmentEvent(AppointmentEventDTO event) {
    log.info("Evento recebido: {} para a consulta {}", event.eventType(), event.appointmentId());

    sendReminder(event);
  }

  private void sendReminder(AppointmentEventDTO event) {
    String message = switch (event.eventType()) {
      case CREATED -> "Sua consulta de %s com Dr(a). %s foi agendada para %s, em %s."
          .formatted(event.specialty(), event.doctorName(),
              event.appointmentDateTime().format(DATE_FORMATTER), event.location());
      case UPDATED -> "Sua consulta de %s com Dr(a). %s foi atualizada para %s, em %s."
          .formatted(event.specialty(), event.doctorName(),
              event.appointmentDateTime().format(DATE_FORMATTER), event.location());
    };

    log.info("Lembrete enviado para {} <{}>: {}", event.patientName(), event.patientEmail(), message);
  }
}
