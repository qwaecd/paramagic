package com.qwaecd.paramagic.spell.arcane;

import com.qwaecd.paramagic.network.Networking;
import com.qwaecd.paramagic.network.packet.session.C2SSessionAttachPacket;
import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.spell.client.CircleAssets;
import com.qwaecd.paramagic.spell.client.ClientSession;
import com.qwaecd.paramagic.spell.client.ClientSessionManager;
import com.qwaecd.paramagic.spell.client.SpellPresentation;
import com.qwaecd.paramagic.spell.core.SessionManagers;
import com.qwaecd.paramagic.spell.core.SpellSessionRef;
import com.qwaecd.paramagic.spell.util.CasterUtils;
import com.qwaecd.paramagic.spell.view.HybridCasterSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

@PlatformScope(PlatformScopeType.CLIENT)
public final class ArcaneClientFactory {
    private ArcaneClientFactory() {
    }

    @Nullable
    public static ClientSession spawnOnClient(Level level, SpellSessionRef sessionRef, CircleAssets circleAssets, Entity fallbackSource) {
        ClientSessionManager manager = SessionManagers.getForClient();
        ClientSession existingSession = manager.getSession(sessionRef.serverSessionId);
        if (existingSession != null) {
            Entity casterSource = CasterUtils.tryFindCaster(level, sessionRef);
            if (casterSource != null) {
                existingSession.upsertCasterSource(casterSource);
            }
            return existingSession;
        }

        Entity casterSource = CasterUtils.tryFindCaster(level, sessionRef);
        HybridCasterSource hybridCasterSource = HybridCasterSource.create(casterSource, fallbackSource);
        SpellPresentation presentation = new ArcanePresentation(sessionRef.serverSessionId, hybridCasterSource, circleAssets);
        ClientSession session = manager.createPresentationSession(sessionRef.serverSessionId, presentation, hybridCasterSource);
        Networking.get().sendToServer(new C2SSessionAttachPacket(sessionRef.serverSessionId));
        return session;
    }
}
