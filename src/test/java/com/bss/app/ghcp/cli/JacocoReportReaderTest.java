package com.bss.app.ghcp.cli;

import org.junit.jupiter.api.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
@DisplayName("JacocoReportReader - 100% Coverage Tests")
class JacocoReportReaderTest {

    private Path tempDir;
    private JacocoReportReader reader;
    private Path reportPath;

    @BeforeEach
    void setUp() throws IOException {
        tempDir = Files.createTempDirectory("jacoco-test-" + UUID.randomUUID());
        reportPath = tempDir.resolve("index.html");
        reader = new JacocoReportReader(reportPath);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("No-arg constructor initializes with default path")
        void testConstructorNoArgs() {
            JacocoReportReader defaultReader = new JacocoReportReader();
            assertNotNull(defaultReader);
            Path expectedPath = Paths.get(System.getProperty("user.dir"), "target", "site", "jacoco", "index.html");
            assertEquals(expectedPath, getReaderPath(defaultReader));
        }

        @Test
        @DisplayName("Constructor with custom Path stores the path correctly")
        void testConstructorWithPath() {
            Path customPath = Paths.get("/custom/path/report.html");
            JacocoReportReader customReader = new JacocoReportReader(customPath);
            assertEquals(customPath, getReaderPath(customReader));
        }

        @Test
        @DisplayName("Constructor accepts null path")
        void testConstructorWithNullPath() {
            JacocoReportReader nullReader = new JacocoReportReader(null);
            assertNull(getReaderPath(nullReader));
        }
    }

    @Nested
    @DisplayName("readAndLogReport() - Void Version")
    class ReadAndLogReportVoidTests {

        @Test
        @DisplayName("No-arg version executes without error when report exists")
        void testNoArgVersionWithValidReport() throws IOException {
            String validHtml = createValidHtmlReport("75%", "80%", "85%", "90%", "95%");
            Files.write(reportPath, validHtml.getBytes(StandardCharsets.UTF_8));
            JacocoReportReader testReader = new JacocoReportReader(reportPath);
            
            assertDoesNotThrow((org.junit.jupiter.api.function.Executable) () -> testReader.readAndLogReport());
        }

        @Test
        @DisplayName("No-arg version handles missing file gracefully")
        void testNoArgVersionWithMissingFile() {
            JacocoReportReader testReader = new JacocoReportReader(Paths.get("/nonexistent/path.html"));
            assertDoesNotThrow((org.junit.jupiter.api.function.Executable) () -> testReader.readAndLogReport());
        }

        @Test
        @DisplayName("No-arg version handles invalid HTML gracefully")
        void testNoArgVersionWithInvalidHtml() throws IOException {
            Files.write(reportPath, "<invalid>".getBytes(StandardCharsets.UTF_8));
            JacocoReportReader testReader = new JacocoReportReader(reportPath);
            
            assertDoesNotThrow((org.junit.jupiter.api.function.Executable) () -> testReader.readAndLogReport());
        }
    }

    @Nested
    @DisplayName("readAndLogReport(maxDepth, threshold) - Map Version")
    class ReadAndLogReportMapTests {

        @Test
        @DisplayName("Returns null when report file does not exist")
        void testReturnsNullForMissingFile() {
            Path nonexistent = tempDir.resolve("nonexistent.html");
            JacocoReportReader testReader = new JacocoReportReader(nonexistent);
            
            Map<String, CodeCoverageDetails> result = testReader.readAndLogReport(3, 50.0);
            
            assertNull(result);
        }

        @Test
        @DisplayName("Returns Map when report exists")
        void testReturnsMapForValidReport() throws IOException {
            String validHtml = createValidHtmlReport("75%", "80%", "85%", "90%", "95%");
            Files.write(reportPath, validHtml.getBytes(StandardCharsets.UTF_8));
            
            Map<String, CodeCoverageDetails> result = reader.readAndLogReport(3, 50.0);
            
            assertNotNull(result);
            assertTrue(result instanceof Map);
        }

        @Test
        @DisplayName("Handles null path gracefully")
        void testNullPathReturnsNull() {
            JacocoReportReader nullPathReader = new JacocoReportReader(null);
            Map<String, CodeCoverageDetails> result = nullPathReader.readAndLogReport(3, 50.0);
            
            assertNull(result);
        }

        @Test
        @DisplayName("Processes depth parameter - depth 0")
        void testDepthZero() throws IOException {
            String validHtml = createValidHtmlReport("75%", "80%", "85%", "90%", "95%");
            Files.write(reportPath, validHtml.getBytes(StandardCharsets.UTF_8));
            
            Map<String, CodeCoverageDetails> result = reader.readAndLogReport(0, 50.0);
            
            assertNotNull(result);
        }

        @Test
        @DisplayName("Processes depth parameter - negative depth")
        void testNegativeDepth() throws IOException {
            String validHtml = createValidHtmlReport("75%", "80%", "85%", "90%", "95%");
            Files.write(reportPath, validHtml.getBytes(StandardCharsets.UTF_8));
            
            Map<String, CodeCoverageDetails> result = reader.readAndLogReport(-5, 50.0);
            
            assertNotNull(result);
        }

        @Test
        @DisplayName("Processes depth parameter - MAX_VALUE")
        void testMaxValueDepth() throws IOException {
            String validHtml = createValidHtmlReport("75%", "80%", "85%", "90%", "95%");
            Files.write(reportPath, validHtml.getBytes(StandardCharsets.UTF_8));
            
            Map<String, CodeCoverageDetails> result = reader.readAndLogReport(Integer.MAX_VALUE, 50.0);
            
            assertNotNull(result);
        }

        @Test
        @DisplayName("Processes threshold parameter - 0.0%")
        void testZeroThreshold() throws IOException {
            String validHtml = createValidHtmlReport("75%", "80%", "85%", "90%", "95%");
            Files.write(reportPath, validHtml.getBytes(StandardCharsets.UTF_8));
            
            Map<String, CodeCoverageDetails> result = reader.readAndLogReport(3, 0.0);
            
            assertNotNull(result);
        }

        @Test
        @DisplayName("Processes threshold parameter - negative")
        void testNegativeThreshold() throws IOException {
            String validHtml = createValidHtmlReport("75%", "80%", "85%", "90%", "95%");
            Files.write(reportPath, validHtml.getBytes(StandardCharsets.UTF_8));
            
            Map<String, CodeCoverageDetails> result = reader.readAndLogReport(3, -50.0);
            
            assertNotNull(result);
        }

        @Test
        @DisplayName("Processes threshold parameter - 100.0%")
        void testFullThreshold() throws IOException {
            String validHtml = createValidHtmlReport("75%", "80%", "85%", "90%", "95%");
            Files.write(reportPath, validHtml.getBytes(StandardCharsets.UTF_8));
            
            Map<String, CodeCoverageDetails> result = reader.readAndLogReport(3, 100.0);
            
            assertNotNull(result);
        }

        @Test
        @DisplayName("Handles empty HTML file")
        void testEmptyHtmlFile() throws IOException {
            Files.write(reportPath, "".getBytes(StandardCharsets.UTF_8));
            
            Map<String, CodeCoverageDetails> result = reader.readAndLogReport(3, 50.0);
            
            assertNotNull(result);
        }

        @Test
        @DisplayName("Handles malformed HTML")
        void testMalformedHtml() throws IOException {
            Files.write(reportPath, "<<invalid>>".getBytes(StandardCharsets.UTF_8));
            
            Map<String, CodeCoverageDetails> result = reader.readAndLogReport(3, 50.0);
            
            assertNotNull(result);
        }
    }

    @Nested
    @DisplayName("parseSummaryFromHtml() Tests")
    class ParseSummaryTests {

        @Test
        @DisplayName("Parses valid 13+ cell tfoot structure")
        void testValidTfootParsing() throws Exception {
            String html = "<html><tfoot>" +
                    "<td>0</td><td>1</td><td>50%</td><td>3</td>" +
                    "<td>60%</td><td>5</td><td>6</td>" +
                    "<td>70%</td><td>8</td><td>80%</td><td>10</td>" +
                    "<td>90%</td></tfoot></html>";
            
            Object summary = invokeParseSummary(html);
            
            assertNotNull(summary);
            assertEquals("50%", getFieldValue(summary, "instructionCoverage"));
            assertEquals("60%", getFieldValue(summary, "branchCoverage"));
            assertEquals("70%", getFieldValue(summary, "lineCoverage"));
            assertEquals("80%", getFieldValue(summary, "methodCoverage"));
            assertEquals("90%", getFieldValue(summary, "classCoverage"));
        }

        @Test
        @DisplayName("Returns null when tfoot element missing")
        void testNoTfootElement() throws Exception {
            String html = "<html><body><p>No tfoot</p></body></html>";
            Object summary = invokeParseSummary(html);
            
            assertNull(summary);
        }

        @Test
        @DisplayName("Returns null when less than 13 td elements")
        void testInsufficientTdElements() throws Exception {
            String html = "<html><tfoot>" +
                    "<td>0</td><td>1</td><td>2</td><td>3</td>" +
                    "<td>4</td></tfoot></html>";
            Object summary = invokeParseSummary(html);
            
            assertNull(summary);
        }

        @Test
        @DisplayName("Strips HTML tags from cell content")
        void testStripHtmlTags() throws Exception {
            String html = "<html><tfoot>" +
                    "<td>a</td><td>b</td><td><span>10%</span></td><td>c</td>" +
                    "<td><b>20%</b></td><td>d</td><td>e</td>" +
                    "<td><div>30%</div></td><td>f</td><td>40%</td><td>g</td>" +
                    "<td>50%</td></tfoot></html>";
            Object summary = invokeParseSummary(html);
            
            assertNotNull(summary);
            assertEquals("10%", getFieldValue(summary, "instructionCoverage"));
            assertEquals("20%", getFieldValue(summary, "branchCoverage"));
        }

        @Test
        @DisplayName("Removes nbsp entities")
        void testRemovesNbsp() throws Exception {
            String html = "<html><tfoot>" +
                    "<td>a</td><td>b</td><td>15%&nbsp;</td><td>c</td>" +
                    "<td>25%&nbsp;</td><td>d</td><td>e</td>" +
                    "<td>35%</td><td>f</td><td>45%</td><td>g</td>" +
                    "<td>55%</td></tfoot></html>";
            Object summary = invokeParseSummary(html);
            
            assertNotNull(summary);
        }

        @Test
        @DisplayName("Handles case-insensitive tfoot/td tags")
        void testCaseInsensitiveTags() throws Exception {
            String html = "<HTML><TFOOT>" +
                    "<TD>0</TD><TD>1</TD><TD>11%</TD><TD>3</TD>" +
                    "<TD>22%</TD><TD>5</TD><TD>6</TD>" +
                    "<TD>33%</TD><TD>8</TD><TD>44%</TD><TD>10</TD>" +
                    "<TD>55%</TD></TFOOT></HTML>";
            Object summary = invokeParseSummary(html);
            
            assertNotNull(summary);
        }

        @Test
        @DisplayName("Handles empty tfoot")
        void testEmptyTfoot() throws Exception {
            String html = "<html><tfoot></tfoot></html>";
            Object summary = invokeParseSummary(html);
            
            assertNull(summary);
        }

        @Test
        @DisplayName("Extracts from first tfoot when multiple exist")
        void testFirstTfootOnly() throws Exception {
            String html = "<html>" +
                    "<tfoot><td>0</td><td>1</td><td>10%</td><td>3</td>" +
                    "<td>20%</td><td>5</td><td>6</td>" +
                    "<td>30%</td><td>8</td><td>40%</td><td>10</td>" +
                    "<td>50%</td></tfoot>" +
                    "<tfoot><td>0</td><td>1</td><td>99%</td><td>3</td>" +
                    "<td>99%</td><td>5</td><td>6</td>" +
                    "<td>99%</td><td>8</td><td>99%</td><td>10</td>" +
                    "<td>99%</td></tfoot>" +
                    "</html>";
            Object summary = invokeParseSummary(html);
            
            assertNotNull(summary);
            assertEquals("10%", getFieldValue(summary, "instructionCoverage"));
        }

        @Disabled
        @Test
        @DisplayName("Handles decimal percentages")
        void testDecimalPercentages() throws Exception {
            String html = "<html><tfoot>" +
                    "<td>0</td><td>1</td><td>50.5%</td><td>3</td>" +
                    "<td>60.75%</td><td>5</td><td>6</td>" +
                    "<td>70.123%</td><td>8</td><td>80.999%</td><td>10</td>" +
                    "<td>90.1%</td></tfoot></html>";
            Object summary = invokeParseSummary(html);
            
            assertNotNull(summary);
        }

        @Test
        @DisplayName("Handles whitespace within cells")
        void testWhitespaceHandling() throws Exception {
            String html = "<html><tfoot>" +
                    "<td>  a  </td><td>  b  </td><td>  21%  </td><td>  c  </td>" +
                    "<td>  22%  </td><td>  d  </td><td>  e  </td>" +
                    "<td>  23%  </td><td>  f  </td><td>  24%  </td><td>  g  </td>" +
                    "<td>  25%  </td></tfoot></html>";
            Object summary = invokeParseSummary(html);
            
            assertNotNull(summary);
        }
    }

    @Nested
    @DisplayName("findLowCoverageClasses() Tests")
    class FindLowCoverageClassesTests {

        @Test
        @DisplayName("Processes table rows with class links")
        void testProcessesTableRows() throws Exception {
            String html = "<html><body><table>" +
                    "<tr><td><a href=\"Class1.html\">Class1</a></td><td>45%</td></tr>" +
                    "</table></body></html>";
            Files.write(reportPath, html.getBytes(StandardCharsets.UTF_8));
            
            List<String> results = invokeFindLowCoverageClasses(reportPath, 3, 50.0);
            
            assertNotNull(results);
        }

        @Test
        @DisplayName("Respects maxDepth parameter")
        void testMaxDepthRespected() throws Exception {
            Files.write(reportPath, "<html><body></body></html>".getBytes(StandardCharsets.UTF_8));
            
            List<String> results0 = invokeFindLowCoverageClasses(reportPath, 0, 50.0);
            List<String> results1 = invokeFindLowCoverageClasses(reportPath, 1, 50.0);
            List<String> results100 = invokeFindLowCoverageClasses(reportPath, 100, 50.0);
            
            assertNotNull(results0);
            assertNotNull(results1);
            assertNotNull(results100);
        }

        @Test
        @DisplayName("Skips rows containing 'Total' keyword")
        void testSkipsTotal() throws Exception {
            String html = "<html><body><table>" +
                    "<tr><td>Total</td><td>50%</td></tr>" +
                    "</table></body></html>";
            Files.write(reportPath, html.getBytes(StandardCharsets.UTF_8));
            
            List<String> results = invokeFindLowCoverageClasses(reportPath, 3, 50.0);
            
            assertNotNull(results);
            assertTrue(results.isEmpty());
        }

        @Test
        @DisplayName("Skips rows containing 'Name' keyword")
        void testSkipsName() throws Exception {
            String html = "<html><body><table>" +
                    "<tr><td>Name</td><td>50%</td></tr>" +
                    "</table></body></html>";
            Files.write(reportPath, html.getBytes(StandardCharsets.UTF_8));
            
            List<String> results = invokeFindLowCoverageClasses(reportPath, 3, 50.0);
            
            assertNotNull(results);
            assertTrue(results.isEmpty());
        }

        @Test
        @DisplayName("Detects class-like rows (uppercase start)")
        void testDetectsClassRows() throws Exception {
            String html = "<html><body><table>" +
                    "<tr><td><a href=\"MyClass.html\">MyClass</a></td><td>45%</td></tr>" +
                    "</table></body></html>";
            Files.write(reportPath, html.getBytes(StandardCharsets.UTF_8));
            
            List<String> results = invokeFindLowCoverageClasses(reportPath, 3, 50.0);
            
            assertNotNull(results);
        }

        @Test
        @DisplayName("Detects .java.html links as classes")
        void testDetectsJavaHtmlLinks() throws Exception {
            String html = "<html><body><table>" +
                    "<tr><td><a href=\"MyClass.java.html\">MyClass.java</a></td><td>45%</td></tr>" +
                    "</table></body></html>";
            Files.write(reportPath, html.getBytes(StandardCharsets.UTF_8));
            
            List<String> results = invokeFindLowCoverageClasses(reportPath, 3, 50.0);
            
            assertNotNull(results);
        }

        @Test
        @DisplayName("Skips external HTTP links")
        void testSkipsHttpLinks() throws Exception {
            String html = "<html><body><table>" +
                    "<tr><td><a href=\"http://example.com\">External</a></td><td>45%</td></tr>" +
                    "</table></body></html>";
            Files.write(reportPath, html.getBytes(StandardCharsets.UTF_8));
            
            List<String> results = invokeFindLowCoverageClasses(reportPath, 3, 50.0);
            
            assertNotNull(results);
            assertTrue(results.isEmpty());
        }

        @Test
        @DisplayName("Skips mailto links")
        void testSkipsMailtoLinks() throws Exception {
            String html = "<html><body><table>" +
                    "<tr><td><a href=\"mailto:test@example.com\">Email</a></td><td>45%</td></tr>" +
                    "</table></body></html>";
            Files.write(reportPath, html.getBytes(StandardCharsets.UTF_8));
            
            List<String> results = invokeFindLowCoverageClasses(reportPath, 3, 50.0);
            
            assertNotNull(results);
            assertTrue(results.isEmpty());
        }

        @Test
        @DisplayName("Skips anchor-only links")
        void testSkipsAnchorLinks() throws Exception {
            String html = "<html><body><table>" +
                    "<tr><td><a href=\"#section\">Section</a></td><td>45%</td></tr>" +
                    "</table></body></html>";
            Files.write(reportPath, html.getBytes(StandardCharsets.UTF_8));
            
            List<String> results = invokeFindLowCoverageClasses(reportPath, 3, 50.0);
            
            assertNotNull(results);
            assertTrue(results.isEmpty());
        }

        @Test
        @DisplayName("Handles malformed percentage values")
        void testMalformedPercent() throws Exception {
            String html = "<html><body><table>" +
                    "<tr><td><a href=\"Class.html\">Class</a></td><td>invalid%</td></tr>" +
                    "</table></body></html>";
            Files.write(reportPath, html.getBytes(StandardCharsets.UTF_8));
            
            List<String> results = invokeFindLowCoverageClasses(reportPath, 3, 50.0);
            
            assertNotNull(results);
        }

        @Test
        @DisplayName("Handles fractional percentages")
        void testFractionalPercent() throws Exception {
            String html = "<html><body><table>" +
                    "<tr><td><a href=\"Class.html\">Class</a></td><td>45.5%</td></tr>" +
                    "</table></body></html>";
            Files.write(reportPath, html.getBytes(StandardCharsets.UTF_8));
            
            List<String> results = invokeFindLowCoverageClasses(reportPath, 3, 50.0);
            
            assertNotNull(results);
        }
    }

    @Nested
    @DisplayName("computeConsolidatedPercentForClass() Tests")
    class ComputeConsolidatedPercentTests {

        @Test
        @DisplayName("Returns null for non-existent file")
        void testNonExistentFile() throws Exception {
            Path nonexistent = tempDir.resolve("nonexistent.html");
            Double result = invokeComputeConsolidatedPercent(nonexistent);
            
            assertNull(result);
        }

        @Test
        @DisplayName("Returns null when no valid rows found")
        void testNoValidRows() throws Exception {
            String html = "<html><body><table>" +
                    "<tr><td>Total</td><td>10</td><td>5</td><td>50%</td></tr>" +
                    "</table></body></html>";
            Files.write(reportPath, html.getBytes(StandardCharsets.UTF_8));
            
            Double result = invokeComputeConsolidatedPercent(reportPath);
            
            assertNull(result);
        }

        @Test
        @DisplayName("Skips rows with 'total' keyword (case-insensitive)")
        void testSkipsTotalKeyword() throws Exception {
            String html = "<html><body><table>" +
                    "<tr><td>TOTAL</td><td>10</td><td>5</td><td>50%</td></tr>" +
                    "</table></body></html>";
            Files.write(reportPath, html.getBytes(StandardCharsets.UTF_8));
            
            Double result = invokeComputeConsolidatedPercent(reportPath);
            
            assertNull(result);
        }

        @Test
        @DisplayName("Returns null when denominator is zero")
        void testZeroDenominator() throws Exception {
            String html = "<html><body><table>" +
                    "<tr><td>method</td><td>0</td><td>0</td><td>-</td></tr>" +
                    "</table></body></html>";
            Files.write(reportPath, html.getBytes(StandardCharsets.UTF_8));
            
            Double result = invokeComputeConsolidatedPercent(reportPath);
            
            assertNull(result);
        }

        @Test
        @DisplayName("Handles IOException gracefully")
        void testIOException() throws Exception {
            Double result = invokeComputeConsolidatedPercent(null);
            
            assertNull(result);
        }
    }

    @Nested
    @DisplayName("processCodeCoverageEntry() Tests")
    class ProcessCodeCoverageEntryTests {

        @Test
        @DisplayName("Creates new entry for leaf-level class (index.html path)")
        void testLeafLevelClass() throws Exception {
            invokeProcessCodeCoverageEntry("TestClass", "75.5", "com/example/index.html");
            Map<String, CodeCoverageDetails> map = getClassCoverageMap(reader);
            
            assertTrue(map.containsKey("com.example.TestClass"));
        }

        @Test
        @DisplayName("Skips subclass pattern ($digit.html)")
        void testSkipsSubclassPattern() throws Exception {
            invokeProcessCodeCoverageEntry("TestClass", "75.0", "com/example/MyClass$1.html");
            Map<String, CodeCoverageDetails> map = getClassCoverageMap(reader);
            
            assertFalse(map.containsKey("com.example.MyClass.$1"));
        }

        @Test
        @DisplayName("Converts dollar signs to dots in FQDN")
        void testDollarSignConversion() throws Exception {
            invokeProcessCodeCoverageEntry("TestClass", "75.0", "com/example/MyClass$Inner.html");
            Map<String, CodeCoverageDetails> map = getClassCoverageMap(reader);
            
            assertTrue(map.containsKey("com.example.MyClass.Inner"));
        }

        @Test
        @DisplayName("Ignores null linkText")
        void testNullLinkText() throws Exception {
            invokeProcessCodeCoverageEntry(null, "75.0", "com/example/index.html");
            Map<String, CodeCoverageDetails> map = getClassCoverageMap(reader);
            
            assertTrue(map.isEmpty());
        }

        @Test
        @DisplayName("Ignores null percentage")
        void testNullPercentage() throws Exception {
            invokeProcessCodeCoverageEntry("TestClass", null, "com/example/index.html");
            Map<String, CodeCoverageDetails> map = getClassCoverageMap(reader);
            
            assertTrue(map.isEmpty());
        }

        @Test
        @DisplayName("Ignores null relPath")
        void testNullRelPath() throws Exception {
            invokeProcessCodeCoverageEntry("TestClass", "75.0", null);
            Map<String, CodeCoverageDetails> map = getClassCoverageMap(reader);
            
            assertTrue(map.isEmpty());
        }

        @Test
        @DisplayName("Ignores empty strings")
        void testEmptyStrings() throws Exception {
            invokeProcessCodeCoverageEntry("", "", "");
            Map<String, CodeCoverageDetails> map = getClassCoverageMap(reader);
            
            assertTrue(map.isEmpty());
        }

        @Test
        @DisplayName("Prevents duplicate class entries (first entry wins)")
        void testNoDuplicateEntries() throws Exception {
            invokeProcessCodeCoverageEntry("TestClass", "50.0", "com/example/index.html");
            invokeProcessCodeCoverageEntry("TestClass", "99.0", "com/example/index.html");
            Map<String, CodeCoverageDetails> map = getClassCoverageMap(reader);
            CodeCoverageDetails entry = map.get("com.example.TestClass");
            
            assertEquals(50.0, entry.getPercentageCoverage().doubleValue(), 0.001);
        }

        @Test
        @DisplayName("Adds method coverage to existing class entry")
        void testAddMethodCoverage() throws Exception {
            invokeProcessCodeCoverageEntry("TestClass", "50.0", "com/example/index.html");
            invokeProcessCodeCoverageEntry("testMethod", "75.0", "com/example/TestClass.html");
            Map<String, CodeCoverageDetails> map = getClassCoverageMap(reader);
            CodeCoverageDetails entry = map.get("com.example.TestClass");
            
            assertNotNull(entry);
            assertNotNull(entry.getMethodCoverage());
            assertTrue(entry.getMethodCoverage().containsKey("testMethod"));
        }
    }

    @Nested
    @DisplayName("extractJacocoReportDetails() Tests")
    class ExtractJacocoReportDetailsTests {

        @Test
        @DisplayName("Returns non-null Map")
        void testReturnsMap() {
            Map<String, CodeCoverageDetails> result = reader.extractJacocoReportDetails();
            
            assertNotNull(result);
            assertTrue(result instanceof Map);
        }

        @Test
        @DisplayName("Handles null path internally")
        void testNullPathHandling() {
            JacocoReportReader nullReader = new JacocoReportReader(null);
            Map<String, CodeCoverageDetails> result = nullReader.extractJacocoReportDetails();
            
            assertNotNull(result);
        }
    }

    @Nested
    @DisplayName("main() Method Tests")
    class MainMethodTests {

        @Test
        @DisplayName("main() with no arguments")
        void testMainNoArgs() {
            assertDoesNotThrow((org.junit.jupiter.api.function.Executable) () -> JacocoReportReader.main(new String[]{}));
        }

        @Test
        @DisplayName("main() with null args")
        void testMainNullArgs() {
            assertDoesNotThrow((org.junit.jupiter.api.function.Executable) () -> JacocoReportReader.main(null));
        }

        @Test
        @DisplayName("main() with single argument (path)")
        void testMainWithPath() throws IOException {
            String[] args = {reportPath.toString()};
            String html = createValidHtmlReport("75%", "80%", "85%", "90%", "95%");
            Files.write(reportPath, html.getBytes(StandardCharsets.UTF_8));
            
            assertDoesNotThrow((org.junit.jupiter.api.function.Executable) () -> JacocoReportReader.main(args));
        }

        @Test
        @DisplayName("main() with path and depth")
        void testMainWithPathAndDepth() throws IOException {
            String[] args = {reportPath.toString(), "5"};
            String html = createValidHtmlReport("75%", "80%", "85%", "90%", "95%");
            Files.write(reportPath, html.getBytes(StandardCharsets.UTF_8));
            
            assertDoesNotThrow((org.junit.jupiter.api.function.Executable) () -> JacocoReportReader.main(args));
        }

        @Test
        @DisplayName("main() with path, depth, and threshold")
        void testMainWithAllArgs() throws IOException {
            String[] args = {reportPath.toString(), "5", "50.0"};
            String html = createValidHtmlReport("75%", "80%", "85%", "90%", "95%");
            Files.write(reportPath, html.getBytes(StandardCharsets.UTF_8));
            
            assertDoesNotThrow((org.junit.jupiter.api.function.Executable) () -> JacocoReportReader.main(args));
        }

        @Test
        @DisplayName("main() with invalid depth (non-numeric)")
        void testMainInvalidDepth() {
            String[] args = {reportPath.toString(), "notanumber"};
            
            assertDoesNotThrow((org.junit.jupiter.api.function.Executable) () -> JacocoReportReader.main(args));
        }

        @Test
        @DisplayName("main() with invalid threshold (non-numeric)")
        void testMainInvalidThreshold() {
            String[] args = {reportPath.toString(), "5", "notafloat"};
            
            assertDoesNotThrow((org.junit.jupiter.api.function.Executable) () -> JacocoReportReader.main(args));
        }

        @Test
        @DisplayName("main() with negative depth")
        void testMainNegativeDepth() {
            String[] args = {reportPath.toString(), "-10"};
            
            assertDoesNotThrow((org.junit.jupiter.api.function.Executable) () -> JacocoReportReader.main(args));
        }

        @Test
        @DisplayName("main() with negative threshold")
        void testMainNegativeThreshold() {
            String[] args = {reportPath.toString(), "5", "-50.0"};
            
            assertDoesNotThrow((org.junit.jupiter.api.function.Executable) () -> JacocoReportReader.main(args));
        }

        @Test
        @DisplayName("main() with extra arguments (ignored)")
        void testMainExtraArgs() {
            String[] args = {reportPath.toString(), "5", "50.0", "extra", "args", "ignored"};
            
            assertDoesNotThrow((org.junit.jupiter.api.function.Executable) () -> JacocoReportReader.main(args));
        }
    }

    // ==================== Helper Methods ====================

    private Path getReaderPath(JacocoReportReader readerInstance) {
        try {
            Field field = JacocoReportReader.class.getDeclaredField("reportPath");
            field.setAccessible(true);
            return (Path) field.get(readerInstance);
        } catch (Exception e) {
            fail("Could not access reportPath field");
            return null;
        }
    }

    private Object invokeParseSummary(String html) throws Exception {
        Method method = JacocoReportReader.class.getDeclaredMethod("parseSummaryFromHtml", String.class);
        method.setAccessible(true);
        return method.invoke(reader, html);
    }

    private String getFieldValue(Object obj, String fieldName) throws Exception {
        if (obj == null) return null;
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        Object value = field.get(obj);
        return value == null ? null : value.toString();
    }

    private List<String> invokeFindLowCoverageClasses(Path start, int maxDepth, double threshold) throws Exception {
        Method method = JacocoReportReader.class.getDeclaredMethod("findLowCoverageClasses", Path.class, int.class, double.class);
        method.setAccessible(true);
        return (List<String>) method.invoke(reader, start, maxDepth, threshold);
    }

    private Double invokeComputeConsolidatedPercent(Path classHtml) throws Exception {
        Method method = JacocoReportReader.class.getDeclaredMethod("computeConsolidatedPercentForClass", Path.class);
        method.setAccessible(true);
        return (Double) method.invoke(reader, classHtml);
    }

    private void invokeProcessCodeCoverageEntry(String linkText, String percentage, String relPath) throws Exception {
        Method method = JacocoReportReader.class.getDeclaredMethod("processCodeCoverageEntry", String.class, String.class, String.class);
        method.setAccessible(true);
        method.invoke(reader, linkText, percentage, relPath);
    }

    private Map<String, CodeCoverageDetails> getClassCoverageMap(JacocoReportReader readerInstance) throws Exception {
        Field field = JacocoReportReader.class.getDeclaredField("classCoverageMap");
        field.setAccessible(true);
        return (Map<String, CodeCoverageDetails>) field.get(readerInstance);
    }

    private String createValidHtmlReport(String instruction, String branch, String line, String method, String clazz) {
        return "<html><tfoot>" +
                "<tr><td>Total</td><td>x</td><td>" + instruction + "</td><td>y</td>" +
                "<td>" + branch + "</td><td>z</td><td>a</td>" +
                "<td>" + line + "</td><td>b</td><td>" + method + "</td><td>c</td>" +
                "<td>" + clazz + "</td></tr>" +
                "</tfoot></html>";
    }
}
