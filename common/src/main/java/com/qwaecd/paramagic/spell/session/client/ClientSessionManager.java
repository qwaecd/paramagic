package com.qwaecd.paramagic.spell.session.client;

import com.qwaecd.paramagic.spell.BuiltinSpellId;
import com.qwaecd.paramagic.spell.builtin.BuiltinSpellEntry;
import com.qwaecd.paramagic.spell.builtin.BuiltinSpellRegistry;
import com.qwaecd.paramagic.spell.builtin.client.BuiltinSpellVisualRegistry;
import com.qwaecd.paramagic.spell.builtin.client.VisualEntry;
import com.qwaecd.paramagic.spell.config.CircleAssets;
import com.qwaecd.paramagic.spell.session.ISessionManager;
import com.qwaecd.paramagic.spell.session.SpellSession;
import com.qwaecd.paramagic.spell.session.SpellSessionRef;
import com.qwaecd.paramagic.spell.util.CasterUtils;
import com.qwaecd.paramagic.spell.view.HybridCasterSource;
import com.qwaecd.paramagic.tools.ConditionalLogger;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientSessionManager implements ISessionManager {
    private static final ConditionalLogger LOGGER = ConditionalLogger.create(ClientSessionManager.class);

    private static ClientSessionManager INSTANCE;

    private final Map<UUID, ClientSession> sessions = new ConcurrentHashMap<>();
    private final Queue<ClientSession> pendingRemovals = new ConcurrentLinkedQueue<>();

    public static ClientSessionManager instance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("ClientSessionManager is not initialized");
        }
        return INSTANCE;
    }

    public static void init() {
        if (INSTANCE == null) {
            INSTANCE = new ClientSessionManager();
        }
    }

    @SuppressWarnings("unused")
    public void tickAll(final ClientLevel clientLevel, final float deltaTime) {
        this.flushPendingRemovals();
        for (var entry : this.sessions.entrySet()) {
            ClientSession session = entry.getValue();
            try {
                session.tick(deltaTime);
                if (session.canRemoveFromManager()) {
                    this.pendingRemovals.add(session);
                }
            } catch (Exception e) {
                LOGGER.logIfDev(l ->
                        l.error("Error while ticking session {}: {}", entry.getKey(), e)
                );
            }
        }
    }

    @Nullable
    public ArcSessionClient tryCreateArcSession(
            Level level,
            SpellSessionRef sessionRef,
            CircleAssets assets,
            Entity fallbackSource
    ) {
        Entity casterSource = CasterUtils.tryFindCaster(level, sessionRef);
        if (casterSource == null) {
            LOGGER.logIfDev(l ->
                    l.warn("Failed to find caster for session {}: casterEntityUuid={}, casterNetworkId={}",
                            sessionRef.serverSessionId,
                            sessionRef.casterEntityUuid,
                            sessionRef.casterNetworkId
                    )
            );
        }
        HybridCasterSource hybridCasterSource = HybridCasterSource.create(casterSource, fallbackSource);

        ArcSessionClient session = new ArcSessionClient(sessionRef.serverSessionId, hybridCasterSource, assets);
        this.addSession(session);
        return session;
    }

    @Nullable
    public MachineSessionClient tryCreateMachineSession(Level level, SpellSessionRef sessionRef, @Nonnull BuiltinSpellId spellId, Entity fallbackSource) {
        Entity casterSource = CasterUtils.tryFindCaster(level, sessionRef);
        if (casterSource == null) {
            LOGGER.logIfDev(l ->
                    l.warn("Failed to find caster for session {}: casterEntityUuid={}, casterNetworkId={}",
                            sessionRef.serverSessionId,
                            sessionRef.casterEntityUuid,
                            sessionRef.casterNetworkId
                    )
            );
        }

        BuiltinSpellEntry bEntry = BuiltinSpellRegistry.getSpell(spellId);
        VisualEntry vEntry = BuiltinSpellVisualRegistry.getSpell(spellId);
        if (bEntry == null || vEntry == null) {
            LOGGER.get().error("Cannot find built-in spell visual entry with id: {}", spellId);
            return null;
        }

        HybridCasterSource hybridCasterSource = HybridCasterSource.create(casterSource, fallbackSource);
        MachineSessionClient clientSession = new MachineSessionClient(
                sessionRef.serverSessionId,
                spellId,
                hybridCasterSource,
                bEntry.getSpell().createMachine(),
                vEntry.createRenderer()
        );
        this.addSession(clientSession);
        return clientSession;
    }

    private void addSession(ClientSession session) {
        this.sessions.put(session.getSessionId(), session);
    }

    private void removeSession(UUID sessionId) {
        ClientSession clientSession = this.sessions.get(sessionId);
        if (clientSession == null) {
            return;
        }
        this.removeSession(clientSession);
    }

    private void removeSession(ClientSession session) {
        session.close();
        this.sessions.remove(session.getSessionId());
    }

    @Override
    public SpellSession getSession(UUID sessionId) {
        return this.sessions.get(sessionId);
    }

    private void flushPendingRemovals() {
        ClientSession session;
        while ((session = this.pendingRemovals.poll()) != null) {
            this.removeSession(session);
        }
    }
}
