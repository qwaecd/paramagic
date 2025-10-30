package com.qwaecd.paramagic.spell.state.phase.struct;

import com.qwaecd.paramagic.spell.state.internal.MachineContext;
import com.qwaecd.paramagic.spell.state.internal.event.AllMachineEvents;
import com.qwaecd.paramagic.spell.state.phase.EffectTriggerPoint;
import com.qwaecd.paramagic.spell.state.phase.SpellPhase;
import com.qwaecd.paramagic.spell.state.phase.property.PhaseConfig;
import com.qwaecd.paramagic.spell.state.phase.property.SpellPhaseType;

public abstract class BasePhase implements SpellPhase {
    protected final PhaseConfig config;
    protected float phaseTime;
    protected boolean phaseCompleted = false;

    protected BasePhase(PhaseConfig cfg) {
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
        if (this.config.getDuration() > 0.0f && this.phaseTime >= this.config.getDuration() && !this.phaseCompleted) {
            this.phaseCompleted = true;
        }
    }

    @Override
    public SpellPhaseType getPhaseType() {
        return this.config.getPhaseType();
    }
}
