package com.qwaecd.paramagic.spell.state.phase.struct.impl;

import com.qwaecd.paramagic.spell.state.MachineContext;
import com.qwaecd.paramagic.spell.state.Transition;
import com.qwaecd.paramagic.spell.state.event.AllMachineEvents;
import com.qwaecd.paramagic.spell.state.event.MachineEvent;
import com.qwaecd.paramagic.spell.state.phase.SpellPhase;
import com.qwaecd.paramagic.spell.state.phase.property.PhaseConfig;
import com.qwaecd.paramagic.spell.state.phase.property.SpellPhaseType;
import com.qwaecd.paramagic.spell.state.phase.struct.BasePhase;

public class CooldownPhase extends BasePhase implements SpellPhase {
    public CooldownPhase(PhaseConfig cfg) {
        super(cfg);
    }

    @Override
    public Transition onEvent(MachineContext context, MachineEvent event) {
        return Transition.stay();
    }

    @Override
    public void update(final MachineContext context, float deltaTime) {
        context.getStateMachine().postEvent(AllMachineEvents.END_SPELL);
    }

    @Override
    public SpellPhaseType getPhaseType() {
        return SpellPhaseType.COOLDOWN;
    }
}
