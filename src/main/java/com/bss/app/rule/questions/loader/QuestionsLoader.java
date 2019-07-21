package com.bss.app.rule.questions.loader;

import com.bss.app.rule.engine.dto.Question;
import com.bss.app.rule.engine.exception.RuleEngineException;
import com.bss.app.rule.questions.reader.QuestionReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QuestionsLoader implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionsLoader.class);
    @Autowired
    private QuestionReader questionReader;
    private List<Question> questions;
    @Value("${cloud.advisory.question.loader.file.path}")
    private String questionsPath;
    private Map<String, String> qnsColumnMap = null;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.questions = questionReader.readQuestionsFromInputStream(questionsPath);
        populateQnsColumnMap();
        LOGGER.info("Questions Loaded: {}", questions);
    }

    public void loadQuestions(final FileInputStream inputStream) throws RuleEngineException {
        LOGGER.info("Started loading questions from source");
        List<Question> newQuestions = null;
        try {
            newQuestions = questionReader.readQuestionsFromInputStream(questionsPath);
            if (!CollectionUtils.isEmpty(newQuestions)) {
                this.questions = newQuestions;
                populateQnsColumnMap();
            }
            LOGGER.info("Finished loading questions from source, no of questions loaded: {}", this.questions.size());
        } catch (IOException e) {
            throw new RuleEngineException("Exception occurred while loading questions from source", e);
        }
    }

    private void populateQnsColumnMap() {
        Map<String, String> newMap = null;
        if (!CollectionUtils.isEmpty(this.questions)) {
            newMap = this.questions.stream().collect(Collectors.toMap(Question::getId, Question::getAssociatedColumnInRules));
            if (newMap != null) {
                this.qnsColumnMap = newMap;
            }
        }
    }

    public Map<String, String> getQnsColumnMap() {
        return this.qnsColumnMap;
    }

    public List<Question> getAllQuestions() {
        return this.questions;
    }

    public List<Question> getNextQuestions(final Integer number, final Integer currentNo) {
        List<Question> ret = null;
        if ((number + currentNo) > this.questions.size()) {
            ret = this.questions.subList(currentNo, questions.size());
        } else if (this.questions.size() > (number + currentNo)) {
            ret = this.questions.subList(currentNo, currentNo + number);
        }

        return ret;
    }

/*    public static void main(String[] args) {
        QuestionsLoader loader = new QuestionsLoader();
        List<Question> questions = new ArrayList<>();
        loader.setQuestions(questions);
        for (int i=0; i<8; i++) {
            Question question = new Question();
            question.setId(""+(i+1));
            questions.add(question);
        }
        loader.getNextQuestions(3, 0);
        loader.getNextQuestions(3,3);
        loader.getNextQuestions(3,6);


        System.out.println("Done");

    }*/

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}