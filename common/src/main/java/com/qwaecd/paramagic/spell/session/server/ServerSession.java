package com.qwaecd.paramagic.spell.session.server;

import com.qwaecd.paramagic.data.para.struct.ParaData;
import com.qwaecd.paramagic.network.Networking;
import com.qwaecd.paramagic.network.packet.session.S2CSessionDataSyncPacket;
import com.qwaecd.paramagic.spell.caster.SpellCaster;
import com.qwaecd.paramagic.spell.config.phase.PhaseConfig;
import com.qwaecd.paramagic.spell.core.Spell;
import com.qwaecd.paramagic.spell.listener.SpellPhaseListener;
import com.qwaecd.paramagic.spell.phase.SpellPhaseType;
import com.qwaecd.paramagic.spell.session.SessionState;
import com.qwaecd.paramagic.spell.session.SpellSession;
import com.qwaecd.paramagic.spell.session.store.SessionDataStore;
import com.qwaecd.paramagic.spell.session.store.SessionDataSyncPayload;
import com.qwaecd.paramagic.spell.session.store.SessionDataValue;
import com.qwaecd.paramagic.spell.state.SpellStateMachine;
import com.qwaecd.paramagic.spell.state.event.MachineEvent;
import com.qwaecd.paramagic.thaumaturgy.ArcaneProcessor;
import com.qwaecd.paramagic.thaumaturgy.ParaContext;
import com.qwaecd.paramagic.thaumaturgy.ParaTree;
import com.qwaecd.paramagic.tools.ConditionalLogger;
import com.qwaecd.paramagic.world.entity.SpellAnchorEntity;
import lombok.Getter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

import javax.annotation.Nonnull;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Consumer;

public class ServerSession extends SpellSession implements AutoCloseable, ServerSessionView {
    private static final ConditionalLogger LOGGER = ConditionalLogger.create(ServerSession.class);

    private final ServerLevel level;
    private final int trackingDistance;

    @Nonnull
    @Getter
    private final SpellCaster caster;
    @Nonnull
    private final SpellStateMachine machine;

    private final ArcaneProcessor processor;

    private final List<WeakReference<SpellAnchorEntity>> anchors = new ArrayList<>();

    private final Set<UUID> trackingPlayers = new HashSet<>();

    public ServerSession(UUID sessionId, @Nonnull SpellCaster caster, @Nonnull Spell spell, ServerLevel level) {
        super(sessionId, spell);
        this.caster = caster;
        this.machine = new SpellStateMachine(spell.definition);
        this.level = level;
        this.trackingDistance = this.level.getServer().getPlayerList().getSimulationDistance() * 16;

        SpellPhaseType executePhase = spell.definition.meta.executePhase;
        PhaseConfig phaseConfig = spell.definition.phases.getPhaseConfig(executePhase);
        ParaData paraData = phaseConfig.getAssetConfig().getSpellAssets().getParaData();
        this.processor = new ArcaneProcessor(new ParaTree(paraData), new ParaContext(this, level, caster));
    }

    public boolean machineCompleted() {
        return this.machine.isCompleted();
    }

    public void tickOnLevel(ServerLevel level, float deltaTime) {
        this.tick(level, deltaTime);
        this.processor.tick();
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
        List<ServerPlayer> players = this.level.getPlayers(player -> (player.distanceToSqr(anchor) < this.trackingDistance * this.trackingDistance));
        players.forEach(player -> this.trackingPlayers.add(player.getUUID()));
        this.anchors.add(new WeakReference<>(anchor));
    }

    public void syncDataStore() {
        this.forEachTrackingPlayer(player -> Networking.get().sendToPlayer(player, this.createFullSyncPacket()));
    }

    private void forEachTrackingPlayer(Consumer<ServerPlayer> action) {
        PlayerList playerList = this.level.getServer().getPlayerList();
        this.trackingPlayers.forEach(uuid -> {
            ServerPlayer player = playerList.getPlayer(uuid);
            if (player != null) {
                action.accept(player);
            }
        });
    }

    public void setDataValueSync(int dataId, @Nonnull SessionDataValue<?> value) {
        this.dataStore.setValue(dataId, value);
        SessionDataStore.DataValue<?> dataValue = this.dataStore.getDataValue(dataId);
        if (dataValue == null) {
            return;
        }
        SessionDataSyncPayload payload = new SessionDataSyncPayload(
                this.sessionId,
                dataId,
                dataValue.getSequenceNumber(),
                dataValue.get()
        );
        final S2CSessionDataSyncPacket packet = new S2CSessionDataSyncPacket(payload);
        this.forEachTrackingPlayer(player -> Networking.get().sendToPlayer(player, packet));
    }

    private S2CSessionDataSyncPacket createFullSyncPacket() {
        List<SessionDataSyncPayload.Entry> entries = new ArrayList<>();
        this.dataStore.forEachEntry(
                (dataId, dataValue) -> entries.add(
                        new SessionDataSyncPayload.Entry(dataId, dataValue.getSequenceNumber(), dataValue.get())
                )
        );
        return new S2CSessionDataSyncPacket(new SessionDataSyncPayload(this.sessionId, entries));
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
