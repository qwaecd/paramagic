package com.qwaecd.paramagic.spell.session.client;

import com.qwaecd.paramagic.spell.Spell;
import com.qwaecd.paramagic.spell.session.ISessionManager;
import com.qwaecd.paramagic.spell.session.SpellSession;
import com.qwaecd.paramagic.tools.ConditionalLogger;

import javax.annotation.Nonnull;
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

    public ClientSession createSession(@Nonnull Spell spell) {
        ClientSession clientSession = new ClientSession(UUID.randomUUID(), spell);
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
