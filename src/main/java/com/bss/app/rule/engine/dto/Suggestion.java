package com.bss.app.rule.engine.dto;

import java.util.List;
import java.util.Objects;

public class Suggestion<T> {
    private List<T> suggestionDetails;

    public List<T> getSuggestionDetails() {
        return suggestionDetails;
    }

    public void setSuggestionDetails(List<T> suggestionDetails) {
        this.suggestionDetails = suggestionDetails;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Suggestion<?> that = (Suggestion<?>) o;
        return Objects.equals(suggestionDetails, that.suggestionDetails);
    }

    @Override
    public int hashCode() {
        return Objects.hash(suggestionDetails);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Suggestion{");
        sb.append("suggestionDetails=").append(suggestionDetails);
        sb.append('}');
        return sb.toString();
    }
}
