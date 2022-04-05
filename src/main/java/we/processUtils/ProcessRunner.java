package we.processUtils;

import lombok.SneakyThrows;

import java.io.File;

public class ProcessRunner {

    @SneakyThrows
    public static void runProcess(File directory, String... command) {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(directory);
        Process process = processBuilder.start();
        int exitStatus = process.waitFor();
        if (exitStatus != 0) {
            throw new RuntimeException(command[0] + " exited with status " + exitStatus);
        }
    }
}
