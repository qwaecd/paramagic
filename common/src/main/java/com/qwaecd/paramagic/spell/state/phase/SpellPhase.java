package com.qwaecd.paramagic.spell.state.phase;

import com.qwaecd.paramagic.spell.state.MachineContext;
import com.qwaecd.paramagic.spell.state.Transition;
import com.qwaecd.paramagic.spell.state.event.MachineEvent;
import com.qwaecd.paramagic.spell.state.phase.property.SpellPhaseType;

public interface SpellPhase {
    Transition onEvent(final MachineContext context, MachineEvent event);
    void onEnter(final MachineContext context);
    void onExit(final MachineContext context);
    void update(final MachineContext context, float deltaTime);

    SpellPhaseType getPhaseType();
}
