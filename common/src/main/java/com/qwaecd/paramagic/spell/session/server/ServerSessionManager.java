package com.qwaecd.paramagic.spell.session.server;

import com.qwaecd.paramagic.mixinapi.IServerLevel;
import com.qwaecd.paramagic.spell.session.ISessionManager;
import com.qwaecd.paramagic.spell.session.SpellSession;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ServerSessionManager implements ISessionManager {
    private final WeakReference<ServerLevel> levelRef;
    private final ResourceKey<Level> levelKey;
    private final Map<UUID, ServerSession> sessions = new ConcurrentHashMap<>();

    public ServerSessionManager(ServerLevel level) {
        this.levelRef = new WeakReference<>(level);
        this.levelKey = level.dimension();

        IServerLevel callbackRegister = (IServerLevel) level;
        callbackRegister.registerOnLevelTick(this::tickAll);
    }

    private void tickAll() {
        this.forEachSessionSafe(session -> session.tick(1.0f / 20.0f));
    }

    private void forEachSessionSafe(Consumer<ServerSession> consumer) {
        for (var entry : this.sessions.entrySet()) {
            try {
                consumer.accept(entry.getValue());
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public SpellSession getSession(UUID sessionId) {
        return this.sessions.get(sessionId);
    }
}
