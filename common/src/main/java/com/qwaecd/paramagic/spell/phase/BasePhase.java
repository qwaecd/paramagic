package com.qwaecd.paramagic.spell.phase;

import com.qwaecd.paramagic.spell.config.phase.PhaseConfig;
import com.qwaecd.paramagic.spell.state.MachineContext;

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
    public PhaseConfig getConfig() {
        return this.config;
    }

    @Override
    public SpellPhaseType getPhaseType() {
        return this.config.getPhaseType();
    }
}
