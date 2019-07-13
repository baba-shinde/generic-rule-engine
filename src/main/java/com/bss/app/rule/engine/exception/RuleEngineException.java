package com.bss.app.rule.engine.exception;

public class RuleEngineException extends Exception {
    public RuleEngineException() {
        super();
    }

    public RuleEngineException(final String message) {
        super(message);
    }

    public RuleEngineException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public RuleEngineException(final Throwable cause) {
        super(cause);
    }

    protected RuleEngineException(final String message, final Throwable cause,
                                  final boolean enableSuppression,
                                  final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
