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
public class ActiveMQConnection{
    private static final Logger logger = LoggerFactory.getLogger(ActiveMQConnection.class);
    @Autowired
    private final ActiveMQConfig config;

    @Autowired
    private final FFMpegRunner ffmpegRunner;


    @Async
    public void run(String topic, String cam){
        logger.info("start thread: " + Thread.currentThread().getId());
        var factory = new ActiveMQConnectionFactory(config.getBrokerUrl());
        //Connection connection = null;
        Session session = null;
        try (var connection = factory.createConnection()){

            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destinationTopic = session.createTopic(topic);

            // Create a MessageConsumer from the Session to the Topic or Queue
            MessageConsumer consumer = session.createConsumer(destinationTopic);
            while(true) {
                var msg = consumer.receive();
                if(msg != null){
                    logger.info("Message received from topic %s in thread %d".formatted(topic,Thread.currentThread().getId()));
                    var s = new String (msg.getBody(byte[].class));
                    if(s.equals("ON")){
                        logger.trace("ON");
                        consumer.close(); // this is to avoid reading messages during video recording.
                        runFFmpeg(cam);
                        consumer = session.createConsumer(destinationTopic);
                    }
                }else{
                    logger.info("Bad message , exit listener");
                    break;
                }
            }
            consumer.close();
            session.close();
        }catch(Exception e){
            logger.error("Error",e);
        }
    }
    void runFFmpeg(String cam) throws IOException{
        logger.info("run recording for cam " + cam);
        ffmpegRunner.runFFmpegCommand(cam, config.findURL(cam));
    }

}
