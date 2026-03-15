package com.bss.app.rule.engine.predicate.creator;

import com.bss.app.rule.engine.dto.Column;
import com.bss.app.rule.engine.dto.Rule;
import com.bss.app.rule.engine.dto.RuleContext;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

class DirectPredicatesCreatorTest {

    private final DirectPredicatesCreator creator = new DirectPredicatesCreator();

    @Test
    void createPredicate_nullContext_returnsAlwaysTrue() {
        Predicate<Rule> predicate = creator.createPredicate(null);
        // initial predicate returns true for any input
        assertTrue(predicate.test(null));

        Rule r = new Rule();
        r.addColumn(new Column("c", "v"));
        assertTrue(predicate.test(r));
    }

    @Test
    void createPredicate_emptyColumns_returnsAlwaysTrue() {
        RuleContext ctx = new RuleContext();
        // no columns set
        Predicate<Rule> predicate = creator.createPredicate(ctx);
        assertTrue(predicate.test(null));

        Rule r = new Rule();
        r.addColumn(new Column("x", "y"));
        assertTrue(predicate.test(r));
    }

    @Test
    void createPredicate_singleColumn_matchTrue() {
        RuleContext ctx = new RuleContext();
        ctx.addColumn(new Column("col1", "val1"));

        Predicate<Rule> predicate = creator.createPredicate(ctx);

        Rule r = new Rule();
        r.addColumn(new Column("col1", "val1"));

        assertTrue(predicate.test(r));
    }

    @Test
    void createPredicate_singleColumn_ruleNull_false() {
        RuleContext ctx = new RuleContext();
        ctx.addColumn(new Column("col1", "val1"));

        Predicate<Rule> predicate = creator.createPredicate(ctx);

        assertFalse(predicate.test(null));
    }

    @Test
    void createPredicate_singleColumn_missingColumn_false() {
        RuleContext ctx = new RuleContext();
        ctx.addColumn(new Column("col1", "val1"));

        Predicate<Rule> predicate = creator.createPredicate(ctx);

        Rule r = new Rule();
        // different column name -> should fail
        r.addColumn(new Column("other", "val1"));

        assertFalse(predicate.test(r));
    }

    @Test
    void createPredicate_contextColumnIsNull_false() {
        RuleContext ctx = new RuleContext();
        Map<String, Column> m = new HashMap<>();
        // put null value in context map to exercise e.getValue() == null branch
        m.put("col1", null);
        ctx.setColumns(m);

        Predicate<Rule> predicate = creator.createPredicate(ctx);

        Rule r = new Rule();
        r.addColumn(new Column("col1", "val1"));

        assertFalse(predicate.test(r));
    }

    @Test
    void createPredicate_ruleColumnValueNull_false() {
        RuleContext ctx = new RuleContext();
        ctx.addColumn(new Column("col1", "val1"));

        Predicate<Rule> predicate = creator.createPredicate(ctx);

        Rule r = new Rule();
        // rule column value is null -> should fail check for non-null rule value
        r.addColumn(new Column("col1", null));

        assertFalse(predicate.test(r));
    }

    @Test
    void createPredicate_valueNotEqual_false() {
        RuleContext ctx = new RuleContext();
        ctx.addColumn(new Column("col1", "val1"));

        Predicate<Rule> predicate = creator.createPredicate(ctx);

        Rule r = new Rule();
        r.addColumn(new Column("col1", "different"));

        assertFalse(predicate.test(r));
    }

    @Test
    void createPredicate_multipleColumns_allMatch_true() {
        RuleContext ctx = new RuleContext();
        ctx.addColumn(new Column("c1", "v1"));
        ctx.addColumn(new Column("c2", "v2"));

        Predicate<Rule> predicate = creator.createPredicate(ctx);

        Rule r = new Rule();
        r.addColumn(new Column("c1", "v1"));
        r.addColumn(new Column("c2", "v2"));

        assertTrue(predicate.test(r));
    }

    @Test
    void createPredicate_multipleColumns_oneMismatch_false() {
        RuleContext ctx = new RuleContext();
        ctx.addColumn(new Column("c1", "v1"));
        ctx.addColumn(new Column("c2", "v2"));

        Predicate<Rule> predicate = creator.createPredicate(ctx);

        Rule r = new Rule();
        r.addColumn(new Column("c1", "v1"));
        r.addColumn(new Column("c2", "wrong"));

        assertFalse(predicate.test(r));
    }
}
