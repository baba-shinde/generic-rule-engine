package com.bss.app.rule.engine.service;

import com.bss.app.rule.engine.aggregator.ResponseAggregator;
import com.bss.app.rule.engine.dto.Rule;
import com.bss.app.rule.engine.dto.RuleContext;
import com.bss.app.rule.engine.loader.RulesLoader;
import com.bss.app.rule.engine.predicate.creator.PredicatesCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RuleEngineServiceTest {

    private RuleEngineService service;
    private PredicatesCreator predicatesCreator;
    private RulesLoader rulesLoader;
    private ResponseAggregator responseAggregator;

    @BeforeEach
    public void setUp() throws Exception {
        service = new RuleEngineService();
        predicatesCreator = mock(PredicatesCreator.class);
        rulesLoader = mock(RulesLoader.class);
        responseAggregator = new ResponseAggregator(); // use real aggregator to verify aggregation

        setField(service, "predicatesCreator", predicatesCreator);
        setField(service, "rulesLoader", rulesLoader);
        setField(service, "responseAggregator", responseAggregator);
    }

    private void setField(Object target, String name, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(target, value);
    }

    @Test
    public void testProcessRequest_withMatches_returnsAggregatedSuggestions() {
        RuleContext ctx = new RuleContext();

        Rule r1 = new Rule();
        r1.setId("r1");
        r1.addSuggestion("s1");

        Rule r2 = new Rule();
        r2.setId("r2");
        r2.addSuggestion("s2");

        List<Rule> allRules = Arrays.asList(r1, r2);

        // predicate that matches all rules
        @SuppressWarnings("unchecked")
        Predicate<Rule> matchAll = (Predicate<Rule>) rule -> true;

        when(predicatesCreator.createPredicate(ctx)).thenReturn(matchAll);
        when(rulesLoader.getRules()).thenReturn(allRules);

        List<String> result = service.processRequest(ctx);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(Arrays.asList("s1", "s2"), result);
    }

    @Test
    public void testProcessRequest_withNoMatches_returnsNull() {
        RuleContext ctx = new RuleContext();

        Rule r = new Rule();
        r.setId("r1");
        r.addSuggestion("s1");

        List<Rule> allRules = Collections.singletonList(r);

        @SuppressWarnings("unchecked")
        Predicate<Rule> matchNone = (Predicate<Rule>) rule -> false;

        when(predicatesCreator.createPredicate(ctx)).thenReturn(matchNone);
        when(rulesLoader.getRules()).thenReturn(allRules);

        List<String> result = service.processRequest(ctx);

        assertNull(result);
    }

    @Test
    public void testProcessRequest_withNullRules_throwsNPE() {
        RuleContext ctx = new RuleContext();

        @SuppressWarnings("unchecked")
        Predicate<Rule> matchAll = (Predicate<Rule>) rule -> true;

        when(predicatesCreator.createPredicate(ctx)).thenReturn(matchAll);
        when(rulesLoader.getRules()).thenReturn(null);

        assertThrows(NullPointerException.class, () -> service.processRequest(ctx));
    }

    @Test
    public void testProcessRequest_predicateThrows_propagates() {
        RuleContext ctx = new RuleContext();

        @SuppressWarnings("unchecked")
        Predicate<Rule> throwingPredicate = (Predicate<Rule>) rule -> { throw new RuntimeException("boom"); };

        when(predicatesCreator.createPredicate(ctx)).thenReturn(throwingPredicate);
        when(rulesLoader.getRules()).thenReturn(Arrays.asList(new Rule()));

        assertThrows(RuntimeException.class, () -> service.processRequest(ctx));
    }
}
