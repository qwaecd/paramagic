package com.qwaecd.paramagic.spell.state.internal.event.transition;

public record TransitionEvent(String name) {
    public String get() {
        return this.name;
    }
}
