package com.qwaecd.paramagic.spell.state.phase.struct.impl;

import com.qwaecd.paramagic.spell.state.phase.struct.BasePhase;
import com.qwaecd.paramagic.spell.state.phase.SpellPhase;
import com.qwaecd.paramagic.spell.state.phase.PhaseConfig;
import com.qwaecd.paramagic.spell.state.phase.SpellPhaseType;

public class CastingPhase extends BasePhase implements SpellPhase {
    private boolean isCastingComplete = false;

    public CastingPhase(PhaseConfig cfg) {
        super(cfg);
    }

    @Override
    public SpellPhaseType getPhaseType() {
        return SpellPhaseType.CASTING;
    }
}
