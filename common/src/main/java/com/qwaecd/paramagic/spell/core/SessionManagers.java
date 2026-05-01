package com.qwaecd.paramagic.spell.core;

import com.qwaecd.paramagic.spell.client.ClientSessionManager;
import com.qwaecd.paramagic.spell.server.ServerSessionManager;
import net.minecraft.server.level.ServerLevel;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public final class SessionManagers {
    private static final Map<ServerLevel, ServerSessionManager> MAP = Collections.synchronizedMap(new WeakHashMap<>());

    public static ServerSessionManager getForServer(ServerLevel level) {
        return MAP.computeIfAbsent(level, ServerSessionManager::new);
    }

    public static ClientSessionManager getForClient() {
        return ClientSessionManager.instance();
    }
}
