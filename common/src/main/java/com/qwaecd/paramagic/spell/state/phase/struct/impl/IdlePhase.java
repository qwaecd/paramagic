package com.qwaecd.paramagic.spell.state.phase.struct.impl;

import com.qwaecd.paramagic.spell.state.phase.struct.BasePhase;
import com.qwaecd.paramagic.spell.state.phase.SpellPhase;
import com.qwaecd.paramagic.spell.state.phase.property.PhaseConfig;
import com.qwaecd.paramagic.spell.state.phase.property.SpellPhaseType;

public class IdlePhase extends BasePhase implements SpellPhase {

    public IdlePhase(PhaseConfig cfg) {
        super(cfg);
    }

    @Override
    public SpellPhaseType getPhaseType() {
        return SpellPhaseType.IDLE;
    }
}
