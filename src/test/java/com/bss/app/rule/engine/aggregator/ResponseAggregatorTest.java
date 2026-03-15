package com.bss.app.rule.engine.aggregator;

import com.bss.app.rule.engine.dto.Rule;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ResponseAggregatorTest {

    private final ResponseAggregator aggregator = new ResponseAggregator();

    @Test
    void aggregateResponse_nullMatchedRules_returnsNull() {
        List<String> result = aggregator.aggregateResponse(null);
        assertNull(result, "Result should be null when matchedRules is null");
    }

    @Test
    void aggregateResponse_emptyMatchedRules_returnsNull() {
        List<Rule> matched = new ArrayList<>();
        List<String> result = aggregator.aggregateResponse(matched);
        assertNull(result, "Result should be null when matchedRules is empty");
    }

    @Test
    void aggregateResponse_singleRuleWithNoSuggestions_returnsEmptyList() {
        Rule r = new Rule();
        List<Rule> matched = Arrays.asList(r);

        List<String> result = aggregator.aggregateResponse(matched);
        assertNotNull(result, "Result should not be null when matchedRules is non-empty");
        assertTrue(result.isEmpty(), "Result should be an empty list when no suggestions are present");
    }

    @Test
    void aggregateResponse_multipleRulesAggregatesAllSuggestions_inOrder() {
        Rule r1 = new Rule();
        r1.addSuggestion("s1");
        r1.addSuggestion("s2");

        Rule r2 = new Rule();
        r2.addSuggestion("s3");

        List<Rule> matched = Arrays.asList(r1, r2);
        List<String> result = aggregator.aggregateResponse(matched);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(Arrays.asList("s1", "s2", "s3"), result);
    }

    @Test
    void aggregateResponse_mixedNullAndNonNullSuggestions_onlyReturnsNonNullSuggestions() {
        Rule r1 = new Rule(); // no suggestions
        Rule r2 = new Rule();
        r2.addSuggestion("only");

        List<Rule> matched = Arrays.asList(r1, r2);
        List<String> result = aggregator.aggregateResponse(matched);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("only", result.get(0));
    }

    @Test
    void aggregateResponse_preservesDuplicatesAndOrder() {
        Rule r = new Rule();
        r.addSuggestion("dup");
        r.addSuggestion("dup");

        List<Rule> matched = Arrays.asList(r);
        List<String> result = aggregator.aggregateResponse(matched);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(Arrays.asList("dup", "dup"), result);
    }

    @Test
    void aggregateResponse_listContainsNullRule_throwsNpe() {
        List<Rule> matched = new ArrayList<>();
        matched.add(null);

        assertThrows(NullPointerException.class, () -> aggregator.aggregateResponse(matched));
    }
}
