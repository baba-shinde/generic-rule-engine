package com.bss.app.rule.engine.dto;

import org.springframework.util.CollectionUtils;

import java.util.*;

public class Rule {
    private String id;
    private Map<String, Column> columns;
    private List<String> suggestions;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Column> getColumns() {
        return columns;
    }

    public void setColumns(Map<String, Column> columns) {
        this.columns = columns;
    }

    public void addColumn(Column column) {
        if (column != null) {
            if (CollectionUtils.isEmpty(this.columns)) {
                this.columns = new HashMap<>();
            }
            this.columns.put(column.getName(), column);
        }
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void addSuggestion(final String suggestion) {
        if (suggestion != null) {
            if (CollectionUtils.isEmpty(this.suggestions)) {
                this.suggestions = new ArrayList<>();
            }
            this.suggestions.add(suggestion);
        }
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
