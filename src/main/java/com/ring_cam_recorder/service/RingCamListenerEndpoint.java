package com.ring_cam_recorder.service;

import com.ring_cam_recorder.config.ActiveMQConfig;
import com.ring_cam_recorder.utils.FFMpegRunner;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.config.JmsListenerEndpoint;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.MessageListenerContainer;

@Data
public class RingCamListenerEndpoint implements JmsListenerEndpoint {
    private static final Logger logger = LoggerFactory.getLogger(RingCamListenerEndpoint.class);
    private final String topicName;

    private final ActiveMQConfig config;
    private final FFMpegRunner ffmpegRunner;

    public RingCamListenerEndpoint(String topic, ActiveMQConfig config, FFMpegRunner ffmpegRunner) {
        this.topicName = topic;
        this.config = config;
        this.ffmpegRunner = ffmpegRunner;
    }


    @Override
    public String getId() {
        return "dynamic-listener-endpoint-%s".formatted(topicName);
    }

    @Override
    public void setupListenerContainer(MessageListenerContainer listenerContainer) {

        listenerContainer.setupMessageListener((MessageListener) this::handleMessage);
        if (listenerContainer instanceof DefaultMessageListenerContainer ls) {
            ls.setDestinationName(topicName);
        }
    }

    public void handleMessage(Message message) {
        logger.trace("got a message {}", message == null);
        if (message != null) {
            logger.info("Message received for topic %s in thread %d".formatted(this.topicName, Thread.currentThread().getId()));
            try {
                var s = new String(message.getBody(byte[].class));
                if (s.equals("ON")) {
                    logger.trace("Motion is ON, start recording");
                    runFFmpeg(this.config.findByTopic(this.topicName).getCam());
                    logger.trace("Stop recording");
                }
            } catch (Exception e){
                logger.error("Message {} error: {}", message, e.getMessage());
            }
        }
    }

    protected void runFFmpeg(String cam) {
        logger.info("run recording for cam {}", cam);
        ffmpegRunner.runFFmpegCommand(cam, config.findURL(cam));
    }
}
