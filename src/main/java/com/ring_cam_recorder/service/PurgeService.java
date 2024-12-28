package com.ring_cam_recorder.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

@Service
@EnableScheduling
public class PurgeService {
    final static Logger logger = LoggerFactory.getLogger(PurgeService.class);
    @Value("${recording.folder}")
    private String rootDir;
    @Value("${recording.retain}")
    private int days_retain;

    @Scheduled(fixedDelayString = "${recording.purge-interval}")
    public void purge() {
        purge_files();
    }

    @Autowired
    public PurgeService() {

    }

    public PurgeService(String rootDir, int days_retain) {
        this.rootDir = rootDir;
        this.days_retain = days_retain;
    }

    public void purge_files() {
        try (Stream<Path> paths = Files.walk(Paths.get(rootDir))) {
            paths.filter(Files::isReadable).filter(Files::isWritable).filter(Files::isRegularFile).forEach(
                    p -> {
                        var file = p.toFile();
                        if (file.getName().contains("_") && file.getName().contains(".avi")) {
                            var dtCreated = LocalDateTime.parse(file.getName().split("_")[1].replaceAll(".avi", ""),
                                    DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
                            if (dtCreated.isBefore(LocalDateTime.now().minusDays(days_retain))) {
                                if (!file.delete()) {
                                    logger.error("Could not delete file {}", file.getName());
                                }
                            }

                        }
                    });
        } catch (IOException e) {
            logger.error("Error puring", e);
            throw new RuntimeException(e);
        }
    }
}