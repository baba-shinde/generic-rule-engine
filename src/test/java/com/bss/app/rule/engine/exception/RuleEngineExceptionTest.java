package com.bss.app.rule.engine.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RuleEngineExceptionTest {

    @Test
    public void defaultConstructor() {
        RuleEngineException ex = new RuleEngineException();
        assertNull(ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    public void messageConstructor() {
        String msg = "test message";
        RuleEngineException ex = new RuleEngineException(msg);
        assertEquals(msg, ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    public void messageAndCauseConstructor() {
        Throwable cause = new IllegalArgumentException("bad arg");
        String msg = "my message";
        RuleEngineException ex = new RuleEngineException(msg, cause);
        assertEquals(msg, ex.getMessage());
        assertEquals(cause, ex.getCause());
    }

    @Test
    public void causeConstructor() {
        Throwable cause = new RuntimeException("boom");
        RuleEngineException ex = new RuleEngineException(cause);
        assertEquals(cause, ex.getCause());
        // When constructed with a cause-only constructor, message is cause.toString()
        assertEquals(cause.toString(), ex.getMessage());
    }

    @Test
    public void fullConstructorDisableSuppressionNoStackTrace() {
        Throwable cause = new IllegalStateException("state");
        RuleEngineException ex = new RuleEngineException("m", cause, false, false);
        assertEquals("m", ex.getMessage());
        assertEquals(cause, ex.getCause());

        // suppression disabled -> addSuppressed should not increase suppressed array
        ex.addSuppressed(new Exception("s"));
        assertEquals(0, ex.getSuppressed().length);

        // writableStackTrace=false -> stack trace should be empty
        assertEquals(0, ex.getStackTrace().length);
    }

    @Test
    public void fullConstructorEnableSuppressionWithStackTrace() {
        Throwable cause = new IllegalStateException("state2");
        RuleEngineException ex = new RuleEngineException("m2", cause, true, true);
        assertEquals("m2", ex.getMessage());
        assertEquals(cause, ex.getCause());

        Exception suppressed = new Exception("s2");
        ex.addSuppressed(suppressed);
        assertEquals(1, ex.getSuppressed().length);
        assertEquals(suppressed, ex.getSuppressed()[0]);

        assertTrue(ex.getStackTrace().length > 0);
    }
}
