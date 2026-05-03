package com.healthcare.appointment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientServiceClient patientServiceClient;

    @Autowired
    private AppointmentEventPublisher eventPublisher;

    public Appointment bookAppointment(Appointment appointment) {

        // SAGA STEP 1: Verify patient exists via REST call to Patient service
        if (!patientServiceClient.patientExists(appointment.getPatientId())) {
            throw new RuntimeException(
                    "Patient not found with id: " + appointment.getPatientId());
        }

        // SAGA STEP 2: Save appointment with PENDING status
        appointment.setStatus(AppointmentStatus.PENDING);
        Appointment saved = appointmentRepository.save(appointment);
        System.out.println(">>> Appointment saved with PENDING status, id: " + saved.getId());

        try {
            // SAGA STEP 3: Publish event to Kafka
            AppointmentBookedEvent event = new AppointmentBookedEvent(
                    saved.getId(),
                    saved.getPatientId(),
                    saved.getAppointmentDate(),
                    saved.getAppointmentTime(),
                    "BOOKED"
            );
            eventPublisher.publishEvent(event);

            // SAGA STEP 4: Mark as CONFIRMED
            saved.setStatus(AppointmentStatus.CONFIRMED);
            Appointment confirmed = appointmentRepository.save(saved);
            System.out.println(">>> Appointment CONFIRMED, id: " + confirmed.getId());
            return confirmed;

        } catch (Exception e) {
            // COMPENSATING TRANSACTION: something failed, mark as FAILED
            System.out.println(">>> Saga failed, running compensation: " + e.getMessage());
            saved.setStatus(AppointmentStatus.FAILED);
            appointmentRepository.save(saved);
            throw new RuntimeException("Appointment booking failed: " + e.getMessage());
        }
    }

    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found: " + id));
    }

    public List<Appointment> getAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    public Appointment cancelAppointment(Long id) {
        Appointment appointment = getAppointmentById(id);
        appointment.setStatus(AppointmentStatus.CANCELLED);

        // Publish cancellation event to Kafka
        AppointmentBookedEvent event = new AppointmentBookedEvent(
                appointment.getId(),
                appointment.getPatientId(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime(),
                "CANCELLED"
        );
        eventPublisher.publishEvent(event);

        return appointmentRepository.save(appointment);
    }
}