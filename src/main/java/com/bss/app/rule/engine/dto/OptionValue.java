package com.bss.app.rule.engine.dto;

import java.util.Objects;

public class OptionValue {
    private String questionId;
    private String option;

    public OptionValue(String questionId, String option) {
        this.questionId = questionId;
        this.option = option;
    }

    public String getQuestionId() {
        return questionId;
    }

    public String getOption() {
        return option;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OptionValue value = (OptionValue) o;
        return Objects.equals(questionId, value.questionId) &&
                Objects.equals(option, value.option);
    }

    @Override
    public int hashCode() {
        return Objects.hash(questionId, option);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("OptionValue{");
        sb.append("questionId='").append(questionId).append('\'');
        sb.append(", option='").append(option).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
