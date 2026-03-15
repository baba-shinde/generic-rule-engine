package com.bss.app.rule.engine.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuestionCategoryTest {

    @Test
    void getDelimiterValues() {
        assertEquals("", QuestionCategory.FREE_TEXT.getDelimiter());
        assertEquals("\\|", QuestionCategory.BOOLEAN.getDelimiter());
        assertEquals("#", QuestionCategory.ONE_IN_MANY.getDelimiter());
        assertEquals(",", QuestionCategory.MANY_IN_MANY.getDelimiter());
    }

    @Test
    void valueOfAndNames() {
        for (QuestionCategory q : QuestionCategory.values()) {
            assertSame(QuestionCategory.valueOf(q.name()), q);
            assertTrue(q.name().length() > 0);
        }
    }

    @Test
    void splitBehavior() {
        // BOOLEAN
        String[] b = "true|false".split(QuestionCategory.BOOLEAN.getDelimiter());
        assertArrayEquals(new String[]{"true", "false"}, b);

        // ONE_IN_MANY
        String[] one = "a#b#c".split(QuestionCategory.ONE_IN_MANY.getDelimiter());
        assertArrayEquals(new String[]{"a", "b", "c"}, one);

        // MANY_IN_MANY
        String[] many = "x,y".split(QuestionCategory.MANY_IN_MANY.getDelimiter());
        assertArrayEquals(new String[]{"x", "y"}, many);

        // FREE_TEXT - splitting with empty delimiter splits into chars
        String[] free = "ab".split(QuestionCategory.FREE_TEXT.getDelimiter());
        assertArrayEquals(new String[]{"a", "b"}, free);
    }
}
