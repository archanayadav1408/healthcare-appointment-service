package com.healthcare.appointment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PatientServiceClient {

    private final RestTemplate restTemplate;

    @Value("${patient.service.url}")
    private String patientServiceUrl;

    public PatientServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean patientExists(Long patientId) {
        try {
            Boolean exists = restTemplate.getForObject(
                    patientServiceUrl + "/api/patients/" + patientId + "/exists",
                    Boolean.class
            );
            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            System.out.println("Patient service unreachable: " + e.getMessage());
            return false;
        }
    }
}