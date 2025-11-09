package com.qwaecd.paramagic.spell.listener;

import com.qwaecd.paramagic.spell.state.phase.EffectTriggerPoint;
import com.qwaecd.paramagic.spell.state.phase.property.SpellPhaseType;

public interface ISpellPhaseListener {
    void onPhaseChanged(SpellPhaseType oldPhase, SpellPhaseType currentPhase);
    /**
     * 在游戏循环内的 tick.
     */
    void onTick(SpellPhaseType currentPhase, float deltaTime);
    void onEffectTriggered(EffectTriggerPoint triggerPoint);
    void onSpellInterrupted();
    void onSpellCompleted();
}
