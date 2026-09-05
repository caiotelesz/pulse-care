package com.fiap.adjt3.pulse.care.scheduling.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum UserRole {
  @JsonProperty("MEDICO")
  DOCTOR,

  @JsonProperty("ENFERMEIRO")
  NURSE,

  @JsonProperty("PACIENTE")
  PATIENT
}
