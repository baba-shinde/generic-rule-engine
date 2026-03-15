package com.bss.app.rule.engine.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RuleEngineExceptionTest {

    @Test
    void constructor_bothNonNull() {
        String msg = "error-message";
        Throwable cause = new IllegalArgumentException("cause-message");

        RuleEngineException ex = new RuleEngineException(msg, cause);

        assertEquals(msg, ex.getMessage());
        assertSame(cause, ex.getCause());
        assertEquals(msg, ex.getLocalizedMessage());
        assertTrue(ex.toString().contains("RuleEngineException"));
    }

    @Test
    void constructor_messageNull_causeNonNull() {
        String msg = null;
        Throwable cause = new RuntimeException("runtime-cause");

        RuleEngineException ex = new RuleEngineException(msg, cause);

        assertNull(ex.getMessage());
        assertSame(cause, ex.getCause());
    }

    @Test
    void constructor_messageNonNull_causeNull() {
        String msg = "only-message";

        RuleEngineException ex = new RuleEngineException(msg, null);

        assertEquals(msg, ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void constructor_bothNull() {
        RuleEngineException ex = new RuleEngineException(null, null);

        assertNull(ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void constructor_withNestedCause() {
        Throwable inner = new IllegalStateException("inner");
        Throwable outer = new Exception("outer", inner);

        RuleEngineException ex = new RuleEngineException("top-message", outer);

        assertEquals("top-message", ex.getMessage());
        assertSame(outer, ex.getCause());
        assertSame(inner, ex.getCause().getCause());
    }
}
