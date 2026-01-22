package com.qwaecd.paramagic.spell.session.client;

import com.qwaecd.paramagic.spell.SpellIdentifier;
import com.qwaecd.paramagic.spell.builtin.BuiltinSpellVisual;
import com.qwaecd.paramagic.spell.builtin.BuiltinSpellVisualRegistry;
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
        this.machine.update(deltaTime);
        // 当前 tick ，状态机已经完成运行，则标记为逻辑完成
        if (this.machineCompleted() && !isState(SessionState.FINISHED_LOGICALLY)) {
            this.setSessionState(SessionState.FINISHED_LOGICALLY);
            return;
        }

        if (isState(SessionState.INTERRUPTED) || isState(SessionState.FINISHED_LOGICALLY)) {
            // TODO: 可以实现延迟销毁
            this.setSessionState(SessionState.DISPOSED);
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
    public int casterNetId() throws NullPointerException {
        return this.casterSource.getCasterNetId();
    }

    @Override
    public void close() {
        SpellIdentifier spellId = this.spell.definition.spellId;
        BuiltinSpellVisual visual = BuiltinSpellVisualRegistry.getSpell(spellId);
        if (visual != null) {
            visual.onClose(this);
        }

        for (SpellPhaseListener listener : this.listeners) {
            if (listener instanceof ClientSessionListener clientListener) {
                clientListener.onSessionClose();
            }
        }
    }
}
