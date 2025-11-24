package com.pskreporter.log.repository;

import com.pskreporter.log.entity.MonitoredCallsign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA Repository for MonitoredCallsign entities.
 */
@Repository
public interface MonitoredCallsignRepository extends JpaRepository<MonitoredCallsign, Long> {

    /**
     * Find a monitored callsign by its callsign string
     */
    Optional<MonitoredCallsign> findByCallsign(String callsign);

    /**
     * Find all active monitored callsigns
     */
    List<MonitoredCallsign> findByActiveTrue();

    /**
     * Check if a callsign is being monitored
     */
    boolean existsByCallsignAndActiveTrue(String callsign);
}
