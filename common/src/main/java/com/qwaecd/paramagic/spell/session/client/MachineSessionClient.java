package com.qwaecd.paramagic.spell.session.client;

import com.qwaecd.paramagic.spell.BuiltinSpellId;
import com.qwaecd.paramagic.spell.api.SpellPhaseListener;
import com.qwaecd.paramagic.spell.builtin.client.BuiltinSpellVisualRegistry;
import com.qwaecd.paramagic.spell.builtin.client.SpellRenderer;
import com.qwaecd.paramagic.spell.phase.SpellPhaseType;
import com.qwaecd.paramagic.spell.session.SessionState;
import com.qwaecd.paramagic.spell.state.SpellStateMachine;
import com.qwaecd.paramagic.spell.state.event.MachineEvent;
import com.qwaecd.paramagic.spell.view.HybridCasterSource;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MachineSessionClient extends ClientSession implements ClientSessionView {
    @Nonnull
    private final SpellStateMachine machine;

    @Nonnull
    private final SpellRenderer renderer;

    private final BuiltinSpellId spellId;

    private final List<SpellPhaseListener> listeners = new ArrayList<>();

    public MachineSessionClient(
            UUID sessionId,
            BuiltinSpellId spellId,
            @Nonnull HybridCasterSource casterSource,
            @Nonnull SpellStateMachine machine,
            @Nonnull SpellRenderer renderer
            ) {
        super(sessionId, casterSource);
        this.machine = machine;
        this.renderer = renderer;
        this.spellId = spellId;

        this.machine.setSessionCallback(this::onPhaseChanged);
    }

    public boolean machineCompleted() {
        return this.machine.isCompleted();
    }

    @Override
    public void tick(float deltaTime) {
        this.machine.update(deltaTime);
        this.renderer.gameTick(this, this.machine.currentPhase());
        // 当前 tick ，状态机已经完成运行，则标记为逻辑完成
        if (this.machineCompleted()
                && !isState(SessionState.FINISHED_LOGICALLY) // 防止重复调用 setState
                && !isState(SessionState.INTERRUPTED) // 防止处于中断状态的 session 被错误地标记为完成
        ) {
            this.setSessionState(SessionState.FINISHED_LOGICALLY);
            return;
        }

        if (isState(SessionState.INTERRUPTED) || isState(SessionState.FINISHED_LOGICALLY)) {
            // TODO: 可以实现延迟销毁
            if (!this.renderer.canFinish()) {
                return;
            }
            this.setSessionState(SessionState.DISPOSED);
        }
    }

    public void registerListener(SpellPhaseListener listener) {
        if (listener instanceof ClientSessionListener clientListener) {
            clientListener.bind(this);
        }
        this.listeners.add(listener);
        this.machine.addListener(listener);
    }

    public void postEvent(MachineEvent event) {
        this.machine.postEvent(event);
    }

    public void unregisterListener(SpellPhaseListener listener) {
        this.listeners.remove(listener);
        this.machine.removeListener(listener);
    }

    public void interrupt() {
        this.setSessionState(SessionState.INTERRUPTED);
        this.machine.interrupt();
        this.renderer.onInterrupt();
    }

    public void forceInterrupt() {
        this.setSessionState(SessionState.INTERRUPTED);
        this.machine.forceInterrupt();
        this.renderer.onInterrupt();
    }

    @Override
    public void close() {
        var entry = BuiltinSpellVisualRegistry.getSpell(this.spellId);
        if (entry != null) {
            entry.getVisual().onClose(this);
        }
        this.renderer.onSessionClose();

        for (SpellPhaseListener listener : this.listeners) {
            if (listener instanceof ClientSessionListener clientListener) {
                clientListener.onSessionClose();
            }
        }
    }

    private void onPhaseChanged(SpellPhaseType oldPhase, SpellPhaseType currentPhase) {
        this.renderer.onPhaseChanged(this, oldPhase, currentPhase);
    }
}
