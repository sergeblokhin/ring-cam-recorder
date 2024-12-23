package com.ring_cam_recorder.service;

import com.ring_cam_recorder.config.ActiveMQConfig;
import com.ring_cam_recorder.config.AsyncConfig;
import com.ring_cam_recorder.utils.ActiveMQConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@EnableAsync
@Service
public class MotionPollerService {
    private static final Logger logger = LoggerFactory.getLogger(MotionPollerService.class);

    private final Map<String,String> camConfig = new HashMap<>();
    @Autowired
    private AsyncConfig aconfig;
    @Autowired
    private ActiveMQConfig mqConfig;

    @Autowired
    private ActiveMQConnection mqConnection;


    @EventListener(ApplicationReadyEvent.class)
    public void run() {
        try {

            for( var topic_conf : this.mqConfig.getTopic_config()){

                mqConnection.run(topic_conf.getTopic(), topic_conf.getCam());

            }
        } catch (Exception e) {
            logger.error("Error polling topic", e);
        }
    }

}