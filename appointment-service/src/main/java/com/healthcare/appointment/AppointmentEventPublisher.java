package com.healthcare.appointment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class AppointmentEventPublisher {

    private static final String TOPIC = "appointment-events";

    @Autowired
    private KafkaTemplate<String, AppointmentBookedEvent> kafkaTemplate;

    public void publishEvent(AppointmentBookedEvent event) {
        kafkaTemplate.send(TOPIC, event.getAppointmentId().toString(), event);
        System.out.println(">>> Kafka event published: " + event.getEventType()
                + " for appointment " + event.getAppointmentId());
    }
}