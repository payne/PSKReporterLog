# PSKReporter Log

A Spring Boot application that monitors PSKReporter.info streaming data for specific ham radio callsigns and sends email alerts when certain conditions are met.

## Features

- **Real-time Monitoring**: Receives streaming data from PSKReporter.info (UDP port 4739)
- **Database Storage**: Uses JPA/Hibernate to persist reception reports to H2 (dev) or PostgreSQL (production)
- **Configurable Monitoring**: Monitor specific ham radio callsigns (A, B, C, etc.)
- **Automated Alerts**: Send email notifications when Condition X is met:
  - SNR (Signal-to-Noise Ratio) exceeds threshold
  - Distance exceeds threshold
- **RESTful API**: Manage monitored callsigns and view reports via REST endpoints
- **Lombok Integration**: Clean, concise code with Lombok annotations

## Technologies Used

- **Spring Boot 3.2.0**: Application framework
- **Spring Data JPA**: Database access and ORM
- **Lombok**: Reduce boilerplate code
- **H2 Database**: In-memory database for development
- **PostgreSQL**: Production database (optional)
- **Spring Mail**: Email notification service
- **Maven**: Build and dependency management

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- (Optional) PostgreSQL database for production use

## Configuration

The application can be configured via `src/main/resources/application.yml` or environment variables:

### Monitored Callsigns

Set the callsigns to monitor (comma-separated):
```yaml
pskreporter:
  monitored-callsigns: W1AW,K1TTT,N0CALL
```

Or via environment variable:
```bash
export MONITORED_CALLSIGNS="W1AW,K1TTT,N0CALL"
```

### Alert Configuration

Configure alert thresholds and recipients:
```yaml
pskreporter:
  alert:
    recipients: admin@example.com,operator@example.com
    snr-threshold: 10        # SNR in dB
    distance-threshold: 1000 # Distance in km
    enabled: true
```

Or via environment variables:
```bash
export ALERT_RECIPIENTS="admin@example.com"
export ALERT_SNR_THRESHOLD=10
export ALERT_DISTANCE_THRESHOLD=1000
export ALERT_ENABLED=true
```

### Email Configuration

Configure SMTP settings for email alerts:
```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: your-email@gmail.com
    password: your-app-password
```

Or via environment variables:
```bash
export MAIL_USERNAME="your-email@gmail.com"
export MAIL_PASSWORD="your-app-password"
```

### Database Configuration

**Development (H2):**
```yaml
spring:
  datasource:
    url: jdbc:h2:file:./data/pskreporter
    driver-class-name: org.h2.Driver
```

**Production (PostgreSQL):**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/pskreporter
    driver-class-name: org.postgresql.Driver
    username: pskreporter
    password: your-password
```

## Building the Application

```bash
./mvnw clean package
```

## Running the Application

### Using Maven:
```bash
./mvnw spring-boot:run
```

### Using Java:
```bash
java -jar target/pskreporter-log-0.0.1-SNAPSHOT.jar
```

### With Custom Configuration:
```bash
export MONITORED_CALLSIGNS="W1AW,K1TTT"
export ALERT_RECIPIENTS="admin@example.com"
export MAIL_USERNAME="your-email@gmail.com"
export MAIL_PASSWORD="your-app-password"
./mvnw spring-boot:run
```

## API Endpoints

Once running, the application exposes several REST endpoints:

### Health Check
```bash
curl http://localhost:8080/api/health
```

### Get Monitored Callsigns
```bash
curl http://localhost:8080/api/callsigns
```

### Add Monitored Callsign
```bash
curl -X POST "http://localhost:8080/api/callsigns?callsign=W1AW"
```

### Remove Monitored Callsign
```bash
curl -X DELETE http://localhost:8080/api/callsigns/W1AW
```

### Get Recent Reports
```bash
# All reports (last 100)
curl http://localhost:8080/api/reports

# Reports for specific callsign
curl "http://localhost:8080/api/reports?callsign=W1AW"

# Limit results
curl "http://localhost:8080/api/reports?limit=50"
```

### Get Specific Report
```bash
curl http://localhost:8080/api/reports/1
```

## Database Console

Access the H2 console (development only) at:
```
http://localhost:8080/h2-console
```

- JDBC URL: `jdbc:h2:file:./data/pskreporter`
- Username: `sa`
- Password: (leave blank)

## How It Works

1. **Data Reception**: The application listens for UDP packets from PSKReporter.info on port 4739
2. **Data Processing**: Incoming reception reports are parsed and filtered for monitored callsigns
3. **Storage**: Matching reports are stored in the database using JPA entities
4. **Alert Checking**: Each report is checked against alert conditions (SNR and distance thresholds)
5. **Email Alerts**: When conditions are met, automated emails are sent to configured recipients

## Alert Conditions

The application monitors for "Condition X", which triggers an alert when:
- **SNR Threshold**: The signal-to-noise ratio exceeds the configured threshold (default: 10 dB), OR
- **Distance Threshold**: The transmission distance exceeds the configured threshold (default: 1000 km)

## Demo Mode

The application includes a demonstration mode that generates synthetic PSKReporter data for testing. This runs automatically and creates reception reports every 30 seconds for monitored callsigns.

To disable demo mode in production, you would modify the `DemoDataGenerator` service or use the actual PSKReporter UDP stream parser.

## Project Structure

```
src/main/java/com/pskreporter/log/
├── PSKReporterLogApplication.java  # Main application class
├── config/
│   └── PSKReporterConfig.java      # Configuration properties
├── controller/
│   └── PSKReporterController.java  # REST API endpoints
├── entity/
│   ├── ReceptionReport.java        # JPA entity for reports
│   └── MonitoredCallsign.java      # JPA entity for callsigns
├── model/
│   └── PSKReception.java           # Data transfer object
├── repository/
│   ├── ReceptionReportRepository.java
│   └── MonitoredCallsignRepository.java
└── service/
    ├── PSKReporterClient.java      # UDP client for PSKReporter
    ├── ReceptionProcessor.java     # Process and save reports
    ├── AlertService.java            # Email alert service
    ├── MonitoredCallsignService.java
    └── DemoDataGenerator.java      # Demo data for testing
```

## Testing

Run the tests with:
```bash
./mvnw test
```

## License

This project is provided as-is for educational and amateur radio purposes.

## Notes

- The actual PSKReporter binary protocol parsing is complex and requires understanding their specific UDP packet format. The current implementation includes a framework for this but uses synthetic data for demonstration.
- For production use, you would need to implement the full PSKReporter packet parser or use their API.
- Remember to use app-specific passwords for Gmail or configure your SMTP server accordingly.
- Adjust alert thresholds based on your monitoring needs.