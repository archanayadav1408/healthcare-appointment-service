package com.healthcare.appointment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    // POST /api/appointments
    @PostMapping
    public ResponseEntity<Appointment> bookAppointment(
            @RequestBody Appointment appointment) {
        Appointment booked = appointmentService.bookAppointment(appointment);
        return ResponseEntity.status(HttpStatus.CREATED).body(booked);
    }

    // GET /api/appointments/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Appointment> getAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.getAppointmentById(id));
    }

    // GET /api/appointments/patient/{patientId}
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Appointment>> getByPatient(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(
                appointmentService.getAppointmentsByPatient(patientId));
    }

    // PUT /api/appointments/{id}/cancel
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Appointment> cancelAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.cancelAppointment(id));
    }
}