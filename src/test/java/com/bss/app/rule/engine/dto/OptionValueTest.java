package com.bss.app.rule.engine.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OptionValueTest {

    @Test
    void testGettersAndToString() {
        OptionValue ov = new OptionValue("q1", "opt1");
        assertEquals("q1", ov.getQuestionId());
        assertEquals("opt1", ov.getOption());
        assertEquals("OptionValue{questionId='q1', option='opt1'}", ov.toString());

        OptionValue ovNulls = new OptionValue(null, null);
        assertNull(ovNulls.getQuestionId());
        assertNull(ovNulls.getOption());
        assertEquals("OptionValue{questionId='null', option='null'}", ovNulls.toString());

        OptionValue ovQuestionNull = new OptionValue(null, "optX");
        assertEquals("OptionValue{questionId='null', option='optX'}", ovQuestionNull.toString());

        OptionValue ovOptionNull = new OptionValue("qY", null);
        assertEquals("OptionValue{questionId='qY', option='null'}", ovOptionNull.toString());
    }

    @Test
    void testEqualsAndHashCode_sameObject() {
        OptionValue ov = new OptionValue("q", "o");
        assertTrue(ov.equals(ov));
        assertEquals(ov.hashCode(), ov.hashCode());
    }

    @Test
    void testEqualsAndHashCode_equalObjects() {
        OptionValue a = new OptionValue("q", "o");
        OptionValue b = new OptionValue("q", "o");
        assertEquals(a, b);
        assertEquals(b, a);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testEquals_differentQuestionId() {
        OptionValue a = new OptionValue("q1", "o");
        OptionValue b = new OptionValue("q2", "o");
        assertNotEquals(a, b);
    }

    @Test
    void testEquals_differentOption() {
        OptionValue a = new OptionValue("q", "o1");
        OptionValue b = new OptionValue("q", "o2");
        assertNotEquals(a, b);
    }

    @Test
    void testEquals_nullAndDifferentClass() {
        OptionValue a = new OptionValue("q", "o");
        assertNotEquals(null, a);
        assertNotEquals(a, null);
        assertFalse(a.equals("some string"));
    }

    @Test
    void testEquals_nullFieldCombinations() {
        OptionValue a = new OptionValue(null, "o");
        OptionValue b = new OptionValue(null, "o");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        OptionValue c = new OptionValue("q", null);
        OptionValue d = new OptionValue("q", null);
        assertEquals(c, d);
        assertEquals(c.hashCode(), d.hashCode());

        OptionValue e = new OptionValue(null, null);
        OptionValue f = new OptionValue(null, null);
        assertEquals(e, f);
        assertEquals(e.hashCode(), f.hashCode());

        // ensure inequality when one side has null and the other has value
        OptionValue g = new OptionValue(null, "o");
        OptionValue h = new OptionValue("q", "o");
        assertNotEquals(g, h);

        OptionValue i = new OptionValue("q", null);
        OptionValue j = new OptionValue("q", "o");
        assertNotEquals(i, j);
    }
}
