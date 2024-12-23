package com.ring_cam_recorder.config;

import lombok.Data;

@Data
public class RecordingConfig  {
    private String timeout_secods;
    private String folder;
}
