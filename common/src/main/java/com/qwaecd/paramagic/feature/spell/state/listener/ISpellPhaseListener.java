package com.qwaecd.paramagic.feature.spell.state.listener;

import com.qwaecd.paramagic.feature.spell.state.phase.SpellPhaseType;

public interface ISpellPhaseListener {
    void onPhaseChanged(SpellPhaseType oldPhase, SpellPhaseType newPhase);
    void onProgressUpdated(float progress);
    void onEffectTriggered();
    void onSpellInterrupted();
    void onSpellCompleted();
}
