package com.qwaecd.paramagic.feature.spell.state.phase.struct.impl;

import com.qwaecd.paramagic.feature.spell.state.phase.struct.BasePhase;
import com.qwaecd.paramagic.feature.spell.state.phase.SpellPhase;
import com.qwaecd.paramagic.feature.spell.state.phase.PhaseConfiguration;
import com.qwaecd.paramagic.feature.spell.state.phase.SpellPhaseType;

public class IdlePhase extends BasePhase implements SpellPhase {

    public IdlePhase(PhaseConfiguration cfg) {
        super(cfg);
    }

    @Override
    public SpellPhaseType getPhaseType() {
        return SpellPhaseType.IDLE;
    }
}
