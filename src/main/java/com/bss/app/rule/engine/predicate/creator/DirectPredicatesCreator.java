package com.bss.app.rule.engine.predicate.creator;

import com.bss.app.rule.engine.dto.Rule;
import com.bss.app.rule.engine.dto.RuleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.function.Predicate;

@Service
public class DirectPredicatesCreator implements PredicatesCreator<RuleContext, Rule> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DirectPredicatesCreator.class);

    @Override
    public Predicate<Rule> createPredicate(RuleContext ruleContext) {
        return null;
    }
}