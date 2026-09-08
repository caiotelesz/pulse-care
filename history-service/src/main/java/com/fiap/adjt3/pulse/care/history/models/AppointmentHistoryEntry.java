package com.fiap.adjt3.pulse.care.history.models;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fiap.adjt3.pulse.care.history.enums.AppointmentEventType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "appointment_history")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentHistoryEntry {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private UUID appointmentId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private AppointmentEventType eventType;

  @Column(nullable = false)
  private UUID patientId;

  @Column(nullable = false)
  private String patientName;

  @Column(nullable = false)
  private String doctorName;

  @Column(nullable = false)
  private LocalDateTime appointmentDateTime;

  @Column(nullable = false)
  private String specialty;

  @Column(nullable = false)
  private String location;

  @Column(nullable = false, updatable = false)
  private LocalDateTime recordedAt;

  @PrePersist
  public void prePersist() {
    this.recordedAt = LocalDateTime.now();
  }
}
