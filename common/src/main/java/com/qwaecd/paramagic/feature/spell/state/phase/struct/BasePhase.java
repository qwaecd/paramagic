package com.qwaecd.paramagic.feature.spell.state.phase.struct;

import com.qwaecd.paramagic.feature.spell.state.SpellStateMachine;
import com.qwaecd.paramagic.feature.spell.state.phase.ISpellPhase;
import com.qwaecd.paramagic.feature.spell.state.phase.PhaseConfiguration;
import com.qwaecd.paramagic.feature.spell.state.phase.SpellPhaseType;

public abstract class BasePhase implements ISpellPhase {
    protected final PhaseConfiguration config;
    protected float phaseTime;

    protected BasePhase(PhaseConfiguration cfg) {
        this.config = cfg;
    }
    @Override
    public void onEnter(SpellStateMachine stateMachine) {
        this.phaseTime = 0.0f;
    }

    @Override
    public void onExit(SpellStateMachine stateMachine) {
    }

    @Override
    public void update(SpellStateMachine stateMachine, float deltaTime) {
        this.phaseTime += deltaTime;
        if (this.config.getDuration() > 0.0f && this.phaseTime >= this.config.getDuration()) {
            stateMachine.requestNextPhase();
        }
    }

    @Override
    public SpellPhaseType getPhaseType() {
        return this.config.getPhaseType();
    }
}
