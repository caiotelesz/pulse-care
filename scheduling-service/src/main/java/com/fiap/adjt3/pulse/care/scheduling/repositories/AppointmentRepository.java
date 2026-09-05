package com.fiap.adjt3.pulse.care.scheduling.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fiap.adjt3.pulse.care.scheduling.enums.AppointmentStatus;
import com.fiap.adjt3.pulse.care.scheduling.models.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

        List<Appointment> findByPatientId(UUID patientId);

        List<Appointment> findByDoctorId(UUID doctorId);

        List<Appointment> findByStatus(AppointmentStatus status);

        // Find appointments by patient id and appointment datetime after a given
        // datetime
        List<Appointment> findByPatientIdAndAppointmentDateTimeAfterOrderByAppointmentDateTime(UUID patientId,
                        LocalDateTime dateTime);

        // Find appointments by patient id and appointment datetime before a given
        // datetime
        List<Appointment> findByPatientIdAndAppointmentDateTimeBeforeOrderByAppointmentDateTimeDesc(UUID patientId,
                        LocalDateTime dateTime);

        // Find appointments by doctor id and appointment datetime between two given
        // datetimes
        List<Appointment> findByDoctorIdAndAppointmentDateTimeBetweenOrderByAppointmentDateTime(UUID doctorId,
                        LocalDateTime start, LocalDateTime end);

}
