package com.healthcare.appointment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentBookedEvent {

    private Long appointmentId;
    private Long patientId;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String eventType;   // "BOOKED" or "CANCELLED"
}