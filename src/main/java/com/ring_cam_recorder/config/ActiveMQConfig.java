package com.ring_cam_recorder.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Data
@Configuration
@ConfigurationProperties(prefix = "activemq")
public class ActiveMQConfig {
    private String brokerUrl;
    private TopicConfig[] topic_config;
    @Data
    public static class TopicConfig {
        String topic;
        String cam;
        String url;
    }

    public String findURL(String cam) {
        var res = Arrays.stream(topic_config).filter(conf -> {
            return conf.cam.equals(cam);
        }).findFirst();
        return res.map(topicConfig -> topicConfig.url).orElse(null);

    }
}


