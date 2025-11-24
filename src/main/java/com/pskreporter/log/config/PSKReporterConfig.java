package com.pskreporter.log.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration properties for PSKReporter monitoring.
 */
@Configuration
@ConfigurationProperties(prefix = "pskreporter")
@Data
public class PSKReporterConfig {

    private Server server = new Server();
    private List<String> monitoredCallsigns;
    private Alert alert = new Alert();

    @Data
    public static class Server {
        private String host = "report.pskreporter.info";
        private int port = 4739;
    }

    @Data
    public static class Alert {
        private List<String> recipients;
        private int snrThreshold = 10;
        private int distanceThreshold = 1000;
        private boolean enabled = true;
    }
}
