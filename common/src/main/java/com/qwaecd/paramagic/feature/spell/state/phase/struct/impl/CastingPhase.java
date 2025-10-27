package com.qwaecd.paramagic.feature.spell.state.phase.struct.impl;

import com.qwaecd.paramagic.feature.spell.state.phase.struct.BasePhase;
import com.qwaecd.paramagic.feature.spell.state.phase.SpellPhase;
import com.qwaecd.paramagic.feature.spell.state.phase.PhaseConfiguration;
import com.qwaecd.paramagic.feature.spell.state.phase.SpellPhaseType;

public class CastingPhase extends BasePhase implements SpellPhase {
    private boolean isCastingComplete = false;

    public CastingPhase(PhaseConfiguration cfg) {
        super(cfg);
    }

    @Override
    public SpellPhaseType getPhaseType() {
        return SpellPhaseType.CASTING;
    }
}
