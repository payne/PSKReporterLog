package com.pskreporter.log.service;

import com.pskreporter.log.entity.MonitoredCallsign;
import com.pskreporter.log.model.PSKReception;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * Demonstration service that generates synthetic PSKReporter data for testing.
 * In production, this would be replaced by actual data from PSKReporter.info stream.
 * 
 * This service simulates receiving data and can be disabled by setting
 * pskreporter.demo.enabled=false in application.yml
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DemoDataGenerator {

    private final ReceptionProcessor receptionProcessor;
    private final MonitoredCallsignService callsignService;
    private final Random random = new Random();

    // Common ham radio frequencies (in Hz)
    private final long[] frequencies = {
        3_573_000L,   // 80m FT8
        7_074_000L,   // 40m FT8
        10_136_000L,  // 30m FT8
        14_074_000L,  // 20m FT8
        18_100_000L,  // 17m FT8
        21_074_000L,  // 15m FT8
        24_915_000L,  // 12m FT8
        28_074_000L   // 10m FT8
    };

    private final String[] modes = {"FT8", "FT4", "CW", "SSB", "PSK31"};
    
    private final String[] receiverCallsigns = {
        "K2ABC", "W3XYZ", "N4QRS", "KD5TUV", "VE6WXY"
    };

    /**
     * Generate synthetic reception data every 30 seconds.
     * This simulates receiving data from PSKReporter stream.
     */
    @Scheduled(fixedDelay = 30000, initialDelay = 10000)
    public void generateDemoData() {
        try {
            List<String> monitoredCallsigns = callsignService.getActiveCallsigns()
                .stream()
                .map(MonitoredCallsign::getCallsign)
                .toList();

            if (monitoredCallsigns.isEmpty()) {
                log.debug("No monitored callsigns, skipping demo data generation");
                return;
            }

            // Generate 1-3 random receptions
            int count = random.nextInt(3) + 1;
            for (int i = 0; i < count; i++) {
                generateRandomReception(monitoredCallsigns);
            }

        } catch (Exception e) {
            log.error("Error generating demo data", e);
        }
    }

    private void generateRandomReception(List<String> monitoredCallsigns) {
        String txCallsign = monitoredCallsigns.get(random.nextInt(monitoredCallsigns.size()));
        String rxCallsign = receiverCallsigns[random.nextInt(receiverCallsigns.length)];
        
        // Random location for transmitter (somewhere in North America)
        double txLat = 35.0 + random.nextDouble() * 15.0; // 35-50 N
        double txLon = -120.0 + random.nextDouble() * 40.0; // -120 to -80 W
        
        // Random location for receiver
        double rxLat = 35.0 + random.nextDouble() * 15.0;
        double rxLon = -120.0 + random.nextDouble() * 40.0;
        
        PSKReception reception = PSKReception.builder()
            .transmitterCallsign(txCallsign)
            .receiverCallsign(rxCallsign)
            .frequency(frequencies[random.nextInt(frequencies.length)])
            .snr(-10 + random.nextInt(35)) // -10 to +25 dB
            .mode(modes[random.nextInt(modes.length)])
            .transmitterLatitude(txLat)
            .transmitterLongitude(txLon)
            .receiverLatitude(rxLat)
            .receiverLongitude(rxLon)
            .timestamp(LocalDateTime.now())
            .build();

        log.info("Generated demo reception: {} -> {}, SNR: {} dB", 
                 txCallsign, rxCallsign, reception.getSnr());
        
        receptionProcessor.processReception(reception);
    }
}
