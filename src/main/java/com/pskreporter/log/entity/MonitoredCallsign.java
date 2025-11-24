package com.pskreporter.log.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * JPA Entity representing a monitored ham radio callsign.
 * This entity stores callsigns that should be monitored for specific conditions.
 */
@Entity
@Table(name = "monitored_callsigns")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonitoredCallsign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The callsign to monitor
     */
    @Column(nullable = false, unique = true, length = 20)
    private String callsign;

    /**
     * Enable/disable monitoring for this callsign
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    /**
     * When this callsign was added to monitoring
     */
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Optional custom SNR threshold for this specific callsign
     */
    private Integer customSnrThreshold;

    /**
     * Optional custom distance threshold for this specific callsign
     */
    private Integer customDistanceThreshold;
}
