package com.qwaecd.paramagic.spell.session.client;

import com.qwaecd.paramagic.spell.session.ISessionManager;
import com.qwaecd.paramagic.spell.session.SpellSession;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ClientSessionManager implements ISessionManager {
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

    @Override
    public SpellSession getSession(UUID sessionId) {
        return INSTANCE.getSession(sessionId);
    }
}
