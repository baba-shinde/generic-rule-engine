package com.bss.app.rule.engine.predicate.creator;

import com.bss.app.rule.engine.dto.Column;
import com.bss.app.rule.engine.dto.Rule;
import com.bss.app.rule.engine.dto.RuleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.function.Predicate;

@Service
public class DirectPredicatesCreator implements PredicatesCreator<RuleContext, Rule> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DirectPredicatesCreator.class);

    @Override
    public Predicate<Rule> createPredicate(final RuleContext ruleContext) {
        LOGGER.info("Started creating predicate for context: {}", ruleContext);
        Predicate<Rule> rulePredicate = new Predicate<Rule>() {
            @Override
            public boolean test(Rule rule) {
                return true;
            }
        };

        if (ruleContext != null && !CollectionUtils.isEmpty(ruleContext.getColumns())) {

            Map<String, Column> columns = ruleContext.getColumns();
            for (Map.Entry<String, Column> e: columns.entrySet()) {
                String inColumnName = e.getKey();
                rulePredicate = rulePredicate.and(new Predicate<Rule>() {
                    @Override
                    public boolean test(final Rule rule) {
                        boolean ret = false;
                        if (rule != null
                                && !CollectionUtils.isEmpty(rule.getColumns())
                                && rule.getColumns().get(inColumnName)!= null
                                && rule.getColumns().get(inColumnName).getValue() != null
                                && e.getValue() != null
                                && e.getValue().getValue() != null
                                && (rule.getColumns().get(inColumnName).getValue()).equals(e.getValue().getValue())){

                            ret = true;
                        }

                        return ret;
                    }
                });
            }

        }
        LOGGER.info("Finished creating predicate for context: {}, details: {}", ruleContext, rulePredicate);

        return rulePredicate;
    }
}