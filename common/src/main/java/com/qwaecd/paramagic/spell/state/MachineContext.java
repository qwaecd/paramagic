package com.qwaecd.paramagic.spell.state;

import lombok.Getter;

public final class MachineContext {
    @Getter
    private final SpellStateMachine stateMachine;

    public MachineContext(SpellStateMachine stateMachine) {
        this.stateMachine = stateMachine;
    }
}
