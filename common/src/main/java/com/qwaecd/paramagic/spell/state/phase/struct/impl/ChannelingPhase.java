package com.qwaecd.paramagic.spell.state.phase.struct.impl;

import com.qwaecd.paramagic.spell.state.internal.MachineContext;
import com.qwaecd.paramagic.spell.state.internal.Transition;
import com.qwaecd.paramagic.spell.state.internal.event.AllMachineEvents;
import com.qwaecd.paramagic.spell.state.internal.event.MachineEvent;
import com.qwaecd.paramagic.spell.state.phase.SpellPhase;
import com.qwaecd.paramagic.spell.state.phase.property.PhaseConfig;
import com.qwaecd.paramagic.spell.state.phase.property.SpellPhaseType;
import com.qwaecd.paramagic.spell.state.phase.struct.BasePhase;

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
