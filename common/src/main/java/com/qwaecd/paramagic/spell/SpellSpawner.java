package com.qwaecd.paramagic.spell;

import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.spell.caster.SpellCaster;
import com.qwaecd.paramagic.spell.core.Spell;
import com.qwaecd.paramagic.spell.listener.ExecutionListener;
import com.qwaecd.paramagic.spell.session.SessionManagers;
import com.qwaecd.paramagic.spell.session.server.ServerSession;
import com.qwaecd.paramagic.spell.session.server.ServerSessionManager;
import com.qwaecd.paramagic.spell.session.store.AllSessionDataKeys;
import com.qwaecd.paramagic.spell.session.store.SessionDataStore;
import com.qwaecd.paramagic.spell.session.store.SessionDataValue;
import com.qwaecd.paramagic.spell.state.AllMachineEvents;
import com.qwaecd.paramagic.world.entity.SpellAnchorEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.HitResult;
import org.joml.Vector3f;

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
            spellAnchorEntity.moveTo(caster.position());
            connect(serverSession, spellAnchorEntity);

            level.addFreshEntity(spellAnchorEntity);
            createListener(serverSession);
            processDataStore(serverSession, level, caster, spell);
        }

        return serverSession;
    }

    private static void processDataStore(ServerSession session, ServerLevel level, SpellCaster caster, Spell spell) {
        Entity casterEntity = level.getEntity(caster.getEntityNetworkId());
        Vector3f v;
        if (casterEntity == null) {
            v = caster.position().toVector3f();
        } else {
            HitResult hitResult = casterEntity.pick(128.0D, 0.0F, false);
            v = hitResult.getLocation().toVector3f();
        }
        SessionDataStore dataStore = session.getDataStore();
        dataStore.setValue(AllSessionDataKeys.firstPosition, SessionDataValue.of(AllSessionDataKeys.firstPosition.id, v));
        session.syncDataStore();
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
