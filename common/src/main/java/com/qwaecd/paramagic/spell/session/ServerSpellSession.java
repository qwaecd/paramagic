package com.qwaecd.paramagic.spell.session;

import com.qwaecd.paramagic.spell.state.SpellStateMachine;

public class ServerSpellSession extends SpellSession {
    private SpellStateMachine machine;

    public ServerSpellSession() {
    }

    @Override
    public void tick(float deltaTime) {
        if (this.machine != null) {
            this.machine.update(deltaTime);
        }
    }
}
