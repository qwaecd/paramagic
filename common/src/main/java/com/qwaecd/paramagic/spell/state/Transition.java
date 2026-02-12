package com.qwaecd.paramagic.spell.state;

import com.qwaecd.paramagic.spell.phase.SpellPhaseType;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Transition that = (Transition) obj;
        return Objects.equals(targetPhase, that.targetPhase);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetPhase);
    }
}
