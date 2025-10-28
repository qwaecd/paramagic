package com.qwaecd.paramagic.spell.state.internal;

import com.qwaecd.paramagic.spell.state.SpellStateMachine;
import lombok.Getter;

public final class MachineContext {
    @Getter
    private final SpellStateMachine stateMachine;

    public MachineContext(SpellStateMachine stateMachine) {
        this.stateMachine = stateMachine;
    }
}
