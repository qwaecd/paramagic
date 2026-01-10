package com.qwaecd.paramagic.spell;

import com.qwaecd.paramagic.world.entity.SpellAnchorEntity;
import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.spell.caster.SpellCaster;
import com.qwaecd.paramagic.spell.core.Spell;
import com.qwaecd.paramagic.spell.listener.ExecutionListener;
import com.qwaecd.paramagic.spell.session.SessionManagers;
import com.qwaecd.paramagic.spell.session.server.ServerSession;
import com.qwaecd.paramagic.spell.session.server.ServerSessionManager;
import com.qwaecd.paramagic.spell.state.AllMachineEvents;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nullable;

@SuppressWarnings("UnusedReturnValue")
@PlatformScope(PlatformScopeType.SERVER)
public class SpellSpawner {
    @Nullable
    public static ServerSession spawnOnServer(ServerLevel level, SpellCaster caster, Spell spell) {
        ServerSessionManager manager = SessionManagers.getForServer(level);
        ServerSession serverSession = manager.tryCreateSession(level, caster, spell);

        if (serverSession != null) {
            serverSession.postEvent(AllMachineEvents.START_CASTING);

            SpellAnchorEntity spellAnchorEntity = new SpellAnchorEntity(level);
            connect(serverSession, spellAnchorEntity);

            spellAnchorEntity.moveTo(caster.position());
            level.addFreshEntity(spellAnchorEntity);
            createListener(serverSession);
        }

        return serverSession;
    }

    private static void connect(ServerSession session, SpellAnchorEntity entity) {
        session.connectAnchor(entity);
        entity.attachSession(session);
    }

    private static void createListener(ServerSession session) {
        ExecutionListener executionListener = new ExecutionListener();
        session.registerListener(executionListener);
    }
}
