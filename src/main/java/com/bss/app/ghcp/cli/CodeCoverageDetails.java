package com.bss.app.ghcp.cli;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CodeCoverageDetails {
    private String fqdnClassName;
    private Double percentageCoverage;
    private Map<String, Double> methodCoverage;


    public String getFqdnClassName() {
        return fqdnClassName;
    }

    public void setFqdnClassName(String fqdnClassName) {
        this.fqdnClassName = fqdnClassName;
    }

    public Double getPercentageCoverage() {
        return percentageCoverage;
    }

    public void setPercentageCoverage(Double percentageCoverage) {
        this.percentageCoverage = percentageCoverage;
    }

    public Map<String, Double> getMethodCoverage() {
        return methodCoverage;
    }

    public void setMethodCoverage(Map<String, Double> methodCoverage) {
        this.methodCoverage = methodCoverage;
    }

    public void addMethodCoverage(String methodName, Double coverage) {
        if (this.methodCoverage == null) {
            this.methodCoverage = new HashMap<>();
        }
        if (!this.methodCoverage.containsKey(methodName)) {
            this.methodCoverage.put(methodName, coverage);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CodeCoverageDetails that = (CodeCoverageDetails) o;
        return Objects.equals(fqdnClassName, that.fqdnClassName) && Objects.equals(percentageCoverage, that.percentageCoverage) && Objects.equals(methodCoverage, that.methodCoverage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fqdnClassName, percentageCoverage, methodCoverage);
    }

    @Override
    public String toString() {
        return "CodeCoverageDetails{" +
                "fqdnClassName='" + fqdnClassName + '\'' +
                ", percentageCoverage=" + percentageCoverage +
                ", methodCoverage=" + methodCoverage +
                '}';
    }
}
