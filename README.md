# Healthcare Appointment Service

Microservice responsible for booking and managing appointments.
Implements the **Saga Pattern** for distributed transactions and publishes
events to **Apache Kafka** for async notification.

Part of the Healthcare Appointment System built for SEZG583 - Scalable Services assignment.

## Tech Stack
- Java 17
- Spring Boot 3.5.13
- Spring Data JPA
- Spring for Apache Kafka
- MySQL 8.0
- Maven

## Port
Runs on **port 8082**

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/appointments` | Book a new appointment |
| GET | `/api/appointments/{id}` | Get appointment by ID |
| GET | `/api/appointments/patient/{patientId}` | Get all appointments for a patient |
| PUT | `/api/appointments/{id}/cancel` | Cancel an appointment |

## Sample Request

### Book Appointment
```json
POST /api/appointments
{
    "patientId": 1,
    "doctorId": 101,
    "appointmentDate": "2026-04-15",
    "appointmentTime": "10:30:00"
}
```

### Sample Response
```json
{
    "id": 1,
    "patientId": 1,
    "doctorId": 101,
    "appointmentDate": "2026-04-15",
    "appointmentTime": "10:30:00",
    "status": "CONFIRMED",
    "createdAt": "2026-04-02T00:42:42"
}
```

## Saga Pattern Implementation

The `bookAppointment` operation follows a 4-step Saga:
```
Step 1: Verify patient exists (REST call → Patient Service)
Step 2: Save appointment with status = PENDING
Step 3: Publish "BOOKED" event to Kafka topic: appointment-events
Step 4: Update appointment status = CONFIRMED

If any step fails → Compensating Transaction:
    Update appointment status = FAILED
```

## Kafka Events

**Topic:** `appointment-events`

**Event payload:**
```json
{
    "appointmentId": 1,
    "patientId": 1,
    "appointmentDate": "2026-04-15",
    "appointmentTime": "10:30:00",
    "eventType": "BOOKED"
}
```

**Event types:** `BOOKED`, `CANCELLED`

## Running Locally

### Prerequisites
- Java 17
- Maven 3.9+
- MySQL 8.0 (via Docker)
- Apache Kafka (via Docker)
- Patient Service running on port 8081

### Start MySQL
```bash
docker run --name appointment-db \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=appointmentdb \
  -p 3307:3306 -d mysql:8.0
```

### Start Kafka
```bash
docker run -d --name kafka -p 9092:9092 apache/kafka:3.7.0
```

### Run the service
```bash
mvn clean package -DskipTests
java -jar target/appointment-service-0.0.1-SNAPSHOT.jar
```

## Running via Docker
```bash
docker build -t appointment-service:1.0 .
docker run -p 8082:8082 appointment-service:1.0
```

## Appointment Status Flow
```
PENDING → CONFIRMED (happy path)
PENDING → FAILED    (saga compensation)
CONFIRMED → CANCELLED (cancellation)
```

## Project Structure
```
src/main/java/com/healthcare/appointment/
├── Appointment.java                  # Entity model
├── AppointmentStatus.java            # Enum: PENDING, CONFIRMED, FAILED, CANCELLED
├── AppointmentBookedEvent.java       # Kafka message payload
├── AppointmentRepository.java        # JPA repository
├── AppointmentService.java           # Saga business logic
├── AppointmentController.java        # REST endpoints
├── PatientServiceClient.java         # REST client → Patient Service
├── AppointmentEventPublisher.java    # Kafka producer
├── KafkaProducerConfig.java          # Kafka configuration
└── AppointmentServiceApplication.java
```

## Group Members
| Name | ID |
|------|----|
| Member 1 | ID |
| Member 2 | ID |
| Member 3 | ID |
| Member 4 | ID |
