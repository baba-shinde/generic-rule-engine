package com.bss.app.rule.engine.dto;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Tuple2Test {

    @Test
    void gettersReturnValues_whenBothNonNull() {
        Tuple2<String, Integer> t = new Tuple2<>("one", 1);
        assertEquals("one", t.getF1());
        assertEquals(1, t.getF2());
    }

    @Test
    void gettersReturnNull_whenFirstNull() {
        Tuple2<String, Integer> t = new Tuple2<>(null, 2);
        assertNull(t.getF1());
        assertEquals(2, t.getF2());
    }

    @Test
    void gettersReturnNull_whenSecondNull() {
        Tuple2<String, Integer> t = new Tuple2<>("two", null);
        assertEquals("two", t.getF1());
        assertNull(t.getF2());
    }

    @Test
    void gettersReturnNull_whenBothNull() {
        Tuple2<Object, Object> t = new Tuple2<>(null, null);
        assertNull(t.getF1());
        assertNull(t.getF2());
    }

    @Test
    void holdsReferences_forMutableObjects() {
        List<String> list = new ArrayList<>();
        list.add("x");
        Tuple2<List<String>, String> t = new Tuple2<>(list, "s");

        // ensure it holds the same reference
        assertSame(list, t.getF1());
        assertEquals("s", t.getF2());

        // mutations to the original list should be visible through the tuple reference
        list.add("y");
        assertEquals(2, t.getF1().size());
    }
}
