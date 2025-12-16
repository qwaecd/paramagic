package com.qwaecd.paramagic.spell;

import com.qwaecd.paramagic.entity.SpellAnchorEntity;
import com.qwaecd.paramagic.spell.caster.SpellCaster;
import com.qwaecd.paramagic.spell.session.SessionManagers;
import com.qwaecd.paramagic.spell.session.SpellSessionRef;
import com.qwaecd.paramagic.spell.session.client.ClientSession;
import com.qwaecd.paramagic.spell.session.client.ClientSessionManager;
import com.qwaecd.paramagic.spell.session.server.ServerSession;
import com.qwaecd.paramagic.spell.session.server.ServerSessionManager;
import com.qwaecd.paramagic.spell.state.event.AllMachineEvents;
import com.qwaecd.paramagic.spell.view.CasterTransformSource;
import com.qwaecd.paramagic.tools.CasterUtils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("UnusedReturnValue")
public final class SpellSpawner {
    @Nullable
    public static ServerSession spawnOnServer(ServerLevel level, SpellCaster<?> caster, Spell spell) {
        ServerSessionManager manager = SessionManagers.getForServer(level);
        ServerSession serverSession = manager.tryCreateSession(level, caster, spell);

        if (serverSession != null) {
            serverSession.postEvent(AllMachineEvents.START_CASTING);

            SpellAnchorEntity spellAnchorEntity = new SpellAnchorEntity(level);
            connect(serverSession, spellAnchorEntity);

            spellAnchorEntity.moveTo(caster.position());
            level.addFreshEntity(spellAnchorEntity);
        }

        return serverSession;
    }

    private static void connect(ServerSession session, SpellAnchorEntity entity) {
        session.connectAnchor(entity);
        entity.attachSession(session);
    }

    @Nullable
    public static ClientSession spawnOnClient(
            Level level,
            SpellSessionRef sessionRef,
            Spell spell,
            Entity fallbackSource
    ) {
        ClientSessionManager manager = SessionManagers.getForClient();
        ClientLevel clientLevel = (ClientLevel) level;

        ClientSession session = (ClientSession) manager.getSession(sessionRef.serverSessionId);
        if (session == null) {
            session = manager.createSession(clientLevel, sessionRef, spell, fallbackSource);
            if (session != null) {
                session.postEvent(AllMachineEvents.START_CASTING);
            }
        } else {
            tryUpsertCasterSource(clientLevel, session, sessionRef);
        }

        return session;
    }

    private static void tryUpsertCasterSource(@Nonnull Level level, ClientSession session, SpellSessionRef sessionRef) {
        Entity casterSource = CasterUtils.tryFindCaster(level, sessionRef);
        if (casterSource != null) {
            session.upsertCasterSource(casterSource);
        }
    }
}
