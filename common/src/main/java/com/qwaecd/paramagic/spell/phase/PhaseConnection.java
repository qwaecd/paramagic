package com.qwaecd.paramagic.spell.phase;

import lombok.Getter;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;

public final class PhaseConnection {
    private final Map<SpellPhaseType, SpellPhase> phases = new EnumMap<>(SpellPhaseType.class);
    @Getter
    private final SpellPhase initialPhase;

    public PhaseConnection(SpellPhase initialPhase) {
        this.initialPhase = initialPhase;
        this.phases.put(initialPhase.getPhaseType(), initialPhase);
    }

    @Nullable
    public SpellPhase getPhase(SpellPhaseType type) {
        return this.phases.get(type);
    }

    public void addPhase(SpellPhase phase) {
        this.phases.put(phase.getPhaseType(), phase);
    }

    public void removePhase(SpellPhaseType type) {
        this.phases.remove(type);
    }

    public boolean containsPhase(SpellPhaseType type) {
        return this.phases.containsKey(type);
    }
}
