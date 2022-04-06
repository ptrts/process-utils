package we.processUtils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;

@Slf4j
public class ProcessRunner {

    @SneakyThrows
    public static void runProcess(File directory, String... command) {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(directory);
        Process process = processBuilder.start();

        try (
                BufferedReader error = process.errorReader();
                BufferedReader input = process.inputReader();
        ) {
            int exitStatus = process.waitFor();
            if (exitStatus != 0) {
                input.lines().forEachOrdered(log::info);
                error.lines().forEachOrdered(log::error);
                throw new RuntimeException(command[0] + " exited with status " + exitStatus);
            }
        }
    }
}
