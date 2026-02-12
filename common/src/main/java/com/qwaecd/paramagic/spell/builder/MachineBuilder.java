package com.qwaecd.paramagic.spell.builder;

import com.qwaecd.paramagic.spell.config.PhaseConfig;
import com.qwaecd.paramagic.spell.phase.PhaseConnection;
import com.qwaecd.paramagic.spell.phase.PhaseFactory;
import com.qwaecd.paramagic.spell.phase.SpellPhase;
import com.qwaecd.paramagic.spell.phase.SpellPhaseType;
import com.qwaecd.paramagic.spell.state.SpellStateMachine;

import javax.annotation.Nonnull;

public class MachineBuilder {
    private final PhaseConnection connection;

    public MachineBuilder(PhaseConfig initialPhaseConfig) {
        this(PhaseFactory.createPhaseFromConfig(initialPhaseConfig));
    }

    public MachineBuilder(@Nonnull SpellPhase initialPhase) {
        this.connection = new PhaseConnection(initialPhase);
    }

    public MachineBuilder addPhase(SpellPhase phase) {
        if (this.connection.containsPhase(phase.getPhaseType())) {
            throw new IllegalArgumentException("Phase type " + phase.getPhaseType() + " already exists in the machine.");
        }

        this.connection.addPhase(phase);
        return this;
    }

    public MachineBuilder addPhase(PhaseConfig cfg) {
        return this.addPhase(PhaseFactory.createPhaseFromConfig(cfg));
    }

    public MachineBuilder phase(SpellPhaseType type, float duration) {
        return this.addPhase(PhaseConfig.create(type, duration));
    }

    public SpellStateMachine build() {
        return new SpellStateMachine(this.connection);
    }
}
