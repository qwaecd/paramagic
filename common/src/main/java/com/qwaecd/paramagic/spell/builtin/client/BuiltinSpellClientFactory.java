package com.qwaecd.paramagic.spell.builtin.client;

import com.qwaecd.paramagic.network.Networking;
import com.qwaecd.paramagic.network.packet.session.C2SSessionAttachPacket;
import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.spell.BuiltinSpellId;
import com.qwaecd.paramagic.spell.api.AllPresentations;
import com.qwaecd.paramagic.spell.client.ClientSession;
import com.qwaecd.paramagic.spell.client.ClientSessionManager;
import com.qwaecd.paramagic.spell.client.SpellPresentation;
import com.qwaecd.paramagic.spell.core.SessionManagers;
import com.qwaecd.paramagic.spell.core.SpellSessionRef;
import com.qwaecd.paramagic.spell.util.CasterUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

@PlatformScope(PlatformScopeType.CLIENT)
public final class BuiltinSpellClientFactory {
    private BuiltinSpellClientFactory() {
    }

    @Nullable
    public static ClientSession spawnOnClient(Level level, SpellSessionRef sessionRef, BuiltinSpellId spellId, Entity fallbackSource) {
        SpellPresentation presentation = AllPresentations.create(spellId);
        if (presentation == null) {
            return null;
        }

        ClientSessionManager manager = SessionManagers.getForClient();
        ClientSession existingSession = manager.getSession(sessionRef.serverSessionId);
        if (existingSession != null) {
            Entity casterSource = CasterUtils.tryFindCaster(level, sessionRef);
            if (casterSource != null) {
                existingSession.upsertCasterSource(casterSource);
            }
            return existingSession;
        }

        ClientSession session = manager.tryCreatePresentationSession(level, sessionRef, presentation, fallbackSource);
        if (session != null) {
            Networking.get().sendToServer(new C2SSessionAttachPacket(sessionRef.serverSessionId));
        }
        return session;
    }
}
