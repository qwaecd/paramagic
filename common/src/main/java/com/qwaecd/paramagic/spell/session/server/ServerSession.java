package com.qwaecd.paramagic.spell.session.server;

import com.qwaecd.paramagic.spell.caster.SpellCaster;
import com.qwaecd.paramagic.spell.Spell;
import com.qwaecd.paramagic.spell.session.SessionState;
import com.qwaecd.paramagic.spell.session.SpellSession;
import com.qwaecd.paramagic.spell.state.SpellStateMachine;
import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.UUID;

public class ServerSession extends SpellSession {
    @Nonnull
    @Getter
    private final SpellCaster caster;
    @Nonnull
    private final SpellStateMachine machine;

    public ServerSession(UUID sessionId, @Nonnull SpellCaster caster, @Nonnull Spell spell) {
        super(sessionId, spell);
        this.caster = caster;
        this.machine = new SpellStateMachine(spell.getSpellConfig());
    }

    public boolean machineCompleted() {
        return this.machine.isCompleted();
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

    @Override
    public boolean canRemoveFromManager() {
        return super.canRemoveFromManager();
    }
}
