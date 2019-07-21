package com.bss.app.rule.questions.service;

import com.bss.app.rule.engine.dto.*;
import com.bss.app.rule.engine.service.RuleEngineService;
import com.bss.app.rule.questions.cache.QuestionnaireCache;
import com.bss.app.rule.questions.loader.QuestionsLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

@Service
public class QuestionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionService.class);
    @Autowired
    private QuestionnaireCache questionnaireCache;
    @Autowired
    private QuestionsLoader questionsLoader;
    @Autowired
    private RuleEngineService ruleEngineService;
    @Value("${cloud.advisory.user.question.fetch.size}")
    private Integer fetchSize;

    /**
     * /Below steps are being followed;
     * <li>1.  </li>
     *
     * @param userId
     * @param answers
     * @return
     */
    public QuestionResponse getNextQuestions(final String userId, final List<Answer> answers, final String requestId) {
        LOGGER.info("Started getting next questions for user: {}, current answers: {}, request-id:{}", userId, answers, requestId);
        QuestionResponse response = null;
        List<Question> nextQuestions = null;
        //Save answers first
        if (!CollectionUtils.isEmpty(answers)) {
            questionnaireCache.saveAnswersForUser(userId, answers);
        }
        //get next set of questions
        nextQuestions = questionsLoader.getNextQuestions(fetchSize, questionnaireCache.getCurrentQnsNo(userId));
        if (!CollectionUtils.isEmpty(nextQuestions)) {
            response = new QuestionResponse();
            response.setQuestions(nextQuestions);
            response.setOriginalRequestId(requestId);
        }

        return response;
    }

    public List<String> getSolutionForUserContext(final String userId) {
        //create actual user context to be used for firing against rules in cache
        List<Answer> answers = questionnaireCache.removeAnswersForUser(userId);
        RuleContext context = createRuleContextFromAnswers(answers);
        List<String> solutions = ruleEngineService.processRequest(context);

        return solutions;
    }

    private RuleContext createRuleContextFromAnswers(final List<Answer> answers) {
        RuleContext ruleContext = null;
        Map<String, String> qnsColumnMap = questionsLoader.getQnsColumnMap();
        if (!CollectionUtils.isEmpty(answers)) {
            ruleContext = new RuleContext();
            for (Answer a : answers) {
                gatherContext(a, ruleContext);
            }
        }

        return ruleContext;
    }

    private void gatherContext(final Answer answer, final RuleContext ruleContext) {
        String qustionId = answer.getQustionId();
        String columnName = questionsLoader.getQnsColumnMap().get(qustionId);
        String value = answer.getAnswer();
        Column column = new Column(columnName, value);
        ruleContext.addColumn(column);
        List<Answer> nestedAnswers = answer.getNestedAnswers();
        if (!CollectionUtils.isEmpty(nestedAnswers)) {
            for (Answer a : nestedAnswers) {
                //recursive loop
                gatherContext(a, ruleContext);
            }
        }
    }

}