package com.bss.app.rule.questions.reader;

import com.bss.app.rule.engine.dto.Question;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class QuestionReaderTest {

    private File createParentChildWorkbook() throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("sheet1");

        // header
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("id");
        header.createCell(1).setCellValue("text");
        header.createCell(2).setCellValue("type");
        header.createCell(3).setCellValue("rawOptions");
        header.createCell(4).setCellType(CellType.BLANK);
        header.createCell(5).setCellType(CellType.BLANK);
        header.createCell(6).setCellValue("assoc");

        // parent row (index 1)
        Row p = sheet.createRow(1);
        p.createCell(0).setCellValue(1); // numeric -> NUMERIC branch
        p.createCell(1).setCellValue("parent");
        p.createCell(2).setCellValue("ONE_IN_MANY");
        p.createCell(3).setCellValue("opt1#opt2");
        p.createCell(4).setCellType(CellType.BLANK);
        p.createCell(5).setCellType(CellType.BLANK);
        // boolean cell to exercise BOOLEAN branch when reading associatedColumnInRules
        p.createCell(6).setCellValue(true);

        // child row (index 2) - depends on question 1 option opt2
        Row c = sheet.createRow(2);
        c.createCell(0).setCellValue(2); // numeric
        c.createCell(1).setCellValue("child");
        c.createCell(2).setCellValue("ONE_IN_MANY");
        c.createCell(3).setCellValue("c1#c2");
        c.createCell(4).setCellValue("1");
        c.createCell(5).setCellValue("opt2");
        c.createCell(6).setCellValue("col");

        File tmp = File.createTempFile("questions", ".xlsx");
        try (FileOutputStream fos = new FileOutputStream(tmp)) {
            workbook.write(fos);
        }
        workbook.close();
        tmp.deleteOnExit();
        return tmp;
    }

    private File createHeaderOnlyWorkbook() throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("sheet1");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("id");
        header.createCell(1).setCellValue("text");
        header.createCell(2).setCellValue("type");
        header.createCell(3).setCellValue("rawOptions");

        File tmp = File.createTempFile("questions_empty", ".xlsx");
        try (FileOutputStream fos = new FileOutputStream(tmp)) {
            workbook.write(fos);
        }
        workbook.close();
        tmp.deleteOnExit();
        return tmp;
    }

    @Test
    public void testReadQuestionsParentChildMapping() throws IOException {
        QuestionReader reader = new QuestionReader();
        File f = createParentChildWorkbook();
        try (FileInputStream fis = new FileInputStream(f)) {
            List<Question> out = reader.readQuestionsFromInputStream(fis);
            // Only parent should be present in the final list (non-dependent questions)
            Assertions.assertNotNull(out);
            Assertions.assertEquals(1, out.size());
            Question parent = out.get(0);
            Assertions.assertEquals("1", parent.getId());
            // parent should have mapping for opt2 to child question
            Assertions.assertNotNull(parent.getQuestionMap());
            Question mapped = parent.getQuestionMap().get("opt2");
            Assertions.assertNotNull(mapped);
            Assertions.assertEquals("2", mapped.getId());
            // child's options should be split from rawOptions
            Assertions.assertNotNull(mapped.getOptions());
            Assertions.assertEquals(2, mapped.getOptions().size());
            Assertions.assertEquals("c1", mapped.getOptions().get(0));
            Assertions.assertEquals("c2", mapped.getOptions().get(1));
        }
    }

    @Test
    public void testReadQuestionsHeaderOnlyThrowsNPE() throws IOException {
        QuestionReader reader = new QuestionReader();
        File f = createHeaderOnlyWorkbook();
        try (FileInputStream fis = new FileInputStream(f)) {
            Assertions.assertThrows(NullPointerException.class, () -> reader.readQuestionsFromInputStream(fis));
        }
    }

    @Test
    public void testReadQuestionsFromPathNotFound() {
        QuestionReader reader = new QuestionReader();
        Assertions.assertThrows(IOException.class, () -> reader.readQuestionsFromInputStream("nonexistent_file_12345.xlsx"));
    }
}
