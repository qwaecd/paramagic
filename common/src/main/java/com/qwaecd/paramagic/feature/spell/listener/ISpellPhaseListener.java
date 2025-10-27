package com.qwaecd.paramagic.feature.spell.listener;

import com.qwaecd.paramagic.feature.spell.state.phase.EffectTriggerPoint;
import com.qwaecd.paramagic.feature.spell.state.phase.SpellPhaseType;

public interface ISpellPhaseListener {
    void onPhaseChanged(SpellPhaseType oldPhase, SpellPhaseType newPhase);
    /**
     * 在游戏循环内的 tick.
     */
    void onTick(float deltaTime);
    void onEffectTriggered(EffectTriggerPoint triggerPoint);
    void onSpellInterrupted();
    void onSpellCompleted();
}
