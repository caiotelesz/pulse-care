package com.fiap.adjt3.pulse.care.scheduling.models;

import java.time.LocalDateTime;

import com.fiap.adjt3.pulse.care.scheduling.enums.AppointmentStatus;
import com.fiap.adjt3.pulse.care.scheduling.models.common.BaseModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Appointment extends BaseModel {

  @ManyToOne
  @JoinColumn(name = "patient_id", nullable = false)
  private User patient;

  @ManyToOne
  @JoinColumn(name = "doctor_id", nullable = false)
  private User doctor;

  @Column(nullable = false)
  private LocalDateTime appointmentDateTime;

  @Column(nullable = false)
  private String specialty;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private AppointmentStatus status;

  @Column(columnDefinition = "TEXT")
  private String observation;

  @Column(nullable = false)
  private String location;
}
