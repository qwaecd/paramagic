package com.qwaecd.paramagic.spell.session.client;

import com.qwaecd.paramagic.spell.Spell;
import com.qwaecd.paramagic.spell.listener.ISpellPhaseListener;
import com.qwaecd.paramagic.spell.session.SessionState;
import com.qwaecd.paramagic.spell.session.SpellSession;
import com.qwaecd.paramagic.spell.state.SpellStateMachine;
import com.qwaecd.paramagic.spell.state.event.MachineEvent;
import com.qwaecd.paramagic.spell.view.CasterTransformSource;
import com.qwaecd.paramagic.spell.view.HybridCasterSource;
import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.UUID;

public class ClientSession extends SpellSession {
    private final SpellStateMachine machine;
    @Nonnull
    @Getter
    private final HybridCasterSource casterSource;

    public ClientSession(UUID sessionId, Spell spell, @Nonnull HybridCasterSource casterSource) {
        super(sessionId, spell);
        this.machine = new SpellStateMachine(spell.getSpellConfig());
        this.casterSource = casterSource;
    }

    public void tick(float deltaTime) {
        if (!this.machineCompleted()) {
            this.machine.update(deltaTime);
        } else {
            this.setSessionState(SessionState.FINISHED_LOGICALLY);
            return;
        }
        if (isState(SessionState.INTERRUPTED) || isState(SessionState.FINISHED_LOGICALLY)) {
            // TODO: 可以实现延迟销毁
            this.setSessionState(SessionState.DISPOSED);
        }
    }

    public void upsertCasterSource(@Nonnull CasterTransformSource source) {
        this.casterSource.setPrimary(source);
    }

    @Override
    public void registerListener(ISpellPhaseListener listener) {
        super.registerListener(listener);
        this.machine.addListener(listener);
    }

    @Override
    public void postEvent(MachineEvent event) {
        this.machine.postEvent(event);
    }

    @Override
    public void unregisterListener(ISpellPhaseListener listener) {
        super.unregisterListener(listener);
        this.machine.removeListener(listener);
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
