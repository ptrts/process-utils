package we.processUtils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.BufferedReader;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ProcessRunner {

    @SneakyThrows
    public static String runProcess(File directory, String... command) {

        command = Arrays
                .stream(command)
                .filter(StringUtils::isNotBlank)
                .toArray(String[]::new);

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(directory);
        Process process = processBuilder.start();

        try (
                BufferedReader error = process.errorReader();
                BufferedReader input = process.inputReader();
        ) {
            int exitStatus = process.waitFor();

            String outputText = input.lines().collect(Collectors.joining("\n"));
            if (StringUtils.isNotBlank(outputText)) {
                log.info(outputText);
            }

            error
                    .lines()
                    .filter(StringUtils::isNotBlank)
                    .forEachOrdered(log::error);

            if (exitStatus == 0) {
                return outputText;
            } else {
                throw new RuntimeException(command[0] + " exited with status " + exitStatus);
            }
        }
    }

    public static String runProcess(File directory, List<String> list) {
        return runProcess(directory, list.toArray(String[]::new));
    }

    public static String runScript(String scriptFilename, String windowsExtension, String nixExtension, File directory, String... command) {

        List<String> commandsList = new ArrayList<>(Arrays.asList(command));

        String shell = SystemUtils.IS_OS_WINDOWS ? "cmd" : "sh";
        commandsList.add(0, shell);

        String extension = SystemUtils.IS_OS_WINDOWS ? windowsExtension : nixExtension;
        String filename = getFilenameWithExtension(scriptFilename, extension);
        String pathStr = Path.of(".", filename).toString();
        commandsList.add(1, pathStr);

        return runProcess(directory, commandsList);
    }

    public static String runBatOrSh(String scriptFilename, File directory, String... command) {
        return runScript(scriptFilename, "bat", "sh", directory, command);
    }

    public static String runCmdOrSh(String scriptFilename, File directory, String... command) {
        return runScript(scriptFilename, "cmd", "sh", directory, command);
    }

    public static String runBat(String scriptFilename, File directory, String... command) {
        return runScript(scriptFilename, "bat", null, directory, command);
    }

    public static String runCmd(String scriptFilename, File directory, String... command) {
        return runScript(scriptFilename, "cmd", null, directory, command);
    }

    private static String getFilenameWithExtension(String filename, String extension) {
        if (StringUtils.isBlank(extension)) {
            return filename;
        } else {
            return filename + "." + extension;
        }
    }
}
