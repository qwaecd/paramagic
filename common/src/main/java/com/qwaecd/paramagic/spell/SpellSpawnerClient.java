package com.qwaecd.paramagic.spell;

import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.spell.session.SessionManagers;
import com.qwaecd.paramagic.spell.session.SpellSessionRef;
import com.qwaecd.paramagic.spell.session.client.ClientSession;
import com.qwaecd.paramagic.spell.session.client.ClientSessionManager;
import com.qwaecd.paramagic.spell.state.event.AllMachineEvents;
import com.qwaecd.paramagic.tools.CasterUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@PlatformScope(PlatformScopeType.CLIENT)
public class SpellSpawnerClient {
    @Nullable
    public static ClientSession spawnOnClient(
            Level level,
            SpellSessionRef sessionRef,
            Spell spell,
            Entity fallbackSource
    ) {
        ClientSessionManager manager = SessionManagers.getForClient();

        ClientSession session = (ClientSession) manager.getSession(sessionRef.serverSessionId);
        if (session == null) {
            session = manager.createSession(level, sessionRef, spell, fallbackSource);
            if (session != null) {
                session.postEvent(AllMachineEvents.START_CASTING);
            }
        } else {
            tryUpsertCasterSource(level, session, sessionRef);
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
