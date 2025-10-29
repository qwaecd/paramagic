package com.qwaecd.paramagic.spell.state.phase.struct.impl;

import com.qwaecd.paramagic.spell.state.internal.MachineContext;
import com.qwaecd.paramagic.spell.state.internal.Transition;
import com.qwaecd.paramagic.spell.state.internal.event.MachineEvent;
import com.qwaecd.paramagic.spell.state.phase.struct.BasePhase;
import com.qwaecd.paramagic.spell.state.phase.SpellPhase;
import com.qwaecd.paramagic.spell.state.phase.property.PhaseConfig;
import com.qwaecd.paramagic.spell.state.phase.property.SpellPhaseType;

public class CastingPhase extends BasePhase implements SpellPhase {
    private boolean isCastingComplete = false;

    public CastingPhase(PhaseConfig cfg) {
        super(cfg);
    }

    @Override
    public Transition onEvent(MachineContext context, MachineEvent event) {
        return Transition.stay();
    }

    @Override
    public SpellPhaseType getPhaseType() {
        return SpellPhaseType.CASTING;
    }
}
