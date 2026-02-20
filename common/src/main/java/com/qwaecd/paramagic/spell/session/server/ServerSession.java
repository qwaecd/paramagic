package com.qwaecd.paramagic.spell.session.server;

import com.qwaecd.paramagic.network.Networking;
import com.qwaecd.paramagic.network.packet.session.S2CSessionDataSyncPacket;
import com.qwaecd.paramagic.spell.caster.SpellCaster;
import com.qwaecd.paramagic.spell.session.SpellSession;
import com.qwaecd.paramagic.spell.session.store.SessionDataStore;
import com.qwaecd.paramagic.spell.session.store.SessionDataSyncPayload;
import com.qwaecd.paramagic.spell.session.store.SessionDataValue;
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

public abstract class ServerSession extends SpellSession implements AutoCloseable, ServerSessionView {
    private static final ConditionalLogger LOGGER = ConditionalLogger.create(ServerSession.class);

    protected final ServerLevel level;
    protected final int trackingDistance;

    /**
     * 用于告诉 ServerSessionManager 不要重复调用{@code casterDisconnected()}的字段.
     */
    boolean disconnected = false;

    @Nonnull
    @Getter
    protected final SpellCaster caster;

    protected final List<WeakReference<SpellAnchorEntity>> anchors = new ArrayList<>();

    protected final Set<UUID> trackingPlayers = new HashSet<>();

    public ServerSession(UUID sessionId, @Nonnull SpellCaster caster, ServerLevel level) {
        super(sessionId);
        this.caster = caster;
        this.level = level;
        this.trackingDistance = this.level.getServer().getPlayerList().getSimulationDistance() * 16;
    }

    public abstract void tickOnLevel(ServerLevel level, float deltaTime);

    public void casterDisconnected() {
        this.interrupt();
    }

    @Override
    public boolean canRemoveFromManager() {
        return super.canRemoveFromManager();
    }

    public final void connectAnchor(@Nonnull SpellAnchorEntity anchor) {
        List<ServerPlayer> players = this.level.getPlayers(player -> (player.distanceToSqr(anchor) < this.trackingDistance * this.trackingDistance));
        players.forEach(player -> this.trackingPlayers.add(player.getUUID()));
        this.anchors.add(new WeakReference<>(anchor));
    }

    public void syncDataStore() {
        S2CSessionDataSyncPacket syncPacket = this.createFullSyncPacket();
        this.forEachTrackingPlayer(player -> Networking.get().sendToPlayer(player, syncPacket));
    }

    protected void forEachTrackingPlayer(Consumer<ServerPlayer> action) {
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

    protected S2CSessionDataSyncPacket createFullSyncPacket() {
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
    }

    @Override
    public ServerLevel getLevel() {
        return this.level;
    }
}
