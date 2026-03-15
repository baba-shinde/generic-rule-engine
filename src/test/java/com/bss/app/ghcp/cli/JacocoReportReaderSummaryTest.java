package com.bss.app.ghcp.cli;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class JacocoReportReaderSummaryTest {

    private Object invokeParse(String html) throws Exception {
        Method m = JacocoReportReader.class.getDeclaredMethod("parseSummaryFromHtml", String.class);
        m.setAccessible(true);
        return m.invoke(new JacocoReportReader(), html);
    }

    private String getFieldStr(Object summary, String name) throws Exception {
        if (summary == null) return null;
        Class<?> cls = summary.getClass();
        Field f = cls.getDeclaredField(name);
        f.setAccessible(true);
        Object v = f.get(summary);
        return v == null ? null : v.toString();
    }

    @Test
    void parseSummary_noTfoot_returnsNull() throws Exception {
        String html = "<html><body><p>No tfoot here</p></body></html>";
        Object s = invokeParse(html);
        assertNull(s);
    }

    @Test
    void parseSummary_insufficientTd_returnsNull() throws Exception {
        String html = "<html><tfoot>" +
                "<td>0</td><td>1</td><td>2</td><td>3</td><td>4</td><td>5</td>" +
                "</tfoot></html>";
        Object s = invokeParse(html);
        assertNull(s);
    }

    @Test
    void parseSummary_parsesCorrectly_stripsTagsAndNbsp() throws Exception {
        // build tfoot with 13 td cells; important indexes: 2,4,8,10,12
        String[] cells = new String[13];
        for (int i = 0; i < cells.length; i++) cells[i] = "X" + i;
        cells[2] = "<span>75%</span>";
        cells[4] = "<b>80%</b>";
        cells[8] = "<div>85%&nbsp;</div>";
        cells[10] = "90%";
        cells[12] = "<em>95%</em>";

        StringBuilder tfoot = new StringBuilder("<tfoot>");
        for (String c : cells) {
            tfoot.append("<td>").append(c).append("</td>");
        }
        tfoot.append("</tfoot>");

        String html = "<html>" + tfoot.toString() + "</html>";

        Object s = invokeParse(html);
        assertNotNull(s);
        assertEquals("75%", getFieldStr(s, "instructionCoverage"));
        assertEquals("80%", getFieldStr(s, "branchCoverage"));
        assertEquals("85%", getFieldStr(s, "lineCoverage"));
        assertEquals("90%", getFieldStr(s, "methodCoverage"));
        assertEquals("95%", getFieldStr(s, "classCoverage"));
    }

    @Test
    void parseSummary_caseInsensitiveAndWhitespace() throws Exception {
        // uppercase TFOOT and extra whitespace inside td
        StringBuilder tfoot = new StringBuilder("<TFOOT>");
        for (int i = 0; i < 13; i++) {
            if (i == 2) tfoot.append("<TD>  1  % </TD>");
            else if (i == 4) tfoot.append("<td> <span>2%</span> </td>");
            else if (i == 8) tfoot.append("<td>&nbsp;3%&nbsp;</td>");
            else if (i == 10) tfoot.append("<td>4%</td>");
            else if (i == 12) tfoot.append("<td>5%</td>");
            else tfoot.append("<td>z</td>");
        }
        tfoot.append("</TFOOT>");
        String html = "<html>" + tfoot.toString() + "</html>";

        Object s = invokeParse(html);
        assertNotNull(s);
        // whitespace trimming should keep the percent tokens as-is (leading/trailing spaces removed)
        assertEquals("1  %", getFieldStr(s, "instructionCoverage"));
        assertEquals("2%", getFieldStr(s, "branchCoverage"));
        assertEquals("3%", getFieldStr(s, "lineCoverage"));
        assertEquals("4%", getFieldStr(s, "methodCoverage"));
        assertEquals("5%", getFieldStr(s, "classCoverage"));
    }
}
