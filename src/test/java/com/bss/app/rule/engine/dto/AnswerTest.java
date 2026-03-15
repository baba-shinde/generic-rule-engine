package com.bss.app.rule.engine.dto;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class AnswerTest {

    @Test
    void testGettersSettersAndToString() {
        Answer nested = new Answer();
        nested.setQustionId("q2");
        nested.setAnswer("ans2");

        Answer a = new Answer();
        a.setQustionId("q1");
        a.setAnswer("ans1");
        a.setNestedAnswers(Arrays.asList(nested));

        assertEquals("q1", a.getQustionId());
        assertEquals("ans1", a.getAnswer());
        assertNotNull(a.getNestedAnswers());
        assertEquals(1, a.getNestedAnswers().size());
        assertEquals(nested, a.getNestedAnswers().get(0));

        String ts = a.toString();
        assertTrue(ts.contains("qustionId='q1'"));
        assertTrue(ts.contains("answer='ans1'"));
        assertTrue(ts.contains("nestedAnswers="));
    }

    @Test
    void testEqualsAndHashCode_equalObjects() {
        Answer n1 = new Answer();
        n1.setQustionId("n");
        n1.setAnswer("na");

        Answer a1 = new Answer();
        a1.setQustionId("q");
        a1.setAnswer("a");
        a1.setNestedAnswers(Arrays.asList(n1));

        Answer n2 = new Answer();
        n2.setQustionId("n");
        n2.setAnswer("na");

        Answer a2 = new Answer();
        a2.setQustionId("q");
        a2.setAnswer("a");
        a2.setNestedAnswers(Arrays.asList(n2));

        assertEquals(a1, a2);
        assertEquals(a1.hashCode(), a2.hashCode());
    }

    @Test
    void testEquals_selfNullDifferentClass() {
        Answer a = new Answer();
        a.setQustionId("x");
        a.setAnswer("y");

        assertEquals(a, a); // same instance
        assertNotEquals(a, null);
        assertNotEquals(a, "a string");
    }

    @Test
    void testNotEquals_differentFields() {
        Answer a1 = new Answer();
        a1.setQustionId("q1");
        a1.setAnswer("ans1");
        a1.setNestedAnswers(Collections.emptyList());

        Answer a2 = new Answer();
        a2.setQustionId("q2");
        a2.setAnswer("ans2");
        a2.setNestedAnswers(null);

        assertNotEquals(a1, a2);
    }

    @Test
    void testToString_withNulls() {
        Answer a = new Answer();
        // all fields null
        String ts = a.toString();
        assertTrue(ts.contains("qustionId='null'"));
        assertTrue(ts.contains("answer='null'"));
        assertTrue(ts.contains("nestedAnswers=null"));
    }
}
