package com.bss.app.rule.engine.dto;

import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Question {
    private String id;
    private String text;
    private QuestionCategory questionCategory;
    private String rawOptions;
    private List<String> options;
    private Map<String, Question> questionMap;
    private String dependsOnQuestion;
    private String dependsOnOption;
    private String associatedColumnInRules;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public QuestionCategory getQuestionCategory() {
        return questionCategory;
    }

    public void setQuestionCategory(QuestionCategory questionCategory) {
        this.questionCategory = questionCategory;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public Map<String, Question> getQuestionMap() {
        return questionMap;
    }

    public void addQuestionMapping(final String option, final Question question) {
        if (CollectionUtils.isEmpty(this.questionMap)) {
            this.questionMap = new HashMap<>();
        }
        this.questionMap.put(option, question);
    }

    public String getDependsOnQuestion() {
        return dependsOnQuestion;
    }

    public void setDependsOnQuestion(String dependsOnQuestion) {
        this.dependsOnQuestion = dependsOnQuestion;
    }

    public String getDependsOnOption() {
        return dependsOnOption;
    }

    public void setDependsOnOption(String dependsOnOption) {
        this.dependsOnOption = dependsOnOption;
    }

    public String getRawOptions() {
        return rawOptions;
    }

    public void setRawOptions(String rawOptions) {
        this.rawOptions = rawOptions;
    }

    public String getAssociatedColumnInRules() {
        return associatedColumnInRules;
    }

    public void setAssociatedColumnInRules(String associatedColumnInRules) {
        this.associatedColumnInRules = associatedColumnInRules;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question = (Question) o;
        return Objects.equals(id, question.id) &&
                Objects.equals(text, question.text) &&
                questionCategory == question.questionCategory &&
                Objects.equals(rawOptions, question.rawOptions) &&
                Objects.equals(options, question.options) &&
                Objects.equals(questionMap, question.questionMap) &&
                Objects.equals(dependsOnQuestion, question.dependsOnQuestion) &&
                Objects.equals(dependsOnOption, question.dependsOnOption) &&
                Objects.equals(associatedColumnInRules, question.associatedColumnInRules);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, questionCategory, rawOptions, options, questionMap, dependsOnQuestion, dependsOnOption, associatedColumnInRules);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Question{");
        sb.append("id='").append(id).append('\'');
        sb.append(", text='").append(text).append('\'');
        sb.append(", questionCategory=").append(questionCategory);
        sb.append(", rawOptions='").append(rawOptions).append('\'');
        sb.append(", options=").append(options);
        sb.append(", questionMap=").append(questionMap);
        sb.append(", dependsOnQuestion='").append(dependsOnQuestion).append('\'');
        sb.append(", dependsOnOption='").append(dependsOnOption).append('\'');
        sb.append(", associatedColumnInRules='").append(associatedColumnInRules).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
