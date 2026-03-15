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
    void defaultConstructor_noArgs() {
        RuleEngineException ex = new RuleEngineException();

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

    @Test
    void constructor_causeOnly_nonNull() {
        Throwable cause = new IllegalArgumentException("only-cause");

        RuleEngineException ex = new RuleEngineException(cause);

        // When constructed with Throwable cause only, message should be cause.toString()
        assertSame(cause, ex.getCause());
        assertEquals(cause.toString(), ex.getMessage());
        assertEquals(cause.toString(), ex.getLocalizedMessage());
        assertTrue(ex.toString().contains("RuleEngineException"));
    }

    @Test
    void constructor_causeOnly_null() {
        RuleEngineException ex = new RuleEngineException((Throwable) null);

        assertNull(ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void protectedConstructor_suppressionTrue_stackTraceTrue() {
        String msg = "msg-true-true";
        Throwable cause = new RuntimeException("c");

        RuleEngineException ex = new RuleEngineException(msg, cause, true, true);

        assertEquals(msg, ex.getMessage());
        assertSame(cause, ex.getCause());

        Exception suppressed = new Exception("suppressed");
        ex.addSuppressed(suppressed);
        Throwable[] suppressedArr = ex.getSuppressed();
        assertEquals(1, suppressedArr.length);
        assertSame(suppressed, suppressedArr[0]);

        // writableStackTrace true => stack trace should be present
        assertTrue(ex.getStackTrace().length > 0);
    }

    @Test
    void protectedConstructor_suppressionTrue_stackTraceFalse() {
        String msg = "msg-true-false";
        Throwable cause = new RuntimeException("c");

        RuleEngineException ex = new RuleEngineException(msg, cause, true, false);

        assertEquals(msg, ex.getMessage());
        assertSame(cause, ex.getCause());

        Exception suppressed = new Exception("suppressed");
        ex.addSuppressed(suppressed);
        Throwable[] suppressedArr = ex.getSuppressed();
        assertEquals(1, suppressedArr.length);
        assertSame(suppressed, suppressedArr[0]);

        // writableStackTrace false => no stack trace retained
        assertEquals(0, ex.getStackTrace().length);
    }

    @Test
    void protectedConstructor_suppressionFalse_stackTraceTrue() {
        String msg = "msg-false-true";
        Throwable cause = new RuntimeException("c");

        RuleEngineException ex = new RuleEngineException(msg, cause, false, true);

        assertEquals(msg, ex.getMessage());
        assertSame(cause, ex.getCause());

        Exception suppressed = new Exception("suppressed");
        ex.addSuppressed(suppressed);
        Throwable[] suppressedArr = ex.getSuppressed();
        // suppression disabled => no suppressed exceptions recorded
        assertEquals(0, suppressedArr.length);

        // writableStackTrace true => stack trace should be present
        assertTrue(ex.getStackTrace().length > 0);
    }

    @Test
    void protectedConstructor_suppressionFalse_stackTraceFalse() {
        String msg = "msg-false-false";
        Throwable cause = new RuntimeException("c");

        RuleEngineException ex = new RuleEngineException(msg, cause, false, false);

        assertEquals(msg, ex.getMessage());
        assertSame(cause, ex.getCause());

        Exception suppressed = new Exception("suppressed");
        ex.addSuppressed(suppressed);
        Throwable[] suppressedArr = ex.getSuppressed();
        // suppression disabled => no suppressed exceptions recorded
        assertEquals(0, suppressedArr.length);

        // writableStackTrace false => no stack trace retained
        assertEquals(0, ex.getStackTrace().length);
    }

    // ---------------------------------------------------------------------
    // Tests specifically covering the single-argument String constructor
    // ---------------------------------------------------------------------

    @Test
    void constructor_stringOnly_nonNull() {
        String msg = "only-string";
        RuleEngineException ex = new RuleEngineException(msg);

        assertEquals(msg, ex.getMessage());
        assertNull(ex.getCause());
        // default behavior keeps a stack trace
        assertTrue(ex.getStackTrace().length > 0);
        assertEquals(0, ex.getSuppressed().length);
    }

    @Test
    void constructor_stringOnly_null() {
        RuleEngineException ex = new RuleEngineException((String) null);

        assertNull(ex.getMessage());
        assertNull(ex.getCause());
        assertEquals(0, ex.getSuppressed().length);
    }

    @Test
    void constructor_stringOnly_empty() {
        RuleEngineException ex = new RuleEngineException("");

        assertEquals("", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void thrown_stringOnly_retainsMessage() {
        String msg = "thrown-string";
        RuleEngineException thrown = assertThrows(RuleEngineException.class, () -> {
            throw new RuleEngineException(msg);
        });

        assertEquals(msg, thrown.getMessage());
    }
}
