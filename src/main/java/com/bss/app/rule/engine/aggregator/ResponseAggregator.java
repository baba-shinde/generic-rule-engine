package com.bss.app.rule.engine.aggregator;

import com.bss.app.rule.engine.dto.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class ResponseAggregator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseAggregator.class);

    public List<String> aggregateResponse(final List<Rule> matchedRules) {
        List<String> aggregatedResponse = null;
        LOGGER.info("Started aggregating the response for matched rules: {}", matchedRules);
        if (!CollectionUtils.isEmpty(matchedRules)) {
            aggregatedResponse = new ArrayList<>();
            for (Rule r : matchedRules) {
                List<String> suggestions = r.getSuggestions();
                if (!CollectionUtils.isEmpty(suggestions)) {
                   aggregatedResponse.addAll(suggestions);
                }
            }
        }

        LOGGER.info("Finished aggregating the response for matched rules: {}, response: {}", matchedRules, aggregatedResponse);

        return aggregatedResponse;
    }
}
