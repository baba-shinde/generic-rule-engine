package com.bss.app.rule.engine.reader;

import com.bss.app.rule.engine.dto.Column;
import com.bss.app.rule.engine.dto.ColumnCategory;
import com.bss.app.rule.engine.dto.Rule;
import com.bss.app.rule.engine.dto.Tuple2;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import org.mockito.Mockito;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class ExcelFileReaderTest {

    @Test
    public void test_getColumnsFromExcelSheet_withNullRow_returnsNull() throws Exception {
        ExcelFileReader reader = new ExcelFileReader();
        Method m = ExcelFileReader.class.getDeclaredMethod("getColumnsFromExcelSheet", Row.class);
        m.setAccessible(true);

        Object result = m.invoke(reader, new Object[]{null});
        assertNull(result);
    }

    @Test
    public void test_getColumnsFromExcelSheet_withHeader_returnsList() throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("s");
        XSSFRow header = sheet.createRow(0);
        header.createCell(0).setCellValue("A");
        header.createCell(1).setCellValue("B");

        ExcelFileReader reader = new ExcelFileReader();
        Method m = ExcelFileReader.class.getDeclaredMethod("getColumnsFromExcelSheet", Row.class);
        m.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<String> cols = (List<String>) m.invoke(reader, header);
        assertNotNull(cols);
        assertEquals(Arrays.asList("A", "B"), cols);
    }

    @Test
    public void test_getColumn_variousCategories() throws Exception {
        ExcelFileReader reader = new ExcelFileReader();
        Method m = ExcelFileReader.class.getDeclaredMethod("getColumn", List.class, int.class, Cell.class);
        m.setAccessible(true);

        List<String> cols = Arrays.asList("Rule-Id", "MyAssessment", "NumCol", "StrCol", "BlankCol", "ErrCol", "EmptyName");

        Cell idCell = Mockito.mock(Cell.class);
        when(idCell.getStringCellValue()).thenReturn("ID123");
        Tuple2<?, ?> t1 = (Tuple2<?, ?>) m.invoke(reader, cols, 0, idCell);
        assertEquals(ColumnCategory.ID, t1.getF1());
        assertEquals(new Column("Rule-Id", "ID123"), t1.getF2());

        // Suggestion (contains Assessment)
        Cell sugCell = Mockito.mock(Cell.class);
        when(sugCell.getStringCellValue()).thenReturn("SUG");
        Tuple2<?, ?> t2 = (Tuple2<?, ?>) m.invoke(reader, cols, 1, sugCell);
        assertEquals(ColumnCategory.SUGGESTION, t2.getF1());
        assertEquals(new Column("MyAssessment", "SUG"), t2.getF2());

        // Regular - STRING
        Cell strCell = Mockito.mock(Cell.class);
        when(strCell.getCellType()).thenReturn(CellType.STRING);
        when(strCell.getStringCellValue()).thenReturn("hello");
        Tuple2<?, ?> t3 = (Tuple2<?, ?>) m.invoke(reader, cols, 3, strCell);
        assertEquals(ColumnCategory.REGULAR_COLUMN, t3.getF1());
        assertEquals(new Column("StrCol", "hello"), t3.getF2());

        // Regular - BLANK
        Cell blankCell = Mockito.mock(Cell.class);
        when(blankCell.getCellType()).thenReturn(CellType.BLANK);
        Tuple2<?, ?> t4 = (Tuple2<?, ?>) m.invoke(reader, cols, 4, blankCell);
        assertEquals(ColumnCategory.REGULAR_COLUMN, t4.getF1());
        assertEquals(new Column("BlankCol", null), t4.getF2());

        // Regular - ERROR
        Cell errCell = Mockito.mock(Cell.class);
        when(errCell.getCellType()).thenReturn(CellType.ERROR);
        Tuple2<?, ?> t5 = (Tuple2<?, ?>) m.invoke(reader, cols, 5, errCell);
        assertEquals(ColumnCategory.REGULAR_COLUMN, t5.getF1());
        // For ERROR case the code does not set column; expect null
        assertNull(t5.getF2());

        // EMPTY - cell is null
        Tuple2<?, ?> t6 = (Tuple2<?, ?>) m.invoke(reader, cols, 6, (Object) null);
        assertEquals(ColumnCategory.EMPTY, t6.getF1());
        assertEquals(new Column("EmptyName", null), t6.getF2());

        // Column name empty string -> no category
        List<String> cols2 = Arrays.asList("");
        Cell any = Mockito.mock(Cell.class);
        Tuple2<?, ?> t7 = (Tuple2<?, ?>) m.invoke(reader, cols2, 0, any);
        assertNull(t7.getF1());
        assertNull(t7.getF2());
    }

    @Test
    public void test_readRulesFromInputStream_and_readRulesFromFile_success() throws Exception {
        Path tempDir = Files.createTempDirectory("er");
        // Create workbook with header and one data row
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("sheet1");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Rule-Id");
        header.createCell(1).setCellValue("Assessment Column");
        header.createCell(2).setCellValue("RegularCol");
        header.createCell(3).setCellValue("EmptyCol");

        Row data = sheet.createRow(1);
        data.createCell(0).setCellValue("RID1");
        data.createCell(1).setCellValue("S1");
        data.createCell(2).setCellValue("RVAL");
        // don't create cell 3 to simulate null -> EMPTY

        File f = tempDir.resolve("test.xlsx").toFile();
        try (FileOutputStream fos = new FileOutputStream(f)) {
            workbook.write(fos);
        }

        ExcelFileReader reader = new ExcelFileReader();
        // use readRulesFromFile
        List<Rule> rules = reader.readRulesFromFile(f.getAbsolutePath());
        assertNotNull(rules);
        assertEquals(1, rules.size());
        Rule r = rules.get(0);
        assertEquals("RID1", r.getId());
        assertNotNull(r.getSuggestions());
        assertEquals(1, r.getSuggestions().size());
        assertEquals("S1", r.getSuggestions().get(0));
        assertNotNull(r.getColumns());
        assertEquals(new Column("RegularCol", "RVAL"), r.getColumns().get("RegularCol"));

        // Also test readRulesFromInputStream directly using FileInputStream
        try (FileInputStream fis = new FileInputStream(f)) {
            List<Rule> rules2 = reader.readRulesFromInputStream(reader, fis);
            assertNotNull(rules2);
            assertEquals(1, rules2.size());
            assertEquals(r, rules2.get(0));
        }
    }

    @Test
    public void test_readRulesFromInputStream_throwsIOException_for_invalid_file() throws Exception {
        Path tempDir = Files.createTempDirectory("er");
        // create a plain text file which is not a valid xlsx
        File f = tempDir.resolve("bad.txt").toFile();
        try (FileWriter fw = new FileWriter(f)) {
            fw.write("not an xlsx");
        }

        ExcelFileReader reader = new ExcelFileReader();
        try (FileInputStream fis = new FileInputStream(f)) {
            assertThrows(Exception.class, () -> reader.readRulesFromInputStream(reader, fis));
        }
    }
}
