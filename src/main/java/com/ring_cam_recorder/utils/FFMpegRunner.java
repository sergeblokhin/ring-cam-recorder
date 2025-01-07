package com.ring_cam_recorder.utils;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Data
@Component
public class FFMpegRunner {
    private static final Logger logger = LoggerFactory.getLogger(FFMpegRunner.class);

    @Value("${recording.timeout-seconds}")
    private int timeoutSeconds;
    @Value("${recording.folder}")
    private String folder;
    @Value("${recording.cmd}")
    private String cmd;
    @Value("${recording.run-time}")
    private int runTime;


    public boolean runFFmpegCommand(String cam, String ringURL) {
        try {
            var output = folder + (folder.endsWith("/") ? "" : "/") + cam;
            if (!Files.exists(Paths.get(output))) {
                new File(output).mkdirs();
            }

            output += String.format("/%s_%s.avi", cam, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")));
            var command_args = String.format(cmd, runTime, ringURL, output).split(" ");
            ProcessBuilder processBuilder = new ProcessBuilder(command_args);
            logger.trace("Execute command: {}", processBuilder.command());
            Process process = processBuilder.start();

            if (!process.waitFor(timeoutSeconds, TimeUnit.SECONDS)) {
                printStdError(process);
                process.destroyForcibly();
                logger.error("FFmpeg command timed out after {} seconds", timeoutSeconds);
                return false;
            }

            int exitCode = process.exitValue();
            if (exitCode == 0) {
                logger.info("FFmpeg command executed successfully");
                return true;
            } else {
                printStdError(process);
                return false;
            }
        } catch (Exception e) {
            logger.error("Error executing FFmpeg command", e);
            return false;
        }
    }

    private void printStdError(Process proc) {
        try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(proc.getErrorStream()))) {
            String errorLine;
            while ((errorLine = errorReader.readLine()) != null) {
                logger.error(errorLine);
            }
        } catch (Exception e) {
            logger.error("Error reading from process stderror {}", e.getMessage(), e);
        }
    }
}