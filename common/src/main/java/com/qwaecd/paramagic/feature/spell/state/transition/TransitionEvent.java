package com.qwaecd.paramagic.feature.spell.state.transition;

public record TransitionEvent(String name) {
    public String get() {
        return this.name;
    }
}
