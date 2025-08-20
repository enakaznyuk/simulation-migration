package com.example.migration.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class ScheduledComponent {

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Autowired
    private final ScheduledRequest scheduledRequest;

    @PostConstruct
    public void start() throws JsonProcessingException {

        scheduledRequest.migrationLogic();

        long initialDelay = computeInitialDelay();
        scheduler.schedule(() -> {
            try {
                scheduledRequest.migrationLogic();
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            scheduleRecurring();
        }, initialDelay, TimeUnit.SECONDS);
    }

    private long computeInitialDelay() {
        LocalTime now = LocalTime.now();
        int minutes = now.getMinute();

        int targetMinutes = 15;
        int delayMinutes;

        if (minutes < targetMinutes) {
            delayMinutes = targetMinutes - minutes;
        } else {
            delayMinutes = 60 - minutes + targetMinutes;
        }

        return delayMinutes * 60L;
    }

    private void scheduleRecurring() {

        long intervalSeconds = 2 * 60 * 60;

        scheduler.scheduleAtFixedRate(() -> {
            try {
                scheduledRequest.migrationLogic();
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }, intervalSeconds, intervalSeconds, TimeUnit.SECONDS);
    }
}
