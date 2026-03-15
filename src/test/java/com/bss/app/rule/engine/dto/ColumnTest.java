package com.bss.app.rule.engine.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ColumnTest {

    @Test
    void gettersSettersAndToString() {
        Column c = new Column("name", "value");
        assertEquals("name", c.getName());
        assertEquals("value", c.getValue());

        c.setName("newName");
        c.setValue("newValue");
        assertEquals("newName", c.getName());
        assertEquals("newValue", c.getValue());

        String s = c.toString();
        assertTrue(s.contains("name='newName'"));
        assertTrue(s.contains("value='newValue'"));
    }

    @Test
    void equalsAndHashCode_sameValues() {
        Column a = new Column("n", "v");
        Column b = new Column("n", "v");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        // symmetry
        assertTrue(a.equals(b) && b.equals(a));
    }

    @Test
    void equalsDifferentName() {
        Column a = new Column("n1", "v");
        Column b = new Column("n2", "v");
        assertNotEquals(a, b);
    }

    @Test
    void equalsDifferentValue() {
        Column a = new Column("n", "v1");
        Column b = new Column("n", "v2");
        assertNotEquals(a, b);
    }

    @Test
    void equalsWithNullsAndDifferentClass() {
        Column a = new Column(null, null);
        // not equal to null
        assertNotEquals(a, null);
        // not equal to different type
        assertNotEquals(a, "some string");

        Column b = new Column(null, null);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equalsSameInstance() {
        Column a = new Column("x", "y");
        assertEquals(a, a);
    }

    @Test
    void toStringHandlesNulls() {
        Column a = new Column(null, "v");
        String s = a.toString();
        // when name==null append(null) produces string "null"
        assertTrue(s.contains("name='null'"));
        assertTrue(s.contains("value='v'"));

        Column b = new Column("n", null);
        String t = b.toString();
        assertTrue(t.contains("name='n'"));
        assertTrue(t.contains("value='null'"));
    }
}
