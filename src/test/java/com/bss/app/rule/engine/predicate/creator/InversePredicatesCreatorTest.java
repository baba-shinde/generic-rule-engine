package com.bss.app.rule.engine.predicate.creator;

import com.bss.app.rule.engine.dto.RuleContext;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Service;

import static org.junit.jupiter.api.Assertions.*;

public class InversePredicatesCreatorTest {

    private final InversePredicatesCreator creator = new InversePredicatesCreator();

    @Test
    public void createPredicate_withNullContext_returnsNull() {
        assertNull(creator.createPredicate(null));
    }

    @Test
    public void createPredicate_withNonNullContext_returnsNull() {
        RuleContext ctx = new RuleContext();
        ctx.setRequestId("req-1");
        assertNull(creator.createPredicate(ctx));
    }

    @Test
    public void createPredicate_multipleCalls_alwaysNull() {
        RuleContext ctx = new RuleContext();
        assertNull(creator.createPredicate(ctx));
        assertNull(creator.createPredicate(null));
    }

    @Test
    public void class_isAnnotatedWithService() {
        Service ann = InversePredicatesCreator.class.getAnnotation(Service.class);
        assertNotNull(ann, "InversePredicatesCreator should be annotated with @Service");
    }
}
