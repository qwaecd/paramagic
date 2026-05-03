package com.qwaecd.paramagic.spell.server;

import com.qwaecd.paramagic.network.Networking;
import com.qwaecd.paramagic.network.packet.session.S2CSessionDataSyncPacket;
import com.qwaecd.paramagic.network.packet.session.S2CSessionStopPacket;
import com.qwaecd.paramagic.spell.caster.SpellCaster;
import com.qwaecd.paramagic.spell.core.EndSpellReason;
import com.qwaecd.paramagic.spell.core.SessionState;
import com.qwaecd.paramagic.spell.core.SpellSession;
import com.qwaecd.paramagic.spell.core.store.SessionDataStore;
import com.qwaecd.paramagic.spell.core.store.SessionDataSyncPayload;
import com.qwaecd.paramagic.spell.core.store.SessionDataValue;
import com.qwaecd.paramagic.tools.ConditionalLogger;
import com.qwaecd.paramagic.world.entity.SpellAnchorEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

import javax.annotation.Nonnull;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Consumer;

public class ServerSession extends SpellSession implements AutoCloseable {
    private static final ConditionalLogger LOGGER = ConditionalLogger.create(ServerSession.class);

    @Nonnull
    private final SpellRuntime spell;
    protected final ServerLevel level;
    protected final int trackingDistance;

    /**
     * 用于告诉 ServerSessionManager 不要重复调用{@code casterDisconnected()}的字段.
     */
    boolean disconnected = false;

    @Nonnull
    protected final SpellCaster caster;

    protected final List<WeakReference<SpellAnchorEntity>> anchors = new ArrayList<>();

    protected final Set<UUID> trackingPlayers = new HashSet<>();

    private final ServerSpellContext context;
    private boolean started = false;

    public ServerSession(@Nonnull SpellRuntime runtime, @Nonnull SpellCaster caster, ServerLevel level) {
        this(UUID.randomUUID(), runtime, caster, level);
    }

    public ServerSession(@Nonnull UUID sessionId, @Nonnull SpellRuntime runtime, @Nonnull SpellCaster caster, ServerLevel level) {
        super(sessionId);
        this.spell = runtime;
        this.caster = caster;
        this.level = level;
        this.trackingDistance = this.level.getServer().getPlayerList().getSimulationDistance() * 16;
        this.context = new ServerSpellContext(this);
    }

    public void start() {
        if (this.started) {
            return;
        }
        this.started = true;
        this.spell.onStart(this.context);
    }

    public void tick() {
        if (!this.started) {
            this.start();
        }

        if (this.isState(SessionState.RUNNING)) {
            this.spell.tick(this.context);
        }

        if (this.isState(SessionState.RUNNING) && this.spell.isFinished()) {
            this.markCompleted();
        }

        if (this.isState(SessionState.STOPPING) && this.spell.isFinished()) {
            this.setSessionState(SessionState.DISPOSED);
        }
    }

    public void release() {
        if (this.spell instanceof ReleaseAwareSpellRuntime runtime) {
            runtime.release(this.context);
        } else {
            this.interrupt();
        }
    }

    @Override
    protected void onStopRequested(@Nonnull EndSpellReason reason) {
        this.spell.interrupt(this.context, reason);
        this.broadcastStop(reason);
    }

    @Override
    protected void onCompleted() {
        this.broadcastStop(EndSpellReason.COMPLETED);
    }

    @Override
    public boolean canRemoveFromManager() {
        return super.canRemoveFromManager();
    }

    public final void connectAnchor(@Nonnull SpellAnchorEntity anchor) {
        this.anchors.add(new WeakReference<>(anchor));
    }

    public void addTrackingPlayer(@Nonnull ServerPlayer player) {
        this.trackingPlayers.add(player.getUUID());
    }

    public void syncDataStore() {
        S2CSessionDataSyncPacket syncPacket = this.createFullSyncPacket();
        this.forEachTrackingPlayer(player -> Networking.get().sendToPlayer(player, syncPacket));
    }

    public void syncDataStore(@Nonnull ServerPlayer player) {
        Networking.get().sendToPlayer(player, this.createFullSyncPacket());
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

    private void broadcastStop(@Nonnull EndSpellReason reason) {
        S2CSessionStopPacket packet = new S2CSessionStopPacket(this.sessionId, reason);
        this.forEachTrackingPlayer(player -> Networking.get().sendToPlayer(player, packet));
    }

    @Override
    public void close() {
        this.spell.dispose(this.context);
        for (WeakReference<SpellAnchorEntity> anchorRef : this.anchors) {
            Optional.ofNullable(anchorRef.get()).ifPresentOrElse(SpellAnchorEntity::discard, () ->
                    LOGGER.logIfDev(l -> l.warn("SpellAnchorEntity has been garbage collected before session close."))
            );
        }
        this.anchors.clear();
    }

    public ServerLevel getLevel() {
        return this.level;
    }

    @Nonnull
    public SpellCaster getCaster() {
        return this.caster;
    }
}
