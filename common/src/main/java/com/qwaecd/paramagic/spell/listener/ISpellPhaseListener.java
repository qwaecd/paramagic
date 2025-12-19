package com.qwaecd.paramagic.spell.listener;

import com.qwaecd.paramagic.spell.phase.EffectTriggerPoint;
import com.qwaecd.paramagic.spell.phase.SpellPhaseType;

public interface ISpellPhaseListener {
    void onPhaseChanged(SpellPhaseType oldPhase, SpellPhaseType currentPhase);
    /**
     * 在游戏循环内的 tick.
     */
    default void onTick(SpellPhaseType currentPhase, float deltaTime) {}
    default void onEffectTriggered(EffectTriggerPoint triggerPoint) {}
    void onSpellInterrupted();
    void onSpellCompleted();
}
