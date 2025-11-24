package com.pskreporter.log.service;

import com.pskreporter.log.config.PSKReporterConfig;
import com.pskreporter.log.entity.MonitoredCallsign;
import com.pskreporter.log.repository.MonitoredCallsignRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service to manage monitored callsigns.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MonitoredCallsignService {

    private final MonitoredCallsignRepository repository;
    private final PSKReporterConfig config;

    /**
     * Initialize monitored callsigns from configuration on startup.
     */
    @PostConstruct
    @Transactional
    public void initializeMonitoredCallsigns() {
        List<String> callsigns = config.getMonitoredCallsigns();
        if (callsigns == null || callsigns.isEmpty()) {
            log.warn("No monitored callsigns configured");
            return;
        }

        for (String callsign : callsigns) {
            if (!repository.existsByCallsignAndActiveTrue(callsign)) {
                MonitoredCallsign monitored = MonitoredCallsign.builder()
                    .callsign(callsign.trim().toUpperCase())
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .build();
                repository.save(monitored);
                log.info("Added monitored callsign: {}", callsign);
            } else {
                log.info("Callsign {} already monitored", callsign);
            }
        }

        log.info("Initialized {} monitored callsigns", callsigns.size());
    }

    /**
     * Add a new callsign to monitor.
     */
    @Transactional
    public MonitoredCallsign addMonitoredCallsign(String callsign) {
        String normalizedCallsign = callsign.trim().toUpperCase();
        
        return repository.findByCallsign(normalizedCallsign)
            .map(existing -> {
                if (!existing.getActive()) {
                    existing.setActive(true);
                    repository.save(existing);
                    log.info("Re-activated monitored callsign: {}", normalizedCallsign);
                }
                return existing;
            })
            .orElseGet(() -> {
                MonitoredCallsign monitored = MonitoredCallsign.builder()
                    .callsign(normalizedCallsign)
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .build();
                repository.save(monitored);
                log.info("Added new monitored callsign: {}", normalizedCallsign);
                return monitored;
            });
    }

    /**
     * Remove a callsign from monitoring.
     */
    @Transactional
    public void removeMonitoredCallsign(String callsign) {
        String normalizedCallsign = callsign.trim().toUpperCase();
        repository.findByCallsign(normalizedCallsign)
            .ifPresent(monitored -> {
                monitored.setActive(false);
                repository.save(monitored);
                log.info("Deactivated monitored callsign: {}", normalizedCallsign);
            });
    }

    /**
     * Get all active monitored callsigns.
     */
    public List<MonitoredCallsign> getActiveCallsigns() {
        return repository.findByActiveTrue();
    }
}
