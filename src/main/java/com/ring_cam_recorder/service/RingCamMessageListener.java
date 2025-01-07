package com.ring_cam_recorder.service;


import com.ring_cam_recorder.config.ActiveMQConfig;
import com.ring_cam_recorder.utils.FFMpegRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.stereotype.Component;

@Component
public class RingCamMessageListener {
    private static final Logger logger = LoggerFactory.getLogger(RingCamMessageListener.class);

    @Autowired
    private JmsListenerEndpointRegistry endpointRegistry;

    @Autowired
    DefaultJmsListenerContainerFactory containerFactory;

    @Autowired
    private FFMpegRunner ffmpegRunner;

    @Autowired
    ActiveMQConfig config;


    public void startListening(String topicName) {
        logger.trace("start listener for topic: {}", topicName);
        var endpoint = new RingCamListenerEndpoint(topicName, config, ffmpegRunner);
        endpointRegistry.registerListenerContainer(endpoint, containerFactory);
        endpointRegistry.getListenerContainer(endpoint.getId()).start();
    }
}
