package com.ring_cam_recorder.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@SpringBootTest
class FFMpegRunnerTest {

    @Autowired
    FFMpegRunner mpegfunner;

    @AfterEach
    void tearDown() {
        try (Stream<Path> paths = Files.walk(Paths.get(mpegfunner.getFolder()))) {
            paths.filter(Files::isWritable).filter(Files::isRegularFile).forEach(
                    p -> {
                        p.toFile().delete();
                    }
            );
        }catch (Exception e) {}
    }

    @Test
    void runFFmpegCommand() {
        var test_avi_path = getClass().getClassLoader().getResource("test.avi").getPath();
        assert (Files.exists(Paths.get(test_avi_path)));
        assert (mpegfunner.runFFmpegCommand("cam1", test_avi_path));
    }
}