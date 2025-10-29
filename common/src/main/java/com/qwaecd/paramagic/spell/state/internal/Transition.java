package com.qwaecd.paramagic.spell.state.internal;

import com.qwaecd.paramagic.spell.state.phase.property.SpellPhaseType;
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
