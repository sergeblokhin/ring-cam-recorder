package com.ring_cam_recorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableJms
@EnableAsync
@SpringBootApplication
public class Main {
    static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.trace("Application start");
        var context = SpringApplication.run(Main.class, args);
        logger.trace("Application: {}} stop", context.getApplicationName());

    }

}