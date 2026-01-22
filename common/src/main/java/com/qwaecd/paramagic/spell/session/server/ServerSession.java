package com.qwaecd.paramagic.spell.session.server;

import com.qwaecd.paramagic.spell.caster.SpellCaster;
import com.qwaecd.paramagic.spell.core.Spell;
import com.qwaecd.paramagic.spell.listener.SpellPhaseListener;
import com.qwaecd.paramagic.spell.session.SessionState;
import com.qwaecd.paramagic.spell.session.SpellSession;
import com.qwaecd.paramagic.spell.state.SpellStateMachine;
import com.qwaecd.paramagic.spell.state.event.MachineEvent;
import com.qwaecd.paramagic.tools.ConditionalLogger;
import com.qwaecd.paramagic.world.entity.SpellAnchorEntity;
import lombok.Getter;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nonnull;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class ServerSession extends SpellSession implements AutoCloseable, ServerSessionView {
    private static final ConditionalLogger LOGGER = ConditionalLogger.create(ServerSession.class);

    private final ServerLevel level;

    @Nonnull
    @Getter
    private final SpellCaster caster;
    @Nonnull
    private final SpellStateMachine machine;

    private final List<WeakReference<SpellAnchorEntity>> anchors = new ArrayList<>();

    public ServerSession(UUID sessionId, @Nonnull SpellCaster caster, @Nonnull Spell spell, ServerLevel level) {
        super(sessionId, spell);
        this.caster = caster;
        this.machine = new SpellStateMachine(spell.definition);
        this.level = level;
    }

    public boolean machineCompleted() {
        return this.machine.isCompleted();
    }

    public void tickOnLevel(ServerLevel level, float deltaTime) {
        this.tick(level, deltaTime);
    }

    @SuppressWarnings("unused")
    private void tick(ServerLevel level, float deltaTime) {
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

    @Override
    public void registerListener(SpellPhaseListener listener) {
        if (listener instanceof ServerSessionListener sessionListener) {
            sessionListener.bind(this);
        }
        super.registerListener(listener);
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

    @Override
    public boolean canRemoveFromManager() {
        return super.canRemoveFromManager();
    }

    public void connectAnchor(@Nonnull SpellAnchorEntity anchor) {
        this.anchors.add(new WeakReference<>(anchor));
    }

    @Override
    public void close() {
        for (WeakReference<SpellAnchorEntity> anchorRef : this.anchors) {
            Optional.ofNullable(anchorRef.get()).ifPresentOrElse(SpellAnchorEntity::discard, () ->
                    LOGGER.logIfDev(l -> l.warn("SpellAnchorEntity has been garbage collected before session close."))
            );
        }
        this.anchors.clear();
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

    @Override
    public ServerLevel getLevel() {
        return this.level;
    }
}
