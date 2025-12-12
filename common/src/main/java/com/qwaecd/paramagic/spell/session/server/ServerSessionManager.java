package com.qwaecd.paramagic.spell.session.server;

import com.qwaecd.paramagic.mixinapi.IServerLevel;
import com.qwaecd.paramagic.spell.Spell;
import com.qwaecd.paramagic.spell.caster.SpellCaster;
import com.qwaecd.paramagic.spell.session.ISessionManager;
import com.qwaecd.paramagic.spell.session.SpellSession;
import com.qwaecd.paramagic.tools.ConditionalLogger;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class ServerSessionManager implements ISessionManager {
    private static final ConditionalLogger LOGGER = ConditionalLogger.create(ServerSessionManager.class);
    private final WeakReference<ServerLevel> levelRef;
    private final ResourceKey<Level> levelKey;
    // sessionId -> ServerSession
    private final Map<UUID, ServerSession> sessions = new ConcurrentHashMap<>();
    // casterId -> Set<ServerSession>
    private final Map<UUID, Set<ServerSession>> casterSessions = new ConcurrentHashMap<>();

    private final Queue<ServerSession> pendingRemovals = new ConcurrentLinkedQueue<>();

    public ServerSessionManager(ServerLevel level) {
        this.levelRef = new WeakReference<>(level);
        this.levelKey = level.dimension();

        IServerLevel callbackRegister = (IServerLevel) level;
        callbackRegister.registerOnLevelTick(serverLevel -> this.tickAll(serverLevel, 1.0f / 20.0f));
    }

    @Nullable
    @SuppressWarnings({"UnusedReturnValue", "unused"})
    public ServerSession tryCreateSession(ServerLevel level, SpellCaster<?> caster, Spell spell) {
        if (!caster.canStartSession(spell, this)) {
            return null;
        }

        ServerSession serverSession = new ServerSession(UUID.randomUUID(), caster, spell);
        this.addSession(serverSession);
        return serverSession;
    }

    @Nonnull
    public Set<ServerSession> getSessionsByCaster(SpellCaster<?> caster) {
        return this.getSessionsByUUID(caster.getCasterId());
    }

    @Nonnull
    public Set<ServerSession> getSessionsByUUID(UUID casterId) {
        Set<ServerSession> sessionSet = this.casterSessions.get(casterId);
        if (sessionSet == null) {
            return Set.of();
        }
        return Collections.unmodifiableSet(sessionSet);
    }

    @SuppressWarnings("SameParameterValue")
    private void tickAll(final ServerLevel serverLevel, final float deltaTime) {
        this.flushPendingRemovals();

        this.forEachSessionSafe(session -> {
            session.tickOnLevel(serverLevel, deltaTime);
            if (session.canRemoveFromManager()) {
                this.pendingRemovals.add(session);
            }
        });
    }

    private void forEachSessionSafe(Consumer<ServerSession> consumer) {
        for (var entry : this.sessions.entrySet()) {
            try {
                consumer.accept(entry.getValue());
            } catch (Exception e) {
                LOGGER.logIfDev(logger ->
                        logger.error("Error while processing spell session {} in level {}", entry.getKey(), this.levelKey, e)
                );
            }
        }
    }

    private void removeSession(ServerSession session) {
        this.sessions.remove(session.getSessionId());

        SpellCaster<?> caster = session.getCaster();
        Set<ServerSession> casterSet = this.casterSessions.get(caster.getCasterId());
        if (casterSet != null) {
            casterSet.remove(session);
            session.close();
            if (casterSet.isEmpty()) {
                this.casterSessions.remove(caster.getCasterId());
            }
        }
    }

    private void addSession(ServerSession session) {
        this.sessions.put(session.getSessionId(), session);

        SpellCaster<?> caster = session.getCaster();
        this.casterSessions.computeIfAbsent(caster.getCasterId(), uuid -> ConcurrentHashMap.newKeySet()).add(session);
    }

    private void flushPendingRemovals() {
        ServerSession session;
        while ((session = this.pendingRemovals.poll()) != null) {
            this.removeSession(session);
        }
    }

    @Override
    public SpellSession getSession(UUID sessionId) {
        return this.sessions.get(sessionId);
    }
}
