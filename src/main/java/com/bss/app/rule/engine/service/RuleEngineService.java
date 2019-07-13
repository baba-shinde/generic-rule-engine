package com.bss.app.rule.engine.service;

import com.bss.app.rule.engine.aggregator.ResponseAggregator;
import com.bss.app.rule.engine.dto.Rule;
import com.bss.app.rule.engine.dto.RuleContext;
import com.bss.app.rule.engine.loader.RulesLoader;
import com.bss.app.rule.engine.predicate.creator.PredicatesCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class RuleEngineService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RuleEngineService.class);

    @Qualifier("directPredicatesCreator")
    @Autowired
    private PredicatesCreator predicatesCreator;
    @Autowired
    private RulesLoader rulesLoader;
    @Autowired
    private ResponseAggregator responseAggregator;

    /**
     * Following steps are followed during processing
     * <li>1. Create Predicate out of RuleContext</li>
     * <li>2. Get loaded rules from {@link com.bss.app.rule.engine.loader.RulesLoader}</li>
     * <li>3. Apply predicates on the loaded list of rules</li>
     * <li>4. Get the resultant/matching rules and aggregate response with the help of {@link com.bss.app.rule.engine.aggregator.ResponseAggregator}</li>
     * <li>5. Return consolidated rules response</li>
     *
     * @param ruleContext
     * @return
     */
    public List<String> processRequest(final RuleContext ruleContext) {
        LOGGER.info("Started processing request, context: {}", ruleContext);
        List<String> response = null;
        //Creating predicates
        Predicate<Rule> predicate = predicatesCreator.createPredicate(ruleContext);
        //Load rules
        List<Rule> allRules = rulesLoader.getRules();
        //Filter
        List<Rule> matchedRules = allRules.stream().filter(predicate).collect(Collectors.toList());
        LOGGER.info("Matched rules:{} found for context: {}", matchedRules, ruleContext);
        //Response Aggregator
        response = responseAggregator.aggregateResponse(matchedRules);

        LOGGER.info("Finished processing request, context: {}, result: {}", ruleContext, response);

        return response;
    }
}