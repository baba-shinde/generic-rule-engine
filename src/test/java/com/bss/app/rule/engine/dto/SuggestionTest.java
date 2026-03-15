package com.bss.app.rule.engine.dto;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SuggestionTest {

    @Test
    void getterSetterAndToString_nonNullList() {
        Suggestion<String> s = new Suggestion<>();
        List<String> list = Arrays.asList("a","b");
        s.setSuggestionDetails(list);
        assertEquals(list, s.getSuggestionDetails());
        assertEquals("Suggestion{suggestionDetails=" + list + "}", s.toString());
    }

    @Test
    void equals_sameReferenceAndNullAndDifferentClass() {
        Suggestion<String> s = new Suggestion<>();
        List<String> list = Arrays.asList("a","b");
        s.setSuggestionDetails(list);
        assertTrue(s.equals(s));
        assertFalse(s.equals(null));
        assertFalse(s.equals(new Object()));
    }

    @Test
    void equalsAndHashCode_equalContent() {
        List<String> list = Arrays.asList("a","b");
        Suggestion<String> s1 = new Suggestion<>();
        s1.setSuggestionDetails(list);
        Suggestion<String> s2 = new Suggestion<>();
        s2.setSuggestionDetails(new ArrayList<>(list));
        assertEquals(s1, s2);
        assertEquals(s1.hashCode(), s2.hashCode());
    }

    @Test
    void notEquals_differentContent() {
        Suggestion<String> s1 = new Suggestion<>();
        s1.setSuggestionDetails(Arrays.asList("a","b"));
        Suggestion<String> s2 = new Suggestion<>();
        s2.setSuggestionDetails(Arrays.asList("x"));
        assertNotEquals(s1, s2);
    }

    @Test
    void nullSuggestionDetails_behavior() {
        Suggestion<String> s1 = new Suggestion<>();
        Suggestion<String> s2 = new Suggestion<>();
        assertNull(s1.getSuggestionDetails());
        assertEquals(s1, s2);
        assertEquals(s1.hashCode(), s2.hashCode());
        assertEquals("Suggestion{suggestionDetails=null}", s1.toString());
    }
}
