package com.qwaecd.paramagic.spell.api;

import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.spell.BuiltinSpellId;
import com.qwaecd.paramagic.spell.builtin.client.BuiltinSpellVisualRegistry;
import com.qwaecd.paramagic.spell.builtin.client.VisualEntry;
import com.qwaecd.paramagic.spell.session.SessionManagers;
import com.qwaecd.paramagic.spell.session.SpellSessionRef;
import com.qwaecd.paramagic.spell.session.client.ClientSession;
import com.qwaecd.paramagic.spell.session.client.ClientSessionManager;
import com.qwaecd.paramagic.spell.session.client.MachineSessionClient;
import com.qwaecd.paramagic.spell.state.AllMachineEvents;
import com.qwaecd.paramagic.spell.util.CasterUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

@PlatformScope(PlatformScopeType.CLIENT)
public class SpellSpawnerClient {
    @Nullable
    public static MachineSessionClient spawnInternalOnClient(
            Level level,
            SpellSessionRef sessionRef,
            BuiltinSpellId spellId,
            Entity fallbackSource
    ) {
        ClientSessionManager manager = SessionManagers.getForClient();

        ClientSession existSession = (ClientSession) manager.getSession(sessionRef.serverSessionId);
        if (existSession == null) {
            MachineSessionClient session = manager.tryCreateMachineSession(level, sessionRef, spellId, fallbackSource);
            if (session != null) {
                processBuiltinApi(session, sessionRef, spellId);
                session.postEvent(AllMachineEvents.START_CASTING);
                return session;
            }
        } else {
            tryUpsertCasterSource(level, existSession, sessionRef);
        }

        return null;
    }

    private static void processBuiltinApi(MachineSessionClient session, SpellSessionRef sessionRef, BuiltinSpellId spellId) {
        VisualEntry entry = BuiltinSpellVisualRegistry.getSpell(spellId);
        Objects.requireNonNull(entry).getVisual().onCreateSession(session, sessionRef);
    }

    private static void tryUpsertCasterSource(@Nonnull Level level, ClientSession session, SpellSessionRef sessionRef) {
        Entity casterSource = CasterUtils.tryFindCaster(level, sessionRef);
        if (casterSource != null) {
            session.upsertCasterSource(casterSource);
        }
    }
}
