package com.qwaecd.paramagic.feature.spell.state.phase;

import com.qwaecd.paramagic.feature.spell.state.internal.context.MachineContext;
import com.qwaecd.paramagic.feature.spell.state.internal.event.MachineEvent;
import com.qwaecd.paramagic.feature.spell.state.internal.event.transition.Transition;

public interface SpellPhase {
    Transition onEvent(final MachineContext context, MachineEvent event);
    void onEnter(final MachineContext context);
    void onExit(final MachineContext context);
    void update(final MachineContext context, float deltaTime);

    SpellPhaseType getPhaseType();
}
