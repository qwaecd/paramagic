package com.qwaecd.paramagic.spell.session.server;

import com.qwaecd.paramagic.entity.SpellAnchorEntity;
import com.qwaecd.paramagic.spell.Spell;
import com.qwaecd.paramagic.spell.caster.SpellCaster;
import com.qwaecd.paramagic.spell.listener.ISpellPhaseListener;
import com.qwaecd.paramagic.spell.session.SessionState;
import com.qwaecd.paramagic.spell.session.SpellSession;
import com.qwaecd.paramagic.spell.state.SpellStateMachine;
import com.qwaecd.paramagic.spell.state.event.MachineEvent;
import com.qwaecd.paramagic.tools.ConditionalLogger;
import lombok.Getter;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nonnull;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ServerSession extends SpellSession implements AutoCloseable {
    private static final ConditionalLogger LOGGER = ConditionalLogger.create(ServerSession.class);

    @Nonnull
    @Getter
    private final SpellCaster<?> caster;
    @Nonnull
    private final SpellStateMachine machine;

    private final List<WeakReference<SpellAnchorEntity>> anchors = new ArrayList<>();

    public ServerSession(UUID sessionId, @Nonnull SpellCaster<?> caster, @Nonnull Spell spell) {
        super(sessionId, spell);
        this.caster = caster;
        this.machine = new SpellStateMachine(spell.getSpellConfig());
    }

    public boolean machineCompleted() {
        return this.machine.isCompleted();
    }

    public void tickOnLevel(ServerLevel level, float deltaTime) {
        this.tick(level, deltaTime);
    }

    @SuppressWarnings("unused")
    private void tick(ServerLevel level, float deltaTime) {
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
    }
}
