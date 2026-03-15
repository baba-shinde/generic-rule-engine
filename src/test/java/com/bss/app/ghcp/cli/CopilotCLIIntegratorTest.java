package com.bss.app.ghcp.cli;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class CopilotCLIIntegratorTest {

    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private ByteArrayOutputStream outContent;
    private ByteArrayOutputStream errContent;

    @BeforeEach
    void setUpStreams() {
        outContent = new ByteArrayOutputStream();
        errContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    void testExecuteProcess_successfulOutput(@TempDir Path tempDir) throws Exception {
        Path script = createScript(tempDir, "success.sh", "#!/bin/sh\necho 'hello world'\nexit 0\n");
        CopilotCLIIntegrator integrator = new CopilotCLIIntegrator(script.toAbsolutePath().toString());

        integrator.executeProcess();

        String out = outContent.toString();
        assertTrue(out.contains("Copilot Response:"), "Should print Copilot Response header");
        assertTrue(out.contains("hello world"), "Should contain script output");
    }

    @Test
    void testExecuteProcess_errorExit(@TempDir Path tempDir) throws Exception {
        Path script = createScript(tempDir, "error.sh", "#!/bin/sh\necho 'failmsg' >&2\nexit 2\n");
        CopilotCLIIntegrator integrator = new CopilotCLIIntegrator(script.toAbsolutePath().toString());

        integrator.executeProcess();

        String err = errContent.toString();
        assertTrue(err.contains("Error: Exit code 2"), "Should report non-zero exit code");
        assertTrue(err.contains("failmsg"), "Should print stderr from process");
    }

    @Test
    void testExecuteProcess_ioException_nonexistent() {
        CopilotCLIIntegrator integrator = new CopilotCLIIntegrator("/no/such/executable/doesnotexist");
        // Should not throw; exception is caught inside method
        assertDoesNotThrow(integrator::executeProcess);
        String out = outContent.toString();
        assertFalse(out.contains("Copilot Response:"), "No successful response expected");
    }

    @Test
    void testExecuteProcess_interrupted(@TempDir Path tempDir) throws Exception {
        Path script = createScript(tempDir, "sleep.sh", "#!/bin/sh\nsleep 5\n");
        CopilotCLIIntegrator integrator = new CopilotCLIIntegrator(script.toAbsolutePath().toString());

        Thread t = new Thread(integrator::executeProcess);
        t.start();
        // Give the process a moment to start and block on waitFor
        Thread.sleep(200);
        t.interrupt();
        t.join(5000);
        //assertFalse(t.isAlive(), "Thread should have finished after interrupt");
    }

    @Disabled
    @Test
    void testExecuteProcess_withGHToken(@TempDir Path tempDir) throws Exception {
        // Set GH_TOKEN in the environment so the code will add it to ProcessBuilder env
        setEnv(Collections.singletonMap("GH_TOKEN", "MY_TEST_TOKEN_123"));

        // Script will print the environment variable value
        Path script = createScript(tempDir, "envprint.sh", "#!/bin/sh\necho \"$GH_TOKEN\"\nexit 0\n");
        CopilotCLIIntegrator integrator = new CopilotCLIIntegrator(script.toAbsolutePath().toString());

        integrator.executeProcess();

        String out = outContent.toString();
        assertTrue(out.contains("MY_TEST_TOKEN_123"), "Should propagate GH_TOKEN to child process");
    }

    @Test
    void testMain_noException() {
        // main uses default "copilot" executable; ensure it doesn't throw
        assertDoesNotThrow(() -> CopilotCLIIntegrator.main(new String[0]));
    }

    // Helper to create executable scripts.
    private Path createScript(Path dir, String name, String content) throws IOException {
        Path script = dir.resolve(name);
        Files.write(script, content.getBytes());
        try {
            // Set executable permissions on POSIX systems
            Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxr-xr-x");
            Files.setPosixFilePermissions(script, perms);
        } catch (UnsupportedOperationException ignored) {
            // Windows or other FS that does not support Posix permissions
            script.toFile().setExecutable(true);
        }
        return script;
    }

    // Reflection-based environment setter. Works on most JVMs used in CI.
    @SuppressWarnings({"unchecked", "TryWithIdenticalCatches"})
    private static void setEnv(Map<String, String> newenv) throws Exception {
        try {
            Class<?> processEnvironment = Class.forName("java.lang.ProcessEnvironment");
            Field theEnvironmentField = processEnvironment.getDeclaredField("theEnvironment");
            theEnvironmentField.setAccessible(true);
            Map<String, String> env = (Map<String, String>) theEnvironmentField.get(null);
            env.putAll(newenv);
            Field theCaseInsensitiveEnvironmentField = processEnvironment.getDeclaredField("theCaseInsensitiveEnvironment");
            theCaseInsensitiveEnvironmentField.setAccessible(true);
            Map<String, String> cienv = (Map<String, String>) theCaseInsensitiveEnvironmentField.get(null);
            cienv.putAll(newenv);
        } catch (NoSuchFieldException e) {
            Map<String, String> env = System.getenv();
            Class<?> cl = env.getClass();
            Field field = cl.getDeclaredField("m");
            field.setAccessible(true);
            Map<String, String> writableEnv = (Map<String, String>) field.get(env);
            writableEnv.putAll(newenv);
        }
    }
}
