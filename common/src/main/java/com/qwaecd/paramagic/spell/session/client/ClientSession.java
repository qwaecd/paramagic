package com.qwaecd.paramagic.spell.session.client;

import com.qwaecd.paramagic.spell.Spell;
import com.qwaecd.paramagic.spell.session.SpellSession;
import com.qwaecd.paramagic.spell.state.SpellStateMachine;

import java.util.UUID;

public class ClientSession extends SpellSession {
    private final SpellStateMachine machine;

    public ClientSession(UUID sessionId, Spell spell) {
        super(sessionId, spell);
        this.machine = new SpellStateMachine(spell.getSpellConfig());
    }

    @Override
    public void tick(float deltaTime) {
        this.machine.update(deltaTime);
    }

    @Override
    public void interrupt() {
        this.machine.interrupt();
    }

    @Override
    public void forceInterrupt() {
        this.machine.forceInterrupt();
    }
}
