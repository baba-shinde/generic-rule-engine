package com.bss.app.ghcp.cli;

import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Enhanced JaCoCo HTML report reader.
 *
 * - Parses the overall summary (tfoot) as before.
 * - Recursively traverses linked HTML pages within the report up to a
 *   specified depth and collects class rows whose coverage is <= threshold.
 *
 * Usage: java JacocoReportReader [<report-index-path>] [<maxDepth>] [<thresholdPercent>]
 * Example: java JacocoReportReader target/site/jacoco/index.html 3 50
 */
public class JacocoReportReader {
    private Map<String, CodeCoverageDetails> classCoverageMap = new HashMap<>();
    private Pattern subClassPattern = Pattern.compile(".*\\$\\d+\\.html$");

    private final Path reportPath;

    public JacocoReportReader() {
        this(Paths.get(System.getProperty("user.dir"), "target", "site", "jacoco", "index.html"));
    }

    public JacocoReportReader(Path reportPath) {
        this.reportPath = reportPath;
    }

    public void readAndLogReport() {
        readAndLogReport(Integer.MAX_VALUE, 50.0);
    }

    /**
     * Read report, parse summary and traverse pages up to maxDepth to list classes
     * whose coverage is <= thresholdPercent.
     */
    public Map<String, CodeCoverageDetails> readAndLogReport(int maxDepth, double thresholdPercent) {
        if (!Files.exists(reportPath)) {
            System.out.println("JaCoCo report not found at: " + reportPath.toAbsolutePath());
            return null;
        }

        try {
            String html = Files.readString(reportPath, StandardCharsets.UTF_8);
            Summary summary = parseSummaryFromHtml(html);
            if (summary == null) {
                System.out.println("Could not parse JaCoCo summary from report.");
            } else {
                System.out.println("JaCoCo Coverage Summary (from " + reportPath.toString() + "):");
                System.out.println("  Instructions: " + summary.instructionCoverage);
                System.out.println("  Branches:     " + summary.branchCoverage);
                System.out.println("  Lines:        " + summary.lineCoverage);
                System.out.println("  Methods:      " + summary.methodCoverage);
                System.out.println("  Classes:      " + summary.classCoverage);
            }

            List<String> lowCoverage = findLowCoverageClasses(reportPath, maxDepth, thresholdPercent);
            if (lowCoverage.isEmpty()) {
                System.out.println("All classes have coverage above " + thresholdPercent + "% (checked up to depth " + maxDepth + ")");
            } else {
                System.out.println("Classes with coverage <= " + thresholdPercent + "%:");
                for (String s : lowCoverage) {
                    System.out.println("  " + s);
                }
            }

            if (classCoverageMap.size() > 0) {
                classCoverageMap.values().stream().forEach(System.out::println);
            }

        } catch (IOException e) {
            System.err.println("Failed to read JaCoCo report: " + e.getMessage());
        }

        return classCoverageMap;
    }

    // Parse summary totals from the main index.html (same logic as before)
    private Summary parseSummaryFromHtml(String html) {
        Pattern tfootPattern = Pattern.compile("<tfoot>(.*?)</tfoot>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        Matcher tfootMatcher = tfootPattern.matcher(html);
        if (!tfootMatcher.find()) {
            return null;
        }
        String tfoot = tfootMatcher.group(1);

        Pattern tdPattern = Pattern.compile("<td[^>]*>(.*?)</td>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        Matcher tdMatcher = tdPattern.matcher(tfoot);
        List<String> cells = new ArrayList<>();
        while (tdMatcher.find()) {
            String cell = tdMatcher.group(1).trim();
            cell = cell.replaceAll("<.*?>", "").replaceAll("&nbsp;", " ").trim();
            cells.add(cell);
        }

        if (cells.size() < 13) {
            return null;
        }

        String instruction = cells.get(2);
        String branch = cells.get(4);
        String line = cells.get(8);
        String method = cells.get(10);
        String clazz = cells.get(12);

        return new Summary(instruction, branch, line, method, clazz);
    }

    private static class Summary {
        final String instructionCoverage;
        final String branchCoverage;
        final String lineCoverage;
        final String methodCoverage;
        final String classCoverage;

        Summary(String instructionCoverage, String branchCoverage, String lineCoverage, String methodCoverage, String classCoverage) {
            this.instructionCoverage = instructionCoverage;
            this.branchCoverage = branchCoverage;
            this.lineCoverage = lineCoverage;
            this.methodCoverage = methodCoverage;
            this.classCoverage = classCoverage;
        }
    }

    /**
     * Traverse HTML files starting from reportPath up to maxDepth levels and return
     * names of classes (with relative path) whose best-found coverage percent is <= threshold.
     */
    private List<String> findLowCoverageClasses(Path start, int maxDepth, double thresholdPercent) throws IOException {
        Path baseDir = start.getParent();
        Queue<Node> q = new ArrayDeque<>();
        Set<Path> visited = new HashSet<>();
        List<String> results = new ArrayList<>();

        q.add(new Node(start, 0));
        visited.add(start.toRealPath());

        Pattern trPattern = Pattern.compile("<tr[^>]*>(.*?)</tr>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        Pattern aPattern = Pattern.compile("<a[^>]*href\s*=\s*\"([^\"]+)\"[^>]*>(.*?)</a>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        Pattern percentPattern = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*%");

        while (!q.isEmpty()) {
            Node node = q.poll();
            if (node.depth > maxDepth) continue;

            String content = Files.readString(node.path, StandardCharsets.UTF_8);

            // Extract rows and inspect each for a name and a percent
            Matcher trMatcher = trPattern.matcher(content);
            while (trMatcher.find()) {
                String row = trMatcher.group(1);
                // skip header/footer rows that contain 'Total' or 'Name'
                if (row.toLowerCase().contains("total") || row.toLowerCase().contains("name")) continue;

                // find first link in the row (likely the class/package name)
                Matcher aMatcher = aPattern.matcher(row);
                String linkHref = null;
                String linkText = null;
                if (aMatcher.find()) {
                    linkHref = aMatcher.group(1).trim();
                    linkText = aMatcher.group(2).replaceAll("<.*?>", "").trim();
                }

                // find all percents in the row, pick the most relevant (last one)
                Matcher pctMatcher = percentPattern.matcher(row);
                String lastPctStr = null;
                while (pctMatcher.find()) {
                    lastPctStr = pctMatcher.group(1);
                }

                if (linkText != null && lastPctStr != null) {
                    try {
                        double pct = Double.parseDouble(lastPctStr);
                        // Heuristic: consider this a class row if linkHref ends with ".java.html" or linkText ends with ".java" or linkText starts with an uppercase letter
                        boolean looksLikeClass = (linkHref != null && linkHref.toLowerCase().contains(".java"))
                                || (linkText != null && linkText.endsWith(".java"))
                                || (linkText != null && Character.isUpperCase(linkText.charAt(0)) && !linkText.contains("/"));

                        if (looksLikeClass && pct <= thresholdPercent) {
                            // build a readable identifier: linkText + " (" + pct + "%) - " + relativePath
                            String rel = start.getParent().relativize(node.path).toString();
                            if (rel.isEmpty()) rel = node.path.getFileName().toString();
                            // try to compute consolidated percent from class page (aggregate methods) when available
                            double effectivePct = pct;
                            if (linkHref != null && !linkHref.startsWith("http") && !linkHref.startsWith("#") && !linkHref.startsWith("mailto:")) {
                                Path linked = node.path.getParent().resolve(linkHref).normalize();
                                try {
                                    Path real = linked.toRealPath();
                                    if (Files.exists(real) && real.toString().endsWith(".html")) {
                                        Double consolidated = computeConsolidatedPercentForClass(real);
                                        if (consolidated != null) effectivePct = consolidated;
                                    }
                                } catch (IOException ignored) {
                                }
                            }
                            String per = new DecimalFormat("0.##").format(effectivePct);
                            String entry = String.format("%s - %s%% (from %s)", linkText, per, rel);
                            processCodeCoverageEntry(linkText, per, rel);
                            results.add(entry);
                        }

                    } catch (NumberFormatException ignored) {
                    }
                }

                // enqueue linked pages for traversal if the href looks like a local html
                if (linkHref != null && !linkHref.startsWith("http") && !linkHref.startsWith("#") && !linkHref.startsWith("mailto:")) {
                    Path linked = node.path.getParent().resolve(linkHref).normalize();
                    try {
                        Path real = linked.toRealPath();
                        if (!visited.contains(real) && Files.exists(real) && real.toString().endsWith(".html")) {
                            visited.add(real);
                            q.add(new Node(real, node.depth + 1));
                        }
                    } catch (IOException e) {
                        // ignore missing/unresolvable links
                    }
                }
            }
        }

        return results;
    }

    private void processCodeCoverageEntry(String linkText, String percentage, String relPath) {
        if (StringUtils.hasText(linkText) && StringUtils.hasText(percentage) && StringUtils.hasText(relPath)) {
            if(relPath.endsWith("index.html")) {
                //that means this is a leaf level class
                String fqdnClass = relPath.split("/index.html")[0].concat(".").concat(linkText);
                if (!classCoverageMap.containsKey(fqdnClass)) {
                    CodeCoverageDetails coverageDetails = new CodeCoverageDetails();
                    coverageDetails.setFqdnClassName(fqdnClass);
                    coverageDetails.setPercentageCoverage(new Double(percentage));
                    classCoverageMap.put(fqdnClass, coverageDetails);
                }
            } else {
                if (subClassPattern.matcher(relPath).find()) {
                    return;
                }
                String fqdnClass = relPath.replace("/", ".").replace(".html", "");
                if (fqdnClass.contains("$")) {
                    fqdnClass = fqdnClass.replace("$", ".");
                }
                CodeCoverageDetails coverageDetails = classCoverageMap.get(fqdnClass);
                coverageDetails.addMethodCoverage(linkText, new Double(percentage));
            }
        }
    }

    /**
     * Compute consolidated coverage for a class HTML page by aggregating
     * method-level counts. It sums covered and missed counts across methods
     * and returns covered/(missed+covered)*100. Returns null if it cannot
     * determine consolidated coverage.
     */
    private Double computeConsolidatedPercentForClass(Path classHtml) {
        try {
            String content = Files.readString(classHtml, StandardCharsets.UTF_8);
            Pattern trPattern = Pattern.compile("<tr[^>]*>(.*?)</tr>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
            Pattern percentPattern = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*%");
            Pattern numberPattern = Pattern.compile("(\\d+)");
            Matcher trMatcher = trPattern.matcher(content);

            long totalCovered = 0L;
            long total = 0L;
            while (trMatcher.find()) {
                String row = trMatcher.group(1);
                String rowLower = row.toLowerCase();
                if (rowLower.contains("total") || rowLower.contains("name") || rowLower.contains("method")) continue;

                Matcher pctM = percentPattern.matcher(row);
                if (!pctM.find()) continue; // likely not a method row

                int pctStart = pctM.start();
                String beforePct = row.substring(0, pctStart);
                Matcher numM = numberPattern.matcher(beforePct);
                List<Long> nums = new ArrayList<>();
                while (numM.find()) {
                    try { nums.add(Long.parseLong(numM.group(1))); } catch (Exception ignored) {}
                }
                if (nums.size() >= 2) {
                    long missed = nums.get(nums.size()-2);
                    long covered = nums.get(nums.size()-1);
                    totalCovered += covered;
                    total += (missed + covered);
                } else {
                    // fallback: look for numbers anywhere in the row
                    Matcher numAll = numberPattern.matcher(row);
                    List<Long> n2 = new ArrayList<>();
                    while (numAll.find()) {
                        try { n2.add(Long.parseLong(numAll.group(1))); } catch (Exception ignored) {}
                    }
                    if (n2.size() >= 2) {
                        long missed = n2.get(n2.size()-2);
                        long covered = n2.get(n2.size()-1);
                        totalCovered += covered;
                        total += (missed + covered);
                    }
                }
            }

            if (total == 0L) return null;
            return (totalCovered * 100.0) / total;
        } catch (IOException e) {
            return null;
        }
    }

    private static class Node {
        final Path path;
        final int depth;

        Node(Path path, int depth) {
            this.path = path;
            this.depth = depth;
        }
    }

    public Map<String, CodeCoverageDetails> extractJacocoReportDetails() {
        Path path = null;
        int depth = Integer.MAX_VALUE;
        double threshold = 99.0;
        JacocoReportReader reader = path == null ? new JacocoReportReader() : new JacocoReportReader(path);
        reader.readAndLogReport(depth, threshold);

        return reader.classCoverageMap;
    }

    public static void main(String[] args) {
        Path path = null;
        int depth = Integer.MAX_VALUE;
        double threshold = 99.0;
        if (args != null && args.length > 0) {
            path = Paths.get(args[0]);
        }
        if (args != null && args.length > 1) {
            try {
                depth = Integer.parseInt(args[1]);
            } catch (NumberFormatException ignored) {
            }
        }
        if (args != null && args.length > 2) {
            try {
                threshold = Double.parseDouble(args[2]);
            } catch (NumberFormatException ignored) {
            }
        }

        JacocoReportReader reader = path == null ? new JacocoReportReader() : new JacocoReportReader(path);
        reader.readAndLogReport(depth, threshold);
    }
}
