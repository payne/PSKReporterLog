package com.pskreporter.log.repository;

import com.pskreporter.log.entity.ReceptionReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * JPA Repository for ReceptionReport entities.
 */
@Repository
public interface ReceptionReportRepository extends JpaRepository<ReceptionReport, Long> {

    /**
     * Find all reports for a specific transmitter callsign
     */
    List<ReceptionReport> findByTxCallsign(String txCallsign);

    /**
     * Find all reports for a specific receiver callsign
     */
    List<ReceptionReport> findByRxCallsign(String rxCallsign);

    /**
     * Find recent reports for a specific transmitter callsign
     */
    List<ReceptionReport> findByTxCallsignAndTimestampAfter(String txCallsign, LocalDateTime since);

    /**
     * Find reports that meet alert conditions but haven't had alerts sent
     */
    @Query("SELECT r FROM ReceptionReport r WHERE r.txCallsign IN :callsigns " +
           "AND r.alertSent = false " +
           "AND (r.snr >= :snrThreshold OR r.distance >= :distanceThreshold)")
    List<ReceptionReport> findReportsForAlert(
        @Param("callsigns") List<String> callsigns,
        @Param("snrThreshold") int snrThreshold,
        @Param("distanceThreshold") int distanceThreshold
    );
}
