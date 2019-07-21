package com.bss.app.rule.engine.dto;

import java.util.List;
import java.util.Objects;

public class QuestionResponse {
    private String originalRequestId;
    private List<Question> questions;

    public String getOriginalRequestId() {
        return originalRequestId;
    }

    public void setOriginalRequestId(String originalRequestId) {
        this.originalRequestId = originalRequestId;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuestionResponse that = (QuestionResponse) o;
        return Objects.equals(originalRequestId, that.originalRequestId) &&
                Objects.equals(questions, that.questions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(originalRequestId, questions);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("QuestionResponse{");
        sb.append("originalRequestId='").append(originalRequestId).append('\'');
        sb.append(", questions=").append(questions);
        sb.append('}');
        return sb.toString();
    }
}
