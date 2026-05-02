package com.qwaecd.paramagic.spell.server;

import com.qwaecd.paramagic.mixinapi.IServerLevel;
import com.qwaecd.paramagic.spell.caster.SpellCaster;
import com.qwaecd.paramagic.spell.core.SpellSession;
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

public class ServerSessionManager {
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
        if (level instanceof IServerLevel callbackRegister) {
            callbackRegister.registerOnLevelTick(this::tickAll);
        } else {
            LOGGER.get().warn("The level {} does not implement IServerLevel, spell sessions will not tick!", this.levelKey);
        }
    }

    @Nullable
    public ServerSession tryCreateRuntimeSession(
            ServerLevel level,
            SpellCaster caster,
            SpellRuntime runtime
    ) {
        if (!caster.canStartSession(this)) {
            return null;
        }

        ServerSession session = new ServerSession(runtime, caster, level);
        this.addSession(session);
        return session;
    }

    @Nonnull
    public Set<ServerSession> getSessionsByCaster(SpellCaster caster) {
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

    private void tickAll(final ServerLevel serverLevel) {
        this.flushPendingRemovals();

        for (var entry : this.sessions.entrySet()) {
            ServerSession session = entry.getValue();
            try {
                if (!session.getCaster().shouldContinueSession(this) && !session.disconnected) {
                    if (session.interrupt()) {
                        session.disconnected = true;
                    }
                }

                if (session.canRemoveFromManager()) {
                    this.pendingRemovals.add(session);
                    continue;
                }

                session.tick();
            } catch (Exception e) {
                LOGGER.logIfDev(logger ->
                        logger.error("Error while processing spell session {} in level {}", entry.getKey(), this.levelKey, e)
                );
            }
        }
    }

    private void removeSession(ServerSession session) {
        this.sessions.remove(session.getSessionId());

        SpellCaster caster = session.getCaster();
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

        SpellCaster caster = session.getCaster();
        this.casterSessions.computeIfAbsent(caster.getCasterId(), uuid -> ConcurrentHashMap.newKeySet()).add(session);
    }

    private void flushPendingRemovals() {
        ServerSession session;
        while ((session = this.pendingRemovals.poll()) != null) {
            this.removeSession(session);
        }
    }

    public SpellSession getSession(UUID sessionId) {
        return this.sessions.get(sessionId);
    }
}
