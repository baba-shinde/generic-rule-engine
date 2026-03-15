package com.bss.app.rule.questions.loader;

import com.bss.app.rule.engine.dto.Question;
import com.bss.app.rule.engine.exception.RuleEngineException;
import com.bss.app.rule.questions.reader.QuestionReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionsLoaderTest {

    @Mock
    private QuestionReader questionReader;

    @InjectMocks
    private QuestionsLoader questionsLoader;

    @BeforeEach
    void setUp() {
        // set a dummy path used by the loader when it calls the reader
        ReflectionTestUtils.setField(questionsLoader, "questionsPath", "dummy-path");
    }

    @Test
    void afterPropertiesSet_populatesQuestionsAndMap() throws Exception {
        List<Question> list = new ArrayList<>();
        Question q1 = new Question(); q1.setId("1"); q1.setAssociatedColumnInRules("col1");
        Question q2 = new Question(); q2.setId("2"); q2.setAssociatedColumnInRules("col2");
        list.add(q1); list.add(q2);

        when(questionReader.readQuestionsFromInputStream("dummy-path")).thenReturn(list);

        questionsLoader.afterPropertiesSet();

        assertEquals(list, questionsLoader.getAllQuestions());
        Map<String, String> map = questionsLoader.getQnsColumnMap();
        assertNotNull(map);
        assertEquals(2, map.size());
        assertEquals("col1", map.get("1"));
        assertEquals("col2", map.get("2"));
    }

    @Test
    void loadQuestions_withNonEmpty_updatesQuestionsAndMap() throws Exception {
        List<Question> newList = new ArrayList<>();
        Question q1 = new Question(); q1.setId("10"); q1.setAssociatedColumnInRules("acol");
        newList.add(q1);

        when(questionReader.readQuestionsFromInputStream("dummy-path")).thenReturn(newList);

        questionsLoader.loadQuestions(null);

        assertEquals(newList, questionsLoader.getAllQuestions());
        assertNotNull(questionsLoader.getQnsColumnMap());
        assertEquals("acol", questionsLoader.getQnsColumnMap().get("10"));
    }

    @Test
    void loadQuestions_withEmpty_doesNotChangeExistingQuestions() throws Exception {
        // set initial questions
        List<Question> initial = new ArrayList<>();
        Question qInit = new Question(); qInit.setId("A"); qInit.setAssociatedColumnInRules("icol");
        initial.add(qInit);
        questionsLoader.setQuestions(initial);
        // populate map by invoking afterPropertiesSet with reader returning initial
        when(questionReader.readQuestionsFromInputStream("dummy-path")).thenReturn(initial);
        questionsLoader.afterPropertiesSet();

        // now mock reader to return empty
        when(questionReader.readQuestionsFromInputStream("dummy-path")).thenReturn(new ArrayList<>());

        questionsLoader.loadQuestions(null);

        // should remain unchanged
        assertEquals(initial, questionsLoader.getAllQuestions());
        assertNotNull(questionsLoader.getQnsColumnMap());
        assertEquals("icol", questionsLoader.getQnsColumnMap().get("A"));
    }

    @Test
    void loadQuestions_ioException_wrappedAsRuleEngineException() throws Exception {
        when(questionReader.readQuestionsFromInputStream("dummy-path")).thenThrow(new IOException("boom"));

        RuleEngineException ex = assertThrows(RuleEngineException.class, () -> questionsLoader.loadQuestions(null));
        assertTrue(ex.getMessage().contains("Exception occurred while loading questions"));
        assertNotNull(ex.getCause());
        assertEquals(IOException.class, ex.getCause().getClass());
    }

    @Test
    void nextQuestions_variousBounds() {
        List<Question> questions = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            Question q = new Question(); q.setId(String.valueOf(i));
            questions.add(q);
        }
        questionsLoader.setQuestions(questions);

        // case: size > number+currentNo -> normal sublist
        List<Question> s1 = questionsLoader.getNextQuestions(3, 0);
        assertNotNull(s1);
        assertEquals(3, s1.size());
        assertEquals("1", s1.get(0).getId());

        // case: size > number+currentNo with offset
        List<Question> s2 = questionsLoader.getNextQuestions(3, 3);
        assertNotNull(s2);
        assertEquals(3, s2.size());
        assertEquals("4", s2.get(0).getId());

        // case: number+currentNo > size -> return until end
        List<Question> s3 = questionsLoader.getNextQuestions(3, 6);
        assertNotNull(s3);
        assertEquals(2, s3.size());
        assertEquals("7", s3.get(0).getId());

        // case: number+currentNo == size -> method returns null
        List<Question> s4 = questionsLoader.getNextQuestions(2, 6);
        assertNull(s4);
    }

    @Test
    void afterPropertiesSet_withEmptyQuestions_resultsInNullMap() throws Exception {
        when(questionReader.readQuestionsFromInputStream("dummy-path")).thenReturn(new ArrayList<>());

        questionsLoader.afterPropertiesSet();

        assertNotNull(questionsLoader.getAllQuestions());
        assertTrue(questionsLoader.getAllQuestions().isEmpty());
        assertNull(questionsLoader.getQnsColumnMap());
    }
}
