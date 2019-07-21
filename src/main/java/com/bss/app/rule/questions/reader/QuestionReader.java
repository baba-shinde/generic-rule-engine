package com.bss.app.rule.questions.reader;

import com.bss.app.rule.engine.dto.Question;
import com.bss.app.rule.engine.dto.QuestionCategory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@Service
public class QuestionReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionReader.class);

    public List<Question> readQuestionsFromInputStream(final String path) throws IOException {
        FileInputStream inputStream = new FileInputStream(new File(path));
        return readQuestionsFromInputStream(inputStream);
    }

    public List<Question> readQuestionsFromInputStream(final FileInputStream inputStream) throws IOException {
        List<Question> questions = null;
        LOGGER.info("Started reading questions from inputStream");
        try {
            //Create Workbook instance holding reference to .xlsx file
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

            //Get first/desired sheet from the workbook
            XSSFSheet sheet = workbook.getSheetAt(0);

            //Iterate through each rows one by one
            Iterator<Row> rowIterator = sheet.iterator();

            //Assume first row to be column names
            Row columnRow = rowIterator.next();
            List<String> columns = getColumnsFromExcelSheet(columnRow);
            questions = new ArrayList<>();

            while (rowIterator.hasNext()) {
                Question question = populateQuestion(rowIterator);
                //rule.setId(columnList.get(0).getValue().toString());
                questions.add(question);
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
        List<Question> outQuestions = enrichQuestions(questions);
        LOGGER.info("Finished reading questions from inputStream, questions: {}", questions);

        return outQuestions;
    }

    private List<Question> enrichQuestions(final List<Question> inQuestions) {
        Map<String, Question> outQuestions = null;
        if (!CollectionUtils.isEmpty(inQuestions)) {
            outQuestions = new LinkedHashMap<>();
            for (Question q : inQuestions) {
                if (StringUtils.hasText(q.getDependsOnQuestion())) {
                    String parentId = q.getDependsOnQuestion();
                    Question parent = inQuestions.get(Integer.parseInt(parentId) - 1);

                    //Set OptionValue
                    String rawOptions = parent.getRawOptions();
                    if (StringUtils.hasText(rawOptions)) {
                        String[] split = rawOptions.split(parent.getQuestionCategory().getDelimiter());
                        List<String> parentOptions = Arrays.asList(split);
                        for (String o : parentOptions) {
                            if (o.equalsIgnoreCase(q.getDependsOnOption())) {
                                parent.addQuestionMapping(o, q);
                                break;
                            }
                        }
                        //setting own options
                        q.setOptions(Arrays.asList(q.getRawOptions().split(q.getQuestionCategory().getDelimiter())));
                    }
                } else {
                    String rawOptions = q.getRawOptions();
                    if (StringUtils.hasText(rawOptions)) {
                        q.setOptions(Arrays.asList(rawOptions.split(q.getQuestionCategory().getDelimiter())));
                    }
                    outQuestions.put(q.getId(), q);
                }
            }

        }
        ArrayList<Question> ret = new ArrayList(outQuestions.values());

        return ret;
    }

    private Question populateQuestion(Iterator<Row> rowIterator) {
        Question question = new Question();
        Row row = rowIterator.next();
        //For each row, iterate through all the columns
        try {
            question.setId(getStringValue(row.getCell(0)));
            question.setText(getStringValue(row.getCell(1)));

            Cell type = row.getCell(2);
            String typeValue = getStringValue(type);
            QuestionCategory questionCategory = QuestionCategory.valueOf(typeValue);
            question.setQuestionCategory(questionCategory);

            String options = getStringValue(row.getCell(3));
            question.setRawOptions(options);

            question.setDependsOnQuestion(getStringValue(row.getCell(4)));
            question.setDependsOnOption(getStringValue(row.getCell(5)));
            question.setAssociatedColumnInRules(getStringValue(row.getCell(6)));
        } catch (EnumConstantNotPresentException ecpe) {
            LOGGER.error("Error occurred while reading excel file", ecpe);
        }

        return question;
    }

    private String getStringValue(final Cell cell) {
        String ret = null;
        CellType cellType = cell.getCellType();
        switch (cellType) {
            case BOOLEAN:
                ret = String.valueOf(cell.getBooleanCellValue());
                break;
            case NUMERIC:
                Double numericCellValue = cell.getNumericCellValue();
                ret = String.valueOf(numericCellValue.intValue());
                break;
            case STRING:
                ret = String.valueOf(cell.getStringCellValue());
                break;
            case BLANK:
                ret = null;
                break;
        }

        return ret;
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
        QuestionReader reader = new QuestionReader();
        FileInputStream inputStream = new FileInputStream(new File("E:\\work\\codebase\\rule-engine\\data\\questions.xlsx"));
        List<Question> questions = reader.readQuestionsFromInputStream(inputStream);
        LOGGER.info("Questions Loaded: {}", questions);
    }
}