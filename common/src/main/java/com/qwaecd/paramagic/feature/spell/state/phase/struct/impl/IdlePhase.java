package com.qwaecd.paramagic.feature.spell.state.phase.struct.impl;

import com.qwaecd.paramagic.feature.spell.state.SpellStateMachine;
import com.qwaecd.paramagic.feature.spell.state.phase.struct.BasePhase;
import com.qwaecd.paramagic.feature.spell.state.phase.ISpellPhase;
import com.qwaecd.paramagic.feature.spell.state.phase.PhaseConfiguration;
import com.qwaecd.paramagic.feature.spell.state.phase.SpellPhaseType;

public class IdlePhase extends BasePhase implements ISpellPhase {

    public IdlePhase(PhaseConfiguration cfg) {
        super(cfg);
    }

    @Override
    public void update(SpellStateMachine stateMachine, float deltaTime) {
    }

    @Override
    public SpellPhaseType getPhaseType() {
        return SpellPhaseType.IDLE;
    }
}
