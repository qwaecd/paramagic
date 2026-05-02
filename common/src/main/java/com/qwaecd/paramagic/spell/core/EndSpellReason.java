package com.qwaecd.paramagic.spell.core;


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

    public static EndSpellReason fromName(String name) {
        for (EndSpellReason reason : values()) {
            if (reason.name.equals(name)) {
                return reason;
            }
        }
        throw new IllegalArgumentException("Unknown EndSpellReason: " + name);
    }
}
