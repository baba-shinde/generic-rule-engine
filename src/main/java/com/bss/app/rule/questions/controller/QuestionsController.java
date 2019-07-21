package com.bss.app.rule.questions.controller;

import com.bss.app.rule.engine.dto.AnswerResponse;
import com.bss.app.rule.engine.dto.QuestionResponse;
import com.bss.app.rule.questions.service.QuestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/cloud-advisory/questions")
public class QuestionsController {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionsController.class);
    @Autowired
    private QuestionService questionService;


    @RequestMapping(value = "/interract", method = RequestMethod.POST)
    public QuestionResponse exchangeQuestionAnswer(
            @RequestHeader(name = "userId") final String userId,
            @RequestBody AnswerResponse answerResponse) {
        LOGGER.info("Started submitting answers and getting new questions, answers: {}", answerResponse);
        QuestionResponse nextQuestions = questionService.getNextQuestions(userId, answerResponse.getAnswers(), answerResponse.getRequestId());

        LOGGER.info("Finished submitting answers and getting new questions: {}", nextQuestions);

        return nextQuestions;
    }

    @RequestMapping(value = "/assessment", method = RequestMethod.GET)
    public List<String> getAssessmentForSubmittedQuestions(@RequestHeader(name = "userId") final String userId) {
        LOGGER.info("Started getting assessment details for submitted questions for user: {}", userId);
        List<String> assessmentDetails = questionService.getSolutionForUserContext(userId);
        LOGGER.info("Finished getting assessment details for submitted questions for user: {}, details: {}", userId, assessmentDetails);

        return assessmentDetails;
    }
}