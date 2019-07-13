package com.bss.app.rule.engine.dto;

import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Rule {
    private String id;
    private List<Column> columns;
    private List<String> suggestions;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        if (CollectionUtils.isEmpty(this.suggestions)) {
            this.suggestions = new ArrayList<>();
        }
        this.suggestions.addAll(suggestions);
    }

    public void addSuggestion(final String suggestion) {
        if (CollectionUtils.isEmpty(this.suggestions)) {
            this.suggestions = new ArrayList<>();
        }
        this.suggestions.add(suggestion);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rule rule = (Rule) o;
        return Objects.equals(id, rule.id) &&
                Objects.equals(columns, rule.columns) &&
                Objects.equals(suggestions, rule.suggestions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, columns, suggestions);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Rule{");
        sb.append("id='").append(id).append('\'');
        sb.append(", columns=").append(columns);
        sb.append(", suggestions=").append(suggestions);
        sb.append('}');
        return sb.toString();
    }
}
