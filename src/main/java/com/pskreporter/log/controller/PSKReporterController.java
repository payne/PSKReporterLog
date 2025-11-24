package com.pskreporter.log.controller;

import com.pskreporter.log.entity.MonitoredCallsign;
import com.pskreporter.log.entity.ReceptionReport;
import com.pskreporter.log.repository.ReceptionReportRepository;
import com.pskreporter.log.service.MonitoredCallsignService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST Controller for managing PSKReporter monitoring.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PSKReporterController {

    private final MonitoredCallsignService callsignService;
    private final ReceptionReportRepository receptionReportRepository;

    /**
     * Get all monitored callsigns.
     */
    @GetMapping("/callsigns")
    public List<MonitoredCallsign> getMonitoredCallsigns() {
        return callsignService.getActiveCallsigns();
    }

    /**
     * Add a new callsign to monitor.
     */
    @PostMapping("/callsigns")
    public ResponseEntity<MonitoredCallsign> addCallsign(@RequestParam String callsign) {
        MonitoredCallsign monitored = callsignService.addMonitoredCallsign(callsign);
        return ResponseEntity.ok(monitored);
    }

    /**
     * Remove a callsign from monitoring.
     */
    @DeleteMapping("/callsigns/{callsign}")
    public ResponseEntity<Void> removeCallsign(@PathVariable String callsign) {
        callsignService.removeMonitoredCallsign(callsign);
        return ResponseEntity.ok().build();
    }

    /**
     * Get recent reception reports.
     */
    @GetMapping("/reports")
    public List<ReceptionReport> getRecentReports(
            @RequestParam(required = false) String callsign,
            @RequestParam(defaultValue = "100") int limit) {
        
        if (callsign != null) {
            return receptionReportRepository.findByTxCallsignAndTimestampAfter(
                callsign, 
                LocalDateTime.now().minusDays(1)
            );
        }
        
        return receptionReportRepository.findAll(PageRequest.of(0, limit)).getContent();
    }

    /**
     * Get a specific reception report by ID.
     */
    @GetMapping("/reports/{id}")
    public ResponseEntity<ReceptionReport> getReport(@PathVariable Long id) {
        return receptionReportRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Health check endpoint.
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("PSKReporter Log is running");
    }
}
