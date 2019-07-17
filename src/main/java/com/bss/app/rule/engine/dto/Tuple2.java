package com.bss.app.rule.engine.dto;

public class Tuple2<F1, F2> {
    private final F1 f1;
    private final F2 f2;

    public Tuple2(F1 f1, F2 f2) {
        this.f1 = f1;
        this.f2 = f2;
    }

    public F1 getF1() {
        return f1;
    }

    public F2 getF2() {
        return f2;
    }
}
