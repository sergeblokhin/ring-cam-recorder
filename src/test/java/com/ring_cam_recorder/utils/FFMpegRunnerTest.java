package com.ring_cam_recorder.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootTest
class FFMpegRunnerTest {

    @Autowired
    FFMpegRunner mpegfunner;

    @BeforeEach
    void setUp() {
    }

    @Test
    void runFFmpegCommand() {
        var test_avi_path = "/Users/sergeblokhin/dev/ring-cam-recorder/home/test.avi";
        assert (Files.exists(Paths.get(test_avi_path)));
        assert (mpegfunner.runFFmpegCommand("cam1", test_avi_path));
    }
}