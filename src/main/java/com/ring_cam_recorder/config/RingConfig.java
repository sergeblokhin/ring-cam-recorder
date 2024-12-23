package com.ring_cam_recorder.config;

import lombok.Data;

import java.util.List;

@Data
public class RingConfig {
    private String accontid;
    private List<String> cams;
}
