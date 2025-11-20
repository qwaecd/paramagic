package com.qwaecd.paramagic.spell.session.manager;

import com.qwaecd.paramagic.mixinapi.IServerLevel;
import com.qwaecd.paramagic.spell.session.ServerSpellSession;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ServerSessionManager implements ISessionManager {
    private final WeakReference<ServerLevel> levelRef;
    private final ResourceKey<Level> levelKey;
    private final Map<UUID, ServerSpellSession> sessions = new ConcurrentHashMap<>();

    ServerSessionManager(ServerLevel level) {
        this.levelRef = new WeakReference<>(level);
        this.levelKey = level.dimension();

        IServerLevel callbackRegister = (IServerLevel) level;
        callbackRegister.registerOnLevelTick$paramagic(this::tickAll);
    }

    private void tickAll() {
        this.sessions.forEach((uuid, session) -> session.tick(1.0f / 20.0f));
    }
}
