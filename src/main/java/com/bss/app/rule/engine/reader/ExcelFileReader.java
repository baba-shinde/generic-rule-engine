package com.bss.app.rule.engine.reader;

import com.bss.app.rule.engine.dto.Column;
import com.bss.app.rule.engine.dto.ColumnCategory;
import com.bss.app.rule.engine.dto.Rule;
import com.bss.app.rule.engine.dto.Tuple2;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ExcelFileReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelFileReader.class);

    public List<Rule> readRulesFromInputStream(ExcelFileReader excelFileReader, final FileInputStream inputStream) throws IOException {
        List<Rule> rules = null;
        LOGGER.info("Started reading rules from inputStream");
        try {
            //Create Workbook instance holding reference to .xlsx file
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

            //Get first/desired sheet from the workbook
            XSSFSheet sheet = workbook.getSheetAt(0);

            //Iterate through each rows one by one
            Iterator<Row> rowIterator = sheet.iterator();

            //Assume first row to be column names
            Row columnRow = rowIterator.next();
            List<String> columns = excelFileReader.getColumnsFromExcelSheet(columnRow);
            rules = new ArrayList<>();

            while (rowIterator.hasNext()) {
                Rule rule = new Rule();
                Row row = rowIterator.next();
                //For each row, iterate through all the columns
                try {
                    for (int i = 0; i < columns.size(); i++) {
                        Cell cell = row.getCell(i);
                        //Check the cell type and format accordingly
                        Tuple2<ColumnCategory, Column> tuple2 = getColumn(columns, i, cell);
                        if (tuple2.getF1().equals(ColumnCategory.ID)) {
                            rule.setId(tuple2.getF2().getValue().toString());
                        } else if (tuple2.getF1().equals(ColumnCategory.SUGGESTION)) {
                            rule.addSuggestion(tuple2.getF2().getValue().toString());
                        } else {
                            rule.addColumn(tuple2.getF2());
                        }
                    }
                } catch (EnumConstantNotPresentException ecpe) {
                    LOGGER.error("Error occurred while reading excel file", ecpe);
                }
                //rule.setId(columnList.get(0).getValue().toString());
                rules.add(rule);
            }
        } catch (IOException e) {
            LOGGER.error("Error occurred while reading excel file", e);
            throw e;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ioe) {
                    LOGGER.error("Error occurred while closing the file handler", ioe);
                }
            }
        }
        LOGGER.info("Finished reading rules from inputStream, rules: {}", rules);

        return rules;
    }

    private Tuple2<ColumnCategory, Column> getColumn(final List<String> columns, int cellNo, final Cell cell) {
        Column column = null;
        ColumnCategory category = null;
        String columnName = columns.get(cellNo);
        if (StringUtils.hasText(columnName)) {
            if (cell == null) {
                column = new Column(columnName, null);
                category = ColumnCategory.EMPTY;
            } else if (columnName.equalsIgnoreCase("Rule-Id")) {
                category = ColumnCategory.ID;
                column = new Column(columnName, cell.getStringCellValue());
            } else if (columnName.contains("Assessment")) {
                category = ColumnCategory.SUGGESTION;
                column = new Column(columnName, cell.getStringCellValue());
            } else {
                category = ColumnCategory.REGULAR_COLUMN;
                switch (cell.getCellType()) {
                    case _NONE:
                        column = new Column(columnName, null);
                        break;
                    case BLANK:
                        column = new Column(columnName, null);
                        break;
                    case NUMERIC:
                        column = new Column(columnName, cell.getStringCellValue());
                        break;
                    case STRING:
                        column = new Column(columnName, cell.getStringCellValue());
                        break;
                    case BOOLEAN:
                        column = new Column(columnName, cell.getStringCellValue());
                        break;
                    case ERROR:
                        //throw new RuntimeException("Unexpected error occurred while reading excel sheet cell: " + cell);
                        break;
                }
            }
        }

        return new Tuple2<>(category, column);
    }

    //"/mnt/sda5/work/codebase/rule-engine/data/input-file.xlsx"
    public List<Rule> readRulesFromFile(final String filePath) throws IOException {
        LOGGER.info("Started reading rules from file: {}", filePath);
        List<Rule> rules = null;
        if (StringUtils.hasText(filePath)) {
            FileInputStream inputStream = new FileInputStream(new File(filePath));
            rules = readRulesFromInputStream(this, inputStream);
        }

        LOGGER.info("Finished reading rules from file: {}", rules);
        return rules;
    }

    private List<String> getColumnsFromExcelSheet(final Row row) {
        LOGGER.info("Started reading all the columns from row: {}", row);
        List<String> columns = null;
        if (row != null) {
            columns = new ArrayList<>();
            Iterator<Cell> cellIterator = row.cellIterator();
            try {
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    columns.add(cell.getStringCellValue());
                }
            } catch (EnumConstantNotPresentException ecpe) {
                LOGGER.error("Error occurred while reading excel file", ecpe);
            }

        }


        LOGGER.info("Finished reading all the columns from row: {}, columns: {}", row, columns);

        return columns;
    }

    public static void main(String[] args) throws IOException {
        ExcelFileReader reader = new ExcelFileReader();
        List<Rule> rules = reader.readRulesFromFile("/mnt/sda5/work/codebase/rule-engine/data/input-file.xlsx");
        LOGGER.info("Rules Loaded: {}", rules);
    }
}