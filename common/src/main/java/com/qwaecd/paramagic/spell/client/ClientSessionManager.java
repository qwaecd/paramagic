package com.qwaecd.paramagic.spell.client;

import com.qwaecd.paramagic.spell.core.SpellSessionRef;
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

public class ClientSessionManager {
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
    public void tickAll(final ClientLevel clientLevel) {
        this.flushPendingRemovals();
        for (var entry : this.sessions.entrySet()) {
            ClientSession session = entry.getValue();
            try {
                session.tick();
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
    public ClientSession tryCreatePresentationSession(
            Level level,
            SpellSessionRef sessionRef,
            @Nonnull SpellPresentation presentation,
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
        ClientSession session = new ClientSession(sessionRef.serverSessionId, presentation, hybridCasterSource);
        this.addSession(session);
        session.start();
        return session;
    }

    public ClientSession createPresentationSession(
            UUID sessionId,
            @Nonnull SpellPresentation presentation,
            @Nonnull HybridCasterSource hybridCasterSource
    ) {
        ClientSession session = new ClientSession(sessionId, presentation, hybridCasterSource);
        this.addSession(session);
        session.start();
        return session;
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

    @Nullable
    public ClientSession getSession(UUID sessionId) {
        return this.sessions.get(sessionId);
    }

    private void flushPendingRemovals() {
        ClientSession session;
        while ((session = this.pendingRemovals.poll()) != null) {
            this.removeSession(session);
        }
    }
}
