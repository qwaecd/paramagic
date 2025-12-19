package com.qwaecd.paramagic.spell.state;

import com.qwaecd.paramagic.spell.phase.SpellPhaseType;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class Transition {
    public static final Transition STAY = new Transition(null);
    @Getter
    @Nullable
    private final SpellPhaseType targetPhase;

    private Transition(@Nullable SpellPhaseType targetPhase) {
        this.targetPhase = targetPhase;
    }

    public static Transition to(@Nonnull SpellPhaseType targetPhase) {
        return new Transition(targetPhase);
    }
    public static Transition stay() {
        return Transition.STAY;
    }
}
