package com.ring_cam_recorder.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;


class PurgeServiceTest {
    final String rootDir = "./home/data/video/cams/";
    PurgeService purgeSvc = new PurgeService(rootDir, 1);
    String fileKeep = "test1_%s.avi".formatted(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")));
    String fileDelete = "test1_%s.avi".formatted(LocalDateTime.now().minusDays(2).format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")));


    @Test
    void purge() {
        purgeSvc.purge_files();
        assert(Files.exists(Paths.get("%s%s".formatted(rootDir,fileKeep))));
    }

    @BeforeEach
    void setUp() {
        try{
            var file = new File("%s/%s".formatted(rootDir,fileDelete ));
            file.createNewFile();
            file = new File("%s/%s".formatted(rootDir,fileKeep ));
            file.createNewFile();

        }catch (Exception e){

        }

    }
    @AfterEach
    void tearDown() {
        try (Stream<Path> paths = Files.walk(Paths.get(rootDir))) {
            paths.filter(Files::isWritable).filter(Files::isRegularFile).forEach(
                    p -> {
                        var file = p.toFile();
                        file.delete();
                    }
            );
        }catch (Exception e) {}
    }
}