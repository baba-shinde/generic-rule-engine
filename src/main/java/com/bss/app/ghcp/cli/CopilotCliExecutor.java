package com.bss.app.ghcp.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class CopilotCliExecutor {
    private static final String COPILOT_EXECUTABLE_CMD = "copilot";

    public void executePrompt(String prompt) {
        try {
            List<String> command = Arrays.asList(COPILOT_EXECUTABLE_CMD, "-p", prompt, "--allow-all-tools");
            ProcessBuilder pb = new ProcessBuilder(command);

            // Only set GH_TOKEN if provided in the current environment to avoid hard-coding secrets
            String token = System.getenv("GH_TOKEN");
            if (token != null && !token.isEmpty()) {
                pb.environment().put("GH_TOKEN", token);
            }

            Process process = pb.start();

            // Read output
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Copilot Response:\n" + output.toString());
            } else {
                System.err.println("Error: Exit code " + exitCode);
                try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = errorReader.readLine()) != null) {
                        System.err.println(line);
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
