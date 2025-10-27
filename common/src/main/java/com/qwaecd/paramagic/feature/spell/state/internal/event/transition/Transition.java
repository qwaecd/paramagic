package com.qwaecd.paramagic.feature.spell.state.internal.event.transition;

import com.qwaecd.paramagic.feature.spell.state.phase.SpellPhaseType;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class Transition {
    @Nullable
    @Getter
    private final SpellPhaseType targetPhase;

    private Transition(@Nullable SpellPhaseType targetPhase) {
        this.targetPhase = targetPhase;
    }

    public static Transition to(@Nonnull SpellPhaseType targetPhase) {
        return new Transition(targetPhase);
    }
    public static Transition stay() {
        return new Transition(null);
    }
}
