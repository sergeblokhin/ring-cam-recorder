package com.ring_cam_recorder.utils;

import com.ring_cam_recorder.config.ActiveMQConfig;
import jakarta.jms.*;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Data
@Component
public class ActiveMQConnection {
    private static final Logger logger = LoggerFactory.getLogger(ActiveMQConnection.class);
    @Autowired
    private final ActiveMQConfig config;

    @Autowired
    private final FFMpegRunner ffmpegRunner;


    @Async
    public void run(String topic, String cam) {
        logger.trace("start thread: {}", Thread.currentThread().getId());

        try (var consumer = createConsumer(topic)) {
            while (true) {
                var msg = consumer.receive();
                if (msg != null) {
                    logger.info("Message received from topic %s in thread %d".formatted(topic, Thread.currentThread().getId()));
                    var s = new String(msg.getBody(byte[].class));
                    if (s.equals("ON")) {
                        logger.trace("Motion is ON, start recording");
                        runFFmpeg(cam);
                        logger.trace("Stop recording");
                    }
                } else {
                    logger.info("Bad message , exit listener");
                    break;
                }
            }
        } catch (Exception e) {
            logger.error("Error", e);
        }
        logger.trace("stop thread: {}", Thread.currentThread().getId());
    }

    void runFFmpeg(String cam) {
        logger.info("run recording for cam {}", cam);
        ffmpegRunner.runFFmpegCommand(cam, config.findURL(cam));
    }

    public MessageConsumer createConsumer(String topic) throws JMSException {
        var factory = new ActiveMQConnectionFactory(config.getBrokerUrl());
        var connection = factory.createConnection(); // is this leaking connection?
        connection.start();
        var session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destinationTopic = session.createTopic(topic);

        // Create a MessageConsumer from the Session to the Topic or Queue
        return session.createConsumer(destinationTopic);
    }

}
