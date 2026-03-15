package com.bss.app.rule.questions.cache;

import com.bss.app.rule.engine.dto.Answer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuestionnaireCacheTest {

    private QuestionnaireCache cache;

    @BeforeEach
    void setup() throws Exception {
        cache = new QuestionnaireCache();
        cache.afterPropertiesSet();
    }

    @Test
    void afterPropertiesSet_initializesMap() {
        // no exception from setup => map initialized; verify behavior for unknown user
        assertEquals(0, cache.getCurrentQnsNo("unknown-user"));
    }

    @Test
    void saveAnswersForUser_createsListAndAddsAnswers() {
        Answer a1 = new Answer();
        a1.setQustionId("q1");
        a1.setAnswer("yes");

        List<Answer> answers = Arrays.asList(a1);
        cache.saveAnswersForUser("u1", answers);

        assertEquals(1, cache.getCurrentQnsNo("u1"));

        List<Answer> removed = cache.removeAnswersForUser("u1");
        assertNotNull(removed);
        assertEquals(1, removed.size());
        assertEquals(a1, removed.get(0));

        // after removal, count should be zero
        assertEquals(0, cache.getCurrentQnsNo("u1"));
    }

    @Test
    void saveAnswersForUser_appendsWhenUserExists() {
        Answer a1 = new Answer();
        a1.setQustionId("q1");
        a1.setAnswer("yes");

        Answer a2 = new Answer();
        a2.setQustionId("q2");
        a2.setAnswer("no");

        cache.saveAnswersForUser("u2", Collections.singletonList(a1));
        assertEquals(1, cache.getCurrentQnsNo("u2"));

        cache.saveAnswersForUser("u2", Collections.singletonList(a2));
        assertEquals(2, cache.getCurrentQnsNo("u2"));

        List<Answer> removed = cache.removeAnswersForUser("u2");
        assertEquals(2, removed.size());
        assertEquals(a1, removed.get(0));
        assertEquals(a2, removed.get(1));
    }

    @Test
    void saveAnswersForUser_withEmptyList_createsEmptyEntry() {
        cache.saveAnswersForUser("u3", Collections.emptyList());
        assertEquals(0, cache.getCurrentQnsNo("u3"));

        List<Answer> removed = cache.removeAnswersForUser("u3");
        // should return an empty list (not null)
        assertNotNull(removed);
        assertTrue(removed.isEmpty());
    }

    @Test
    void saveAnswersForUser_withNestedAnswers_preservesStructure() {
        Answer child = new Answer();
        child.setQustionId("cq1");
        child.setAnswer("child-ans");

        Answer parent = new Answer();
        parent.setQustionId("pq1");
        parent.setAnswer("parent-ans");
        parent.setNestedAnswers(new ArrayList<>(Collections.singletonList(child)));

        cache.saveAnswersForUser("u4", Collections.singletonList(parent));
        List<Answer> removed = cache.removeAnswersForUser("u4");
        assertEquals(1, removed.size());
        Answer stored = removed.get(0);
        assertEquals("pq1", stored.getQustionId());
        assertNotNull(stored.getNestedAnswers());
        assertEquals(1, stored.getNestedAnswers().size());
        assertEquals(child, stored.getNestedAnswers().get(0));
    }

    @Test
    void saveAnswersForUser_nullAnswers_throwsNPE() {
        assertThrows(NullPointerException.class, () -> cache.saveAnswersForUser("u5", null));
    }
}
