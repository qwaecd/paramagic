package com.qwaecd.paramagic.spell.api;

import com.qwaecd.paramagic.spell.phase.EffectTriggerPoint;
import com.qwaecd.paramagic.spell.phase.SpellPhaseType;

import javax.annotation.Nullable;

public interface SpellPhaseListener {
    void onPhaseChanged(SpellPhaseType oldPhase, SpellPhaseType currentPhase);
    /**
     * 在游戏循环内的 tick.
     */
    default void onTick(SpellPhaseType currentPhase, float deltaTime) {}
    default void onEffectTriggered(EffectTriggerPoint triggerPoint, @Nullable SpellPhaseType currentPhase) {}
    void onSpellInterrupted();
    void onSpellCompleted();
}
