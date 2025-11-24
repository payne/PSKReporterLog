package com.pskreporter.log.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * JPA Entity representing a reception report from PSKReporter.
 * This stores information about a radio signal reception including
 * transmitter callsign, receiver callsign, frequency, SNR, and location data.
 */
@Entity
@Table(name = "reception_reports", indexes = {
    @Index(name = "idx_tx_callsign", columnList = "txCallsign"),
    @Index(name = "idx_rx_callsign", columnList = "rxCallsign"),
    @Index(name = "idx_timestamp", columnList = "timestamp")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceptionReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Transmitter callsign
     */
    @Column(nullable = false, length = 20)
    private String txCallsign;

    /**
     * Receiver callsign
     */
    @Column(nullable = false, length = 20)
    private String rxCallsign;

    /**
     * Frequency in Hz
     */
    @Column(nullable = false)
    private Long frequency;

    /**
     * Signal-to-Noise Ratio in dB
     */
    private Integer snr;

    /**
     * Operating mode (e.g., FT8, CW, SSB)
     */
    @Column(length = 20)
    private String mode;

    /**
     * Transmitter latitude
     */
    private Double txLatitude;

    /**
     * Transmitter longitude
     */
    private Double txLongitude;

    /**
     * Receiver latitude
     */
    private Double rxLatitude;

    /**
     * Receiver longitude
     */
    private Double rxLongitude;

    /**
     * Distance in kilometers
     */
    private Integer distance;

    /**
     * Timestamp of the report
     */
    @Column(nullable = false)
    private LocalDateTime timestamp;

    /**
     * Flag indicating if an alert was sent for this report
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean alertSent = false;
}
