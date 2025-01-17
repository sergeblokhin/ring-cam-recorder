package com.ring_cam_recorder.service;

import com.ring_cam_recorder.config.ActiveMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

@EnableAsync
@Service
public class MotionPollerService {
    private static final Logger logger = LoggerFactory.getLogger(MotionPollerService.class);

    @Autowired
    private ActiveMQConfig mqConfig;

    @Autowired
    private RingCamMessageListener listener;


    @EventListener(ApplicationReadyEvent.class)
    public void run() {
        try {

            for (var topic_conf : this.mqConfig.getTopic_config()) {
                listener.startListening(topic_conf.getTopic());
            }
        } catch (Exception e) {
            logger.error("Error polling topic", e);
        }
    }

}