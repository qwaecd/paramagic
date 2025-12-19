package com.qwaecd.paramagic.spell.phase.impl;

import com.qwaecd.paramagic.spell.state.MachineContext;
import com.qwaecd.paramagic.spell.state.Transition;
import com.qwaecd.paramagic.spell.state.event.AllMachineEvents;
import com.qwaecd.paramagic.spell.state.event.MachineEvent;
import com.qwaecd.paramagic.spell.phase.SpellPhase;
import com.qwaecd.paramagic.spell.config.PhaseConfig;
import com.qwaecd.paramagic.spell.phase.SpellPhaseType;
import com.qwaecd.paramagic.spell.phase.BasePhase;

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
