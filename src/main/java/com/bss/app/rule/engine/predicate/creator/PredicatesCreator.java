package com.bss.app.rule.engine.predicate.creator;

import java.util.function.Predicate;

public interface PredicatesCreator<I, O> {
    Predicate<O> createPredicate(final I i);
}