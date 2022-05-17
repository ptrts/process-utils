package org.taruts.processUtils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.File;

@Slf4j
class ProcessRunnerTest {

    @Test
    void runProcess() {
        File directory = new File(".");
        ProcessRunner.runProcess(directory, "cmd.exe", "/c", "echo", "hello");
    }
}
