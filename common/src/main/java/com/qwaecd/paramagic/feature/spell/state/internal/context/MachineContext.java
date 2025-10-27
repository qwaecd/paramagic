package com.qwaecd.paramagic.feature.spell.state.internal.context;

import com.qwaecd.paramagic.feature.spell.state.SpellStateMachine;
import lombok.Getter;

public final class MachineContext {
    @Getter
    private final SpellStateMachine stateMachine;

    public MachineContext(SpellStateMachine stateMachine) {
        this.stateMachine = stateMachine;
    }
}
