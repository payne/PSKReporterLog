package com.pskreporter.log.service;

import com.pskreporter.log.config.PSKReporterConfig;
import com.pskreporter.log.entity.ReceptionReport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service to send email alerts when conditions are met.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AlertService {

    private final JavaMailSender mailSender;
    private final PSKReporterConfig config;

    /**
     * Check if a reception report meets alert conditions and send an alert if needed.
     */
    @Transactional
    public void checkAndSendAlert(ReceptionReport report) {
        if (!config.getAlert().isEnabled()) {
            log.debug("Alerts are disabled");
            return;
        }

        if (report.getAlertSent()) {
            log.debug("Alert already sent for report {}", report.getId());
            return;
        }

        // Condition X: Check if SNR or distance exceeds thresholds
        boolean alertConditionMet = false;
        StringBuilder reason = new StringBuilder();

        if (report.getSnr() != null && report.getSnr() >= config.getAlert().getSnrThreshold()) {
            alertConditionMet = true;
            reason.append(String.format("SNR %d dB exceeds threshold of %d dB. ", 
                                       report.getSnr(), config.getAlert().getSnrThreshold()));
        }

        if (report.getDistance() != null && report.getDistance() >= config.getAlert().getDistanceThreshold()) {
            alertConditionMet = true;
            reason.append(String.format("Distance %d km exceeds threshold of %d km. ", 
                                       report.getDistance(), config.getAlert().getDistanceThreshold()));
        }

        if (alertConditionMet) {
            sendAlert(report, reason.toString());
            report.setAlertSent(true);
        }
    }

    /**
     * Send an email alert for a reception report.
     */
    private void sendAlert(ReceptionReport report, String reason) {
        try {
            String subject = String.format("PSKReporter Alert: %s", report.getTxCallsign());
            String body = buildAlertMessage(report, reason);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(config.getAlert().getRecipients().toArray(new String[0]));
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            log.info("Alert sent for callsign {} to {}", 
                     report.getTxCallsign(), config.getAlert().getRecipients());

        } catch (Exception e) {
            log.error("Failed to send alert email", e);
        }
    }

    /**
     * Build the alert message body.
     */
    private String buildAlertMessage(ReceptionReport report, String reason) {
        return String.format("""
            PSKReporter Alert
            
            Alert Condition Met: %s
            
            Reception Details:
            - Transmitter: %s
            - Receiver: %s
            - Frequency: %,d Hz (%.3f MHz)
            - Mode: %s
            - SNR: %d dB
            - Distance: %d km
            - Timestamp: %s
            
            Transmitter Location: %s
            Receiver Location: %s
            
            This is an automated alert from PSKReporter Log.
            """,
            reason,
            report.getTxCallsign(),
            report.getRxCallsign(),
            report.getFrequency(),
            report.getFrequency() / 1_000_000.0,
            report.getMode() != null ? report.getMode() : "Unknown",
            report.getSnr() != null ? report.getSnr() : 0,
            report.getDistance() != null ? report.getDistance() : 0,
            report.getTimestamp(),
            formatLocation(report.getTxLatitude(), report.getTxLongitude()),
            formatLocation(report.getRxLatitude(), report.getRxLongitude())
        );
    }

    /**
     * Format latitude/longitude for display.
     */
    private String formatLocation(Double lat, Double lon) {
        if (lat == null || lon == null) {
            return "Unknown";
        }
        return String.format("%.4f°, %.4f°", lat, lon);
    }
}
