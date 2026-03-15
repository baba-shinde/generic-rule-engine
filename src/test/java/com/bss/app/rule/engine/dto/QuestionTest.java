package com.bss.app.rule.engine.dto;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class QuestionTest {

    @Test
    void gettersSettersEqualsHashCodeToString() {
        Question q = new Question();
        q.setId("q1");
        q.setText("What is x?");
        q.setQuestionCategory(QuestionCategory.ONE_IN_MANY);
        q.setRawOptions("a#b#c");
        List<String> opts = Arrays.asList("a", "b", "c");
        q.setOptions(opts);
        q.setDependsOnQuestion("dq");
        q.setDependsOnOption("do");
        q.setAssociatedColumnInRules("col1");

        // add mapping (map will be created inside method)
        Question nested = new Question();
        nested.setId("nested");
        q.addQuestionMapping("a", nested);

        // getters
        assertEquals("q1", q.getId());
        assertEquals("What is x?", q.getText());
        assertEquals(QuestionCategory.ONE_IN_MANY, q.getQuestionCategory());
        assertEquals("a#b#c", q.getRawOptions());
        assertSame(opts, q.getOptions());
        assertEquals("dq", q.getDependsOnQuestion());
        assertEquals("do", q.getDependsOnOption());
        assertEquals("col1", q.getAssociatedColumnInRules());

        Map<String, Question> map = q.getQuestionMap();
        assertNotNull(map);
        assertTrue(map.containsKey("a"));
        assertEquals("nested", map.get("a").getId());

        // toString contains key pieces
        String s = q.toString();
        assertTrue(s.contains("q1"));
        assertTrue(s.contains("What is x?") || s.contains("Question{"));

        // equals/hashCode with identical values
        Question other = new Question();
        other.setId("q1");
        other.setText("What is x?");
        other.setQuestionCategory(QuestionCategory.ONE_IN_MANY);
        other.setRawOptions("a#b#c");
        other.setOptions(Arrays.asList("a", "b", "c"));
        other.setDependsOnQuestion("dq");
        other.setDependsOnOption("do");
        other.setAssociatedColumnInRules("col1");
        Question nested2 = new Question();
        nested2.setId("nested");
        other.addQuestionMapping("a", nested2);

        assertEquals(q, other);
        assertEquals(q.hashCode(), other.hashCode());

        // self equality
        assertEquals(q, q);
    }

    @Test
    void equalsWithNullAndDifferentClass() {
        Question q = new Question();
        q.setId("x");

        assertNotEquals(null, q);
        assertNotEquals("some string", q);
    }

    @Test
    void differentValuesNotEqual() {
        Question a = new Question();
        a.setId("id-a");
        a.setQuestionCategory(QuestionCategory.FREE_TEXT);

        Question b = new Question();
        b.setId("id-b");
        b.setQuestionCategory(QuestionCategory.BOOLEAN);

        assertNotEquals(a, b);

        // same id but different category should be not equal because category compared with ==
        b.setId("id-a");
        assertNotEquals(a, b);
    }

    @Test
    void addQuestionMappingWhenMapAlreadyExists() {
        Question q = new Question();
        Question n1 = new Question();
        n1.setId("n1");
        Question n2 = new Question();
        n2.setId("n2");

        q.addQuestionMapping("o1", n1);
        q.addQuestionMapping("o2", n2);

        Map<String, Question> map = q.getQuestionMap();
        assertEquals(2, map.size());
        assertEquals("n1", map.get("o1").getId());
        assertEquals("n2", map.get("o2").getId());
    }

    @Test
    void hashCodeAndToStringWhenFieldsAreNull() {
        Question q = new Question();
        // no NPE when calling hashCode/toString
        int hc = q.hashCode();
        assertNotNull(q.toString());
        // hashCode should be stable across calls
        assertEquals(hc, q.hashCode());
    }
}
