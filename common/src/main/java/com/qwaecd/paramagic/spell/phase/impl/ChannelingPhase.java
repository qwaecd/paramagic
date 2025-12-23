package com.qwaecd.paramagic.spell.phase.impl;

import com.qwaecd.paramagic.spell.config.phase.PhaseConfig;
import com.qwaecd.paramagic.spell.phase.BasePhase;
import com.qwaecd.paramagic.spell.phase.SpellPhase;
import com.qwaecd.paramagic.spell.phase.SpellPhaseType;
import com.qwaecd.paramagic.spell.state.MachineContext;
import com.qwaecd.paramagic.spell.state.Transition;
import com.qwaecd.paramagic.spell.state.AllMachineEvents;
import com.qwaecd.paramagic.spell.state.event.MachineEvent;

public class ChannelingPhase extends BasePhase implements SpellPhase {
    public ChannelingPhase(PhaseConfig cfg) {
        super(cfg);
    }

    @Override
    public Transition onEvent(final MachineContext context, MachineEvent event) {
        if (event.equals(AllMachineEvents.CHANNELING_COMPLETE)) {
            return Transition.to(SpellPhaseType.COOLDOWN);
        }
        return Transition.stay();
    }

    @Override
    public void update(final MachineContext context, float deltaTime) {
        super.update(context, deltaTime);
        if (this.phaseCompleted) {
            context.getStateMachine().postEvent(AllMachineEvents.CHANNELING_COMPLETE);
        }
    }

    @Override
    public SpellPhaseType getPhaseType() {
        return SpellPhaseType.CHANNELING;
    }
}
