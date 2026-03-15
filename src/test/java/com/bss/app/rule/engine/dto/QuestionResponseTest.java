package com.bss.app.rule.engine.dto;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuestionResponseTest {

    @Test
    void gettersSettersEqualsHashCodeToString() {
        QuestionResponse qr = new QuestionResponse();
        qr.setOriginalRequestId("req-1");

        Question q = new Question();
        q.setId("q1");
        List<Question> list = Collections.singletonList(q);
        qr.setQuestions(list);

        // getters
        assertEquals("req-1", qr.getOriginalRequestId());
        assertSame(list, qr.getQuestions());

        // toString contains key pieces
        String s = qr.toString();
        assertTrue(s.contains("req-1"));
        assertTrue(s.contains("q1") || s.contains("Question{"));

        // equals/hashCode with identical values
        QuestionResponse other = new QuestionResponse();
        other.setOriginalRequestId("req-1");
        other.setQuestions(Collections.singletonList(q));

        assertEquals(qr, other);
        assertEquals(qr.hashCode(), other.hashCode());

        // self equality
        assertEquals(qr, qr);
    }

    @Test
    void equalsWithNullAndDifferentClass() {
        QuestionResponse qr = new QuestionResponse();
        qr.setOriginalRequestId("x");

        assertNotEquals(null, qr);
        assertNotEquals("some string", qr);
    }

    @Test
    void differentOriginalRequestIdOrQuestionsNotEqual() {
        QuestionResponse a = new QuestionResponse();
        a.setOriginalRequestId("id-a");

        QuestionResponse b = new QuestionResponse();
        b.setOriginalRequestId("id-b");

        assertNotEquals(a, b);

        // different questions (null vs empty list)
        a.setOriginalRequestId(null);
        a.setQuestions(null);

        b.setOriginalRequestId(null);
        b.setQuestions(Collections.emptyList());

        assertNotEquals(a, b);
    }

    @Test
    void hashCodeAndToStringWhenFieldsAreNull() {
        QuestionResponse qr = new QuestionResponse();
        // no NPE when calling hashCode/toString
        int hc = qr.hashCode();
        assertNotNull(qr.toString());
        // hashCode should be stable across calls
        assertEquals(hc, qr.hashCode());
    }
}
