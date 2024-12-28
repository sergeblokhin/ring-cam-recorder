package com.ring_cam_recorder.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

@SpringBootTest
@ContextConfiguration(classes={PurgeService.class})
class PurgeServiceTest {
    @Autowired
    PurgeService purgeSvc;
    String fileKeep = "test1_%s.avi".formatted(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")));
    String fileDelete = "test1_%s.avi".formatted(LocalDateTime.now().minusDays(2).format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")));


    @Test
    void purge() {
        purgeSvc.purge();
        assert(Files.exists(Paths.get("%s%s".formatted(purgeSvc.getRootDir(),fileKeep))));
    }

    @BeforeEach
    void setUp() {
        try{
            var file = new File("%s/%s".formatted(purgeSvc.getRootDir(),fileDelete ));
            file.createNewFile();
            file = new File("%s/%s".formatted(purgeSvc.getRootDir(),fileKeep ));
            file.createNewFile();

        }catch (Exception e){

        }

    }
    @AfterEach
    void tearDown() {
        try (Stream<Path> paths = Files.walk(Paths.get(purgeSvc.getRootDir()))) {
            paths.filter(Files::isWritable).filter(Files::isRegularFile).forEach(
                    p -> {
                        var file = p.toFile();
                        file.delete();
                    }
            );
        }catch (Exception e) {}
    }
}