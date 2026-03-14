package com.bss.app.ghcp.cli;

import java.io.*;
import java.util.*;

public class CopilotCLIIntegrator {
    private final String copilotExecutable;

    public CopilotCLIIntegrator() {
        this("copilot");
    }

    /**
     * Constructor for testing to inject a different executable path/name.
     */
    public CopilotCLIIntegrator(String copilotExecutable) {
        this.copilotExecutable = copilotExecutable;
    }

    public void executeProcess() {
        String userInput = "factorial calculation";  // Dynamic prompt generation
        String prompt = "Generate the README file for the project in detail";

        try {
            List<String> command = Arrays.asList(copilotExecutable, "-p", prompt, "--allow-all-tools");
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

    public static void main(String[] args) {
        CopilotCLIIntegrator integrator = new CopilotCLIIntegrator();
        integrator.executeProcess();
    }
}
