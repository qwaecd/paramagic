package com.qwaecd.paramagic.feature.spell.state.internal.event.machine;


public enum EndSpellReason {
    COMPLETED("completed"),
    INTERRUPTED("interrupted"),
    FAILED("failed");
    private final String name;
    EndSpellReason(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
    }
}
