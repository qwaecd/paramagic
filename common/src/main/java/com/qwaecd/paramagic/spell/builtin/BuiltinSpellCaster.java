package com.qwaecd.paramagic.spell.builtin;

import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.spell.BuiltinSpellId;
import com.qwaecd.paramagic.spell.SpellUnion;
import com.qwaecd.paramagic.spell.caster.SpellCaster;
import com.qwaecd.paramagic.spell.core.SessionManagers;
import com.qwaecd.paramagic.spell.core.SpellSessionRef;
import com.qwaecd.paramagic.spell.core.store.AllSessionDataKeys;
import com.qwaecd.paramagic.spell.core.store.SessionDataStore;
import com.qwaecd.paramagic.spell.core.store.SessionDataValue;
import com.qwaecd.paramagic.spell.server.ServerSession;
import com.qwaecd.paramagic.spell.server.ServerSessionManager;
import com.qwaecd.paramagic.spell.server.SpellRuntime;
import com.qwaecd.paramagic.world.entity.SpellAnchorEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.HitResult;
import org.joml.Vector3f;

import javax.annotation.Nullable;

@PlatformScope(PlatformScopeType.SERVER)
public final class BuiltinSpellCaster {
    private BuiltinSpellCaster() {
    }

    @Nullable
    public static ServerSession castOnServer(ServerLevel level, SpellCaster caster, BuiltinSpellId spellId) {
        SpellRuntime runtime = BuiltinSpellRuntimeRegistry.create(spellId);
        if (runtime == null) {
            return null;
        }

        ServerSessionManager manager = SessionManagers.getForServer(level);
        ServerSession session = manager.tryCreateRuntimeSession(level, caster, runtime);
        if (session == null) {
            return null;
        }

        SpellAnchorEntity spellAnchorEntity = new SpellAnchorEntity(level);
        spellAnchorEntity.moveTo(caster.position());

        SpellUnion spellUnion = SpellUnion.ofBuiltin(SpellSessionRef.fromSession(session), spellId);
        connect(session, spellAnchorEntity, spellUnion);

        level.addFreshEntity(spellAnchorEntity);
        processDataStore(session, level, caster);
        session.start();
        return session;
    }

    private static void processDataStore(ServerSession session, ServerLevel level, SpellCaster caster) {
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

    private static void connect(ServerSession session, SpellAnchorEntity entity, SpellUnion spellUnion) {
        session.connectAnchor(entity);
        entity.attachSessionData(session, spellUnion);
    }
}
