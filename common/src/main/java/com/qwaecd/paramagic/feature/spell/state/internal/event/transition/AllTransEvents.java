package com.qwaecd.paramagic.feature.spell.state.internal.event.transition;

public final class AllTransEvents {
    private AllTransEvents() {}

    public static final TransitionEvent NEXT = new TransitionEvent("next");
    public static final TransitionEvent INTERRUPT = new TransitionEvent("interrupt");
}
