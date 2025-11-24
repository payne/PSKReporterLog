package com.pskreporter.log.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data model for a PSKReporter reception record.
 * This is a simplified representation of the data received from PSKReporter.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PSKReception {
    
    private String transmitterCallsign;
    private String receiverCallsign;
    private long frequency;
    private Integer snr;
    private String mode;
    private Double transmitterLatitude;
    private Double transmitterLongitude;
    private Double receiverLatitude;
    private Double receiverLongitude;
    private LocalDateTime timestamp;
    
    /**
     * Calculate distance between transmitter and receiver in kilometers
     */
    public Integer calculateDistance() {
        if (transmitterLatitude == null || transmitterLongitude == null ||
            receiverLatitude == null || receiverLongitude == null) {
            return null;
        }
        
        // Haversine formula
        double lat1Rad = Math.toRadians(transmitterLatitude);
        double lat2Rad = Math.toRadians(receiverLatitude);
        double deltaLat = Math.toRadians(receiverLatitude - transmitterLatitude);
        double deltaLon = Math.toRadians(receiverLongitude - transmitterLongitude);
        
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                   Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                   Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double earthRadius = 6371; // Earth radius in kilometers
        
        return (int) Math.round(earthRadius * c);
    }
}
