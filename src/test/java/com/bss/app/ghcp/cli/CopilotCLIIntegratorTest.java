package com.bss.app.ghcp.cli;

import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class CopilotCLIIntegratorTest {

    private Path tempDir;
    private Path copilotScript;

    @BeforeEach
    void setUp() throws IOException {
        tempDir = Files.createTempDirectory("copilot-test-");
    }

    @AfterEach
    void tearDown() throws IOException {
        if (copilotScript != null && Files.exists(copilotScript)) {
            Files.deleteIfExists(copilotScript);
        }
        if (tempDir != null && Files.exists(tempDir)) {
            Files.walk(tempDir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(p -> {
                        try { Files.deleteIfExists(p); } catch (IOException ignored) {}
                    });
        }
    }

    @Test
    void executeProcess_printsOutputOnSuccess() throws Exception {
        // Create a simple script that writes to stdout and exits 0
        copilotScript = tempDir.resolve("copilot");
        String script = "#!/bin/sh\necho \"Simulated response from copilot\"\nexit 0\n";
        Files.write(copilotScript, script.getBytes());
        // Make executable (POSIX)
        copilotScript.toFile().setExecutable(true);

        // Capture stdout
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        try {
            System.setOut(new PrintStream(out));

            CopilotCLIIntegrator integrator = new CopilotCLIIntegrator(copilotScript.toAbsolutePath().toString());
            integrator.executeProcess();

            String output = out.toString();
            assertTrue(output.contains("Simulated response from copilot"), "Should contain script output");
            assertTrue(output.contains("Copilot Response"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void executeProcess_printsErrorOnNonZeroExit() throws Exception {
        // Create a script that writes to stderr and exits 2
        copilotScript = tempDir.resolve("copilot");
        String script = "#!/bin/sh\necho \"Simulated error from copilot\" 1>&2\nexit 2\n";
        Files.write(copilotScript, script.getBytes());
        copilotScript.toFile().setExecutable(true);

        // Capture stderr
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;
        try {
            System.setErr(new PrintStream(err));

            CopilotCLIIntegrator integrator = new CopilotCLIIntegrator(copilotScript.toAbsolutePath().toString());
            integrator.executeProcess();

            String errorOutput = err.toString();
            assertTrue(errorOutput.contains("Error: Exit code 2"));
            assertTrue(errorOutput.contains("Simulated error from copilot"));
        } finally {
            System.setErr(originalErr);
        }
    }
}
