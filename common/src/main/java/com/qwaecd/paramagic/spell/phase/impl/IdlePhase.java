package com.qwaecd.paramagic.spell.phase.impl;

import com.qwaecd.paramagic.spell.config.PhaseConfig;
import com.qwaecd.paramagic.spell.phase.BasePhase;
import com.qwaecd.paramagic.spell.phase.SpellPhase;
import com.qwaecd.paramagic.spell.phase.SpellPhaseType;
import com.qwaecd.paramagic.spell.state.AllMachineEvents;
import com.qwaecd.paramagic.spell.state.MachineContext;
import com.qwaecd.paramagic.spell.state.Transition;
import com.qwaecd.paramagic.spell.state.event.MachineEvent;

public class IdlePhase extends BasePhase implements SpellPhase {

    public IdlePhase(PhaseConfig cfg) {
        super(cfg);
    }

    @Override
    public Transition onEvent(MachineContext context, MachineEvent event) {
        // 不需要考虑该阶段是否结束, 只要收到开始施法的事件就切换到施法阶段
        if (event.equals(AllMachineEvents.START_CASTING)) {
            return Transition.to(SpellPhaseType.CASTING);
        }
        if (this.phaseCompleted) {
            return Transition.to(SpellPhaseType.CASTING);
        }
        return Transition.stay();
    }

    @Override
    public void update(final MachineContext context, float deltaTime) {
        super.update(context, deltaTime);
        if (this.phaseCompleted) {
            context.getStateMachine().postEvent(AllMachineEvents.START_CASTING);
        }
    }

    @Override
    public SpellPhaseType getPhaseType() {
        return SpellPhaseType.IDLE;
    }
}
