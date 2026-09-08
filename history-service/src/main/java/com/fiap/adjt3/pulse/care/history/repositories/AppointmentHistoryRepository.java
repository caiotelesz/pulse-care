package com.fiap.adjt3.pulse.care.history.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fiap.adjt3.pulse.care.history.models.AppointmentHistoryEntry;

public interface AppointmentHistoryRepository extends JpaRepository<AppointmentHistoryEntry, UUID> {

  List<AppointmentHistoryEntry> findByPatientIdOrderByRecordedAtDesc(UUID patientId);
}
