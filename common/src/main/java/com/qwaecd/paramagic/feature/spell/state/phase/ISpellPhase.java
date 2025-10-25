package com.qwaecd.paramagic.feature.spell.state.phase;

import com.qwaecd.paramagic.feature.spell.state.SpellStateMachine;

public interface ISpellPhase {
    void onEnter(SpellStateMachine stateMachine);
    void onExit(SpellStateMachine stateMachine);
    void update(SpellStateMachine stateMachine, float deltaTime);

    SpellPhaseType getPhaseType();
}
