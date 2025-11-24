package com.pskreporter.log;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main Spring Boot application class for PSKReporter Log.
 * This application monitors PSKReporter.info streaming data and sends email alerts
 * when specific conditions are met for monitored ham radio callsigns.
 */
@SpringBootApplication
@EnableScheduling
@EnableAsync
public class PSKReporterLogApplication {

    public static void main(String[] args) {
        SpringApplication.run(PSKReporterLogApplication.class, args);
    }
}
