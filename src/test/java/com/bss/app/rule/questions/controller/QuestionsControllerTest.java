package com.bss.app.rule.questions.controller;

import com.bss.app.rule.engine.dto.*;
import com.bss.app.rule.questions.service.QuestionService;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QuestionsControllerTest {

    private final QuestionsController controller = new QuestionsController();

    private void injectQuestionService(QuestionService mock) throws Exception {
        Field f = QuestionsController.class.getDeclaredField("questionService");
        f.setAccessible(true);
        f.set(controller, mock);
    }

    @Test
    void exchangeQuestionAnswer_returnsQuestionResponse() throws Exception {
        QuestionService mockService = mock(QuestionService.class);

        AnswerResponse answerResponse = new AnswerResponse();
        answerResponse.setRequestId("req-1");
        Answer answer = new Answer();
        answer.setQustionId("q1");
        answer.setAnswer("a1");
        answerResponse.setAnswers(Collections.singletonList(answer));

        QuestionResponse expected = new QuestionResponse();
        expected.setOriginalRequestId("req-1");
        Question q = new Question();
        q.setId("q-2");
        expected.setQuestions(Collections.singletonList(q));

        when(mockService.getNextQuestions("user-1", answerResponse.getAnswers(), "req-1")).thenReturn(expected);

        injectQuestionService(mockService);

        QuestionResponse actual = controller.exchangeQuestionAnswer("user-1", answerResponse);

        assertSame(expected, actual);
        verify(mockService).getNextQuestions("user-1", answerResponse.getAnswers(), "req-1");
    }

    @Test
    void exchangeQuestionAnswer_returnsNullWhenServiceReturnsNull() throws Exception {
        QuestionService mockService = mock(QuestionService.class);
        AnswerResponse answerResponse = new AnswerResponse();
        answerResponse.setRequestId(null);
        answerResponse.setAnswers(null);

        when(mockService.getNextQuestions("user-2", null, null)).thenReturn(null);

        injectQuestionService(mockService);

        QuestionResponse actual = controller.exchangeQuestionAnswer("user-2", answerResponse);

        assertNull(actual);
        verify(mockService).getNextQuestions("user-2", null, null);
    }

    @Test
    void getAssessmentForSubmittedQuestions_returnsList() throws Exception {
        QuestionService mockService = mock(QuestionService.class);
        List<String> expected = Arrays.asList("sol1", "sol2");
        when(mockService.getSolutionForUserContext("userX")).thenReturn(expected);

        injectQuestionService(mockService);

        List<String> actual = controller.getAssessmentForSubmittedQuestions("userX");

        assertEquals(expected, actual);
        verify(mockService).getSolutionForUserContext("userX");
    }

    @Test
    void getAssessmentForSubmittedQuestions_returnsNullWhenServiceReturnsNull() throws Exception {
        QuestionService mockService = mock(QuestionService.class);
        when(mockService.getSolutionForUserContext(null)).thenReturn(null);

        injectQuestionService(mockService);

        List<String> actual = controller.getAssessmentForSubmittedQuestions(null);

        assertNull(actual);
        verify(mockService).getSolutionForUserContext(null);
    }
}
