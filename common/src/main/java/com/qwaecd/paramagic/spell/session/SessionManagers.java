package com.qwaecd.paramagic.spell.session;

import com.qwaecd.paramagic.spell.session.client.ClientSessionManager;
import com.qwaecd.paramagic.spell.session.server.ServerSessionManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public final class SessionManagers {
    private static final Map<ServerLevel, ServerSessionManager> MAP = Collections.synchronizedMap(new WeakHashMap<>());

    public static ISessionManager getFor(Level level) {
        if (level.isClientSide()) {
            return ClientSessionManager.instance();
        } else {
            return MAP.computeIfAbsent((ServerLevel) level, ServerSessionManager::new);
        }
    }

    public static ServerSessionManager getForServer(ServerLevel level) {
        return MAP.computeIfAbsent(level, ServerSessionManager::new);
    }

    public static ClientSessionManager getForClient() {
        return ClientSessionManager.instance();
    }
}
