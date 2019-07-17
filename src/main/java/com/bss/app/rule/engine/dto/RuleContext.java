package com.bss.app.rule.engine.dto;

import java.util.Objects;

public class RuleContext extends Rule {
    private String requestId;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        RuleContext that = (RuleContext) o;
        return Objects.equals(requestId, that.requestId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), requestId);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RuleContext{");
        sb.append("requestId='").append(requestId).append('\'');
        sb.append("Rule='").append(super.toString()).append('\'');
        sb.append('}');
        return sb.toString();
    }
}