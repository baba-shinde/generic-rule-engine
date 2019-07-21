package com.bss.app.rule.engine.dto;

import java.util.List;
import java.util.Objects;

public class AnswerResponse {
    private String requestId;
    private List<Answer> answers;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnswerResponse that = (AnswerResponse) o;
        return Objects.equals(requestId, that.requestId) &&
                Objects.equals(answers, that.answers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestId, answers);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AnswerResponse{");
        sb.append("requestId='").append(requestId).append('\'');
        sb.append(", answers=").append(answers);
        sb.append('}');
        return sb.toString();
    }
}
