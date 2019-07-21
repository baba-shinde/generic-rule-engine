package com.bss.app.rule.engine.dto;

public enum QuestionCategory {
    FREE_TEXT(""),
    BOOLEAN("\\|"),
    ONE_IN_MANY("#"),
    MANY_IN_MANY(",");

    private String delimiter;

    QuestionCategory(final String delimiter) {
        this.delimiter = delimiter;
    }

    public String getDelimiter() {
        return this.delimiter;
    }
}