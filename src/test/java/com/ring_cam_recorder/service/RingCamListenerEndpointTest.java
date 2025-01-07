package com.ring_cam_recorder.service;

import com.ring_cam_recorder.config.ActiveMQConfig;
import com.ring_cam_recorder.utils.FFMpegRunner;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.util.ByteSequence;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.*;

@EnableAutoConfiguration
@SpringBootTest(classes=ActiveMQConfig.class)
class RingCamListenerEndpointTest {
    @Autowired
    ActiveMQConfig cfg;


    @Test
    void handleMessageIgnore() {
        var mockFF = Mockito.mock(FFMpegRunner.class);
        var endPoint = new RingCamListenerEndpoint("test_topic", cfg, mockFF);
        var msg = new ActiveMQBytesMessage();
        msg.setContent(new ByteSequence("Random".getBytes()));
        endPoint.handleMessage(msg);
        verify(mockFF, times(0)).runFFmpegCommand(eq("cam_test"),any());
    }
    @Test
    void handleMessageOn(){
        var mockFF = Mockito.mock(FFMpegRunner.class);
        var endPoint = new RingCamListenerEndpoint("test_topic", cfg, mockFF);
        var msg = new ActiveMQBytesMessage();
        msg.setContent(new ByteSequence("ON".getBytes()));
        endPoint.handleMessage(msg);
        verify(mockFF, times(1)).runFFmpegCommand(eq("cam_test"),any());
    }
}