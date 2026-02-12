package com.qwaecd.paramagic.spell.session.server;

import com.qwaecd.paramagic.spell.caster.SpellCaster;
import com.qwaecd.paramagic.spell.SpellPhaseListener;
import com.qwaecd.paramagic.spell.phase.SpellPhaseType;
import com.qwaecd.paramagic.spell.session.SessionState;
import com.qwaecd.paramagic.spell.state.SpellStateMachine;
import com.qwaecd.paramagic.spell.state.event.MachineEvent;
import com.qwaecd.paramagic.tools.ConditionalLogger;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class MachineSessionServer extends ServerSession implements ServerSessionView {
    private static final ConditionalLogger LOGGER = ConditionalLogger.create(MachineSessionServer.class);
    @Nonnull
    private final SpellStateMachine machine;

    @Nonnull
    private final SpellExecutor executor;

    private final List<SpellPhaseListener> listeners = new ArrayList<>();

    public MachineSessionServer(
            UUID sessionId,
            @Nonnull SpellCaster caster,
            @Nonnull SpellStateMachine machine,
            @Nonnull SpellExecutor executor,
            ServerLevel level
    ) {
        super(sessionId, caster, level);
        this.machine = machine;
        this.executor = executor;

        this.machine.setSessionCallback(this::onPhaseChanged);
    }

    public void tickOnLevel(ServerLevel level, float deltaTime) {
        this.machine.update(deltaTime);

        this.executor.tick(this, this.machine.currentPhase(), level);

        // 当前 tick ，状态机已经完成运行，则标记为逻辑完成
        if (this.machineCompleted() && !isState(SessionState.FINISHED_LOGICALLY)) {
            this.setSessionState(SessionState.FINISHED_LOGICALLY);
            return;
        }

        if (isState(SessionState.INTERRUPTED) || isState(SessionState.FINISHED_LOGICALLY)) {
            // TODO: 可以实现延迟销毁
            if (!this.executor.canFinish()) {
                return;
            }
            this.setSessionState(SessionState.DISPOSED);
        }
    }

    public boolean machineCompleted() {
        return this.machine.isCompleted();
    }

    public void registerListener(SpellPhaseListener listener) {
        if (listener instanceof ServerSessionListener sessionListener) {
            sessionListener.bind(this);
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
        this.executor.onInterrupt();
    }

    public void forceInterrupt() {
        this.setSessionState(SessionState.INTERRUPTED);
        this.machine.forceInterrupt();
        this.executor.onInterrupt();
    }

    @Override
    public void close() {
        super.close();
        this.executor.onSessionClose();
        this.forEachListenerSafe(listener -> {
            if (listener instanceof ServerSessionListener sessionListener) {
                sessionListener.onSessionClose();
            }
        });
    }

    private void forEachListenerSafe(Consumer<SpellPhaseListener> action) {
        for (SpellPhaseListener listener : this.listeners) {
            try {
                action.accept(listener);
            } catch (Exception e) {
                LOGGER.get().warn("Exception occurred while notifying listener: {}", listener.getClass().getName(), e);
            }
        }
    }

    private void onPhaseChanged(SpellPhaseType oldPhase, SpellPhaseType currentPhase) {
        this.executor.onPhaseChanged(this, oldPhase, currentPhase);
    }
}
