package com.ring_cam_recorder.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.NoOpTaskScheduler;

@Configuration
public class TestConfig {
    // This will overwrite scheduler to NOOP so we can call the @Scheduled method manually
    @Bean
    public TaskScheduler taskScheduler() {
        return new NoOpTaskScheduler();
    }
}