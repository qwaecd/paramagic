package com.qwaecd.paramagic.spell.session.client;

import com.qwaecd.paramagic.spell.core.Spell;
import com.qwaecd.paramagic.spell.listener.SpellPhaseListener;
import com.qwaecd.paramagic.spell.session.SessionState;
import com.qwaecd.paramagic.spell.session.SpellSession;
import com.qwaecd.paramagic.spell.state.SpellStateMachine;
import com.qwaecd.paramagic.spell.state.event.MachineEvent;
import com.qwaecd.paramagic.spell.view.CasterTransformSource;
import com.qwaecd.paramagic.spell.view.HybridCasterSource;
import lombok.Getter;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nonnull;
import java.util.UUID;

public class ClientSession extends SpellSession implements ClientSessionView, AutoCloseable {
    private final SpellStateMachine machine;
    @Nonnull
    @Getter
    private final HybridCasterSource casterSource;

    public ClientSession(UUID sessionId, Spell spell, @Nonnull HybridCasterSource casterSource) {
        super(sessionId, spell);
        this.machine = new SpellStateMachine(spell.definition);
        this.casterSource = casterSource;
    }

    public void tick(float deltaTime) {
        if (isState(SessionState.INTERRUPTED) || isState(SessionState.FINISHED_LOGICALLY)) {
            // TODO: 可以实现延迟销毁
            this.setSessionState(SessionState.DISPOSED);
            return;
        }
        if (!this.machineCompleted()) {
            this.machine.update(deltaTime);
        } else {
            this.setSessionState(SessionState.FINISHED_LOGICALLY);
        }
    }

    public void upsertCasterSource(@Nonnull Entity source) {
        this.casterSource.setPrimary(source);
    }

    @Override
    public void registerListener(SpellPhaseListener listener) {
        super.registerListener(listener);
        if (listener instanceof ClientSessionListener clientListener) {
            clientListener.bind(this);
        }
        this.machine.addListener(listener);
    }

    @Override
    public void postEvent(MachineEvent event) {
        this.machine.postEvent(event);
    }

    @Override
    public void unregisterListener(SpellPhaseListener listener) {
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

    @Override
    public CasterTransformSource casterSource() {
        return this.casterSource;
    }

    @Override
    public void close() {
        for (SpellPhaseListener listener : this.listeners) {
            if (listener instanceof ClientSessionListener clientListener) {
                clientListener.onSessionClose();
            }
        }
    }
}
