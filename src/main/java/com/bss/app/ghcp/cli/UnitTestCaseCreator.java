package com.bss.app.ghcp.cli;

import java.util.Map;

public class UnitTestCaseCreator {
    private CopilotCliExecutor copilotCliExecutor;
    private JacocoReportReader reportReader;

    public UnitTestCaseCreator() {
        this.copilotCliExecutor = new CopilotCliExecutor();
        this.reportReader = new JacocoReportReader();
    }

    static void main() {
        UnitTestCaseCreator unitTestCaseCreator = new UnitTestCaseCreator();
        unitTestCaseCreator.generateUnitTestCasesUsingCli();
    }

    public void generateUnitTestCasesUsingCli() {
        Map<String, CodeCoverageDetails> codeCoverageDetailsMap = reportReader.extractJacocoReportDetails();

        codeCoverageDetailsMap.values().stream().forEach(cc -> {
            System.out.println("Processing CodeCoverageDetails: " + cc);
            if (cc.getPercentageCoverage() != null && cc.getPercentageCoverage() < 85.00d) {
                String unitTestPrompt = getUnitTestPrompt(cc.getFqdnClassName());
                System.out.println("***** Generating test case for class: " + cc.getFqdnClassName() + " *****");
                copilotCliExecutor.executePrompt(unitTestPrompt);
            } else {
                System.out.println("Class: " + cc.getFqdnClassName() + " has better code coverage, Ignoring it");
            }

            /*Map<String, Double> methodCoverage = cc.getMethodCoverage();
            for (String k : methodCoverage.keySet()) {
                Double percentage = methodCoverage.get(k);
                if (percentage < 85.00d) {
                    String unitTestPrompt = getUnitTestPrompt(cc.getFqdnClassName(), k);
                    System.out.println("***** Generating test case for Method : " + k + " from class: " + cc.getFqdnClassName() + " *****");
                    copilotCliExecutor.executePrompt(unitTestPrompt);
                } else {
                    System.out.println("Method : " + k + " from class: " + cc.getFqdnClassName() + " has better code coverage, Ignoring it");
                }
            }*/
        });
    }

    public String getUnitTestPrompt(String fqdnClassName) {
        StringBuilder builder = new StringBuilder("");
        /*builder.append("Create the junit test class for java class ")
                .append(fqdnClassName)
                .append(" if does not exist. Write the unit test cases for ")
                .append(methodName)
                .append(" to have 100% coverage for the method. Cover all the corver cases and possible combinations of cases.");*/

        builder.append("Create the junit test class for java class ")
                .append(fqdnClassName)
                .append(" if does not exist. Write the unit test cases for it")
                .append(" to have 100% coverage for the method. Cover all the corver cases and possible combinations of cases.");

        return builder.toString();
    }
}
