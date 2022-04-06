package we.processUtils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.util.stream.Collectors;

@Slf4j
public class ProcessRunner {

    @SneakyThrows
    public static String runProcess(File directory, String... command) {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(directory);
        Process process = processBuilder.start();

        try (
                BufferedReader error = process.errorReader();
                BufferedReader input = process.inputReader();
        ) {
            int exitStatus = process.waitFor();

            String outputText = input.lines().collect(Collectors.joining("\n"));
            log.info(outputText);

            error.lines().forEachOrdered(log::error);

            if (exitStatus == 0) {
                return outputText;
            } else {
                throw new RuntimeException(command[0] + " exited with status " + exitStatus);
            }
        }
    }
}
