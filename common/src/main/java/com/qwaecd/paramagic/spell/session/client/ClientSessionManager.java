package com.qwaecd.paramagic.spell.session.client;

import com.qwaecd.paramagic.spell.Spell;
import com.qwaecd.paramagic.spell.session.ISessionManager;
import com.qwaecd.paramagic.spell.session.SpellSession;
import com.qwaecd.paramagic.spell.session.SpellSessionRef;
import com.qwaecd.paramagic.spell.view.CasterTransformSource;
import com.qwaecd.paramagic.tools.CasterUtils;
import com.qwaecd.paramagic.tools.ConditionalLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ClientSessionManager implements ISessionManager {
    private static final ConditionalLogger LOGGER = ConditionalLogger.create(ClientSessionManager.class);

    private static ClientSessionManager INSTANCE;

    private final Map<UUID, ClientSession> sessions = new ConcurrentHashMap<>();

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

    public void tickAll() {
        this.forEachSessionSafe(session -> session.tick(1.0f / 20.0f));
    }

    private void forEachSessionSafe(Consumer<ClientSession> consumer) {
        for (var entry : this.sessions.entrySet()) {
            try {
                consumer.accept(entry.getValue());
            } catch (Exception e) {
                LOGGER.logIfDev(l ->
                        l.error("Error while ticking session {}: {}", entry.getKey(), e)
                );
            }
        }
    }

    @Nullable
    public ClientSession createSession(Level level, SpellSessionRef sessionRef, @Nonnull Spell spell) {
        CasterTransformSource casterSource = CasterUtils.tryFindCaster(level, sessionRef);
        if (casterSource == null) {
            LOGGER.logIfDev(l ->
                    l.warn("Failed to find caster for session {}: casterEntityUuid={}, casterNetworkId={}",
                            sessionRef.serverSessionId,
                            sessionRef.casterEntityUuid,
                            sessionRef.casterNetworkId
                    )
            );
            casterSource = CasterUtils.EMPTY_SOURCE;
        }
        ClientSession clientSession = new ClientSession(sessionRef.serverSessionId, spell, casterSource);
        this.addSession(clientSession);
        return clientSession;
    }

    private void addSession(ClientSession session) {
        this.sessions.put(session.getSessionId(), session);
    }

    private void removeSession(ClientSession session) {
        this.removeSession(session.getSessionId());
    }

    private void removeSession(UUID sessionId) {
        this.sessions.remove(sessionId);
    }

    @Override
    public SpellSession getSession(UUID sessionId) {
        return this.sessions.get(sessionId);
    }
}
