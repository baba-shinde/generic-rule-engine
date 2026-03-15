package com.bss.app.rule.engine.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ColumnCategoryTest {

    @Test
    void testValuesAndContains() {
        ColumnCategory[] vals = ColumnCategory.values();
        assertEquals(4, vals.length);
        assertArrayEquals(new ColumnCategory[]{ColumnCategory.ID, ColumnCategory.REGULAR_COLUMN, ColumnCategory.SUGGESTION, ColumnCategory.EMPTY}, vals);
    }

    @Test
    void testValueOfEach() {
        assertEquals(ColumnCategory.ID, ColumnCategory.valueOf("ID"));
        assertEquals(ColumnCategory.REGULAR_COLUMN, ColumnCategory.valueOf("REGULAR_COLUMN"));
        assertEquals(ColumnCategory.SUGGESTION, ColumnCategory.valueOf("SUGGESTION"));
        assertEquals(ColumnCategory.EMPTY, ColumnCategory.valueOf("EMPTY"));
    }

    @Test
    void testOrdinalsAndToString() {
        assertEquals("ID", ColumnCategory.ID.name());
        assertEquals(0, ColumnCategory.ID.ordinal());

        assertEquals("REGULAR_COLUMN", ColumnCategory.REGULAR_COLUMN.name());
        assertEquals("SUGGESTION", ColumnCategory.SUGGESTION.name());
        assertEquals("EMPTY", ColumnCategory.EMPTY.name());

        // toString defaults to name()
        assertEquals(ColumnCategory.ID.name(), ColumnCategory.ID.toString());
    }

    @Test
    void testSwitchLikeMapping() {
        assertEquals("id", map(ColumnCategory.ID));
        assertEquals("regular", map(ColumnCategory.REGULAR_COLUMN));
        assertEquals("suggestion", map(ColumnCategory.SUGGESTION));
        assertEquals("empty", map(ColumnCategory.EMPTY));
    }

    private String map(ColumnCategory c) {
        switch (c) {
            case ID:
                return "id";
            case REGULAR_COLUMN:
                return "regular";
            case SUGGESTION:
                return "suggestion";
            case EMPTY:
                return "empty";
            default:
                throw new IllegalArgumentException("Unknown: " + c);
        }
    }
}
