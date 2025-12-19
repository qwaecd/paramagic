package com.qwaecd.paramagic.spell.state.phase.struct.impl;

import com.qwaecd.paramagic.spell.state.MachineContext;
import com.qwaecd.paramagic.spell.state.Transition;
import com.qwaecd.paramagic.spell.state.event.AllMachineEvents;
import com.qwaecd.paramagic.spell.state.event.MachineEvent;
import com.qwaecd.paramagic.spell.state.phase.SpellPhase;
import com.qwaecd.paramagic.spell.state.phase.struct.PhaseConfig;
import com.qwaecd.paramagic.spell.state.phase.struct.SpellPhaseType;
import com.qwaecd.paramagic.spell.state.phase.struct.BasePhase;

public class CastingPhase extends BasePhase implements SpellPhase {
    public CastingPhase(PhaseConfig cfg) {
        super(cfg);
    }

    @Override
    public Transition onEvent(MachineContext context, MachineEvent event) {
        if (!checkResource()) {
            // 缺少资源, 回到待机阶段
            return Transition.to(SpellPhaseType.IDLE);
        }

        if (event.equals(AllMachineEvents.CASTING_COMPLETE)) {
            return Transition.to(SpellPhaseType.CHANNELING);
        }
        return Transition.stay();
    }

    @Override
    public void update(final MachineContext context, float deltaTime) {
        super.update(context, deltaTime);
        if (this.phaseCompleted) {
            context.getStateMachine().postEvent(AllMachineEvents.CASTING_COMPLETE);
        }
    }

    private boolean checkResource() {
        return true;
    }

    @Override
    public SpellPhaseType getPhaseType() {
        return SpellPhaseType.CASTING;
    }
}
