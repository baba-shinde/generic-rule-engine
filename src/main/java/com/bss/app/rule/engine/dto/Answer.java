package com.bss.app.rule.engine.dto;

import java.util.List;
import java.util.Objects;

public class Answer {
    private String qustionId;
    private String answer;
    private List<Answer> nestedAnswers;

    public String getQustionId() {
        return qustionId;
    }

    public void setQustionId(String qustionId) {
        this.qustionId = qustionId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<Answer> getNestedAnswers() {
        return nestedAnswers;
    }

    public void setNestedAnswers(List<Answer> nestedAnswers) {
        this.nestedAnswers = nestedAnswers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Answer answer1 = (Answer) o;
        return Objects.equals(qustionId, answer1.qustionId) &&
                Objects.equals(answer, answer1.answer) &&
                Objects.equals(nestedAnswers, answer1.nestedAnswers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(qustionId, answer, nestedAnswers);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Answer{");
        sb.append("qustionId='").append(qustionId).append('\'');
        sb.append(", answer='").append(answer).append('\'');
        sb.append(", nestedAnswers=").append(nestedAnswers);
        sb.append('}');
        return sb.toString();
    }
}
