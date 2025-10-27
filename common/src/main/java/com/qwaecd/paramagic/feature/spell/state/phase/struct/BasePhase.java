package com.qwaecd.paramagic.feature.spell.state.phase.struct;

import com.qwaecd.paramagic.feature.spell.state.internal.context.MachineContext;
import com.qwaecd.paramagic.feature.spell.state.phase.EffectTriggerPoint;
import com.qwaecd.paramagic.feature.spell.state.phase.SpellPhase;
import com.qwaecd.paramagic.feature.spell.state.phase.PhaseConfiguration;
import com.qwaecd.paramagic.feature.spell.state.phase.SpellPhaseType;

public abstract class BasePhase implements SpellPhase {
    protected final PhaseConfiguration config;
    protected float phaseTime;

    protected BasePhase(PhaseConfiguration cfg) {
        this.config = cfg;
    }
    @Override
    public void onEnter(final MachineContext context) {
        this.phaseTime = 0.0f;
        context.getStateMachine().triggerEffect(EffectTriggerPoint.ON_ENTER);
    }

    @Override
    public void onExit(final MachineContext context) {
        context.getStateMachine().triggerEffect(EffectTriggerPoint.ON_EXIT);
    }

    @Override
    public void update(final MachineContext context, float deltaTime) {
        this.phaseTime += deltaTime;
        if (this.config.getDuration() > 0.0f && this.phaseTime >= this.config.getDuration()) {
            context.getStateMachine().requestNextPhase();
        }
    }

    @Override
    public SpellPhaseType getPhaseType() {
        return this.config.getPhaseType();
    }
}
