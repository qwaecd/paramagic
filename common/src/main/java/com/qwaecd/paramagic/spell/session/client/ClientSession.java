package com.qwaecd.paramagic.spell.session.client;

import com.qwaecd.paramagic.spell.Spell;
import com.qwaecd.paramagic.spell.session.SessionState;
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
        if (!this.machineCompleted())
            this.machine.update(deltaTime);
        if (isState(SessionState.INTERRUPTED) || isState(SessionState.FINISHED_LOGICALLY)) {
            // TODO: 可以实现延迟销毁
            this.setSessionState(SessionState.DISPOSED);
        }
    }

    @Override
    public void interrupt() {
        this.setSessionState(SessionState.INTERRUPTED);
        this.machine.interrupt();
    }

    @Override
    public void forceInterrupt() {
        this.setSessionState(SessionState.INTERRUPTED);
        this.machine.forceInterrupt();
    }

    public boolean machineCompleted() {
        return this.machine.isCompleted();
    }
}
