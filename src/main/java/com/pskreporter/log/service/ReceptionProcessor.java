package com.pskreporter.log.service;

import com.pskreporter.log.entity.ReceptionReport;
import com.pskreporter.log.model.PSKReception;
import com.pskreporter.log.repository.ReceptionReportRepository;
import com.pskreporter.log.repository.MonitoredCallsignRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service to process reception reports and save them to the database.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ReceptionProcessor {

    private final ReceptionReportRepository receptionReportRepository;
    private final MonitoredCallsignRepository monitoredCallsignRepository;
    private final AlertService alertService;

    /**
     * Process a PSKReporter reception and save it if the callsign is monitored.
     */
    @Transactional
    public void processReception(PSKReception reception) {
        try {
            String txCallsign = reception.getTransmitterCallsign();
            
            // Check if this callsign is being monitored
            if (!monitoredCallsignRepository.existsByCallsignAndActiveTrue(txCallsign)) {
                log.debug("Callsign {} not monitored, skipping", txCallsign);
                return;
            }

            // Calculate distance
            Integer distance = reception.calculateDistance();

            // Create and save reception report
            ReceptionReport report = ReceptionReport.builder()
                .txCallsign(reception.getTransmitterCallsign())
                .rxCallsign(reception.getReceiverCallsign())
                .frequency(reception.getFrequency())
                .snr(reception.getSnr())
                .mode(reception.getMode())
                .txLatitude(reception.getTransmitterLatitude())
                .txLongitude(reception.getTransmitterLongitude())
                .rxLatitude(reception.getReceiverLatitude())
                .rxLongitude(reception.getReceiverLongitude())
                .distance(distance)
                .timestamp(reception.getTimestamp())
                .alertSent(false)
                .build();

            report = receptionReportRepository.save(report);
            log.info("Saved reception report: {} -> {} on {} Hz, SNR: {} dB, Distance: {} km",
                     report.getTxCallsign(), report.getRxCallsign(), 
                     report.getFrequency(), report.getSnr(), report.getDistance());

            // Check if alert conditions are met
            alertService.checkAndSendAlert(report);

        } catch (Exception e) {
            log.error("Error processing reception", e);
        }
    }
}
