package com.qwaecd.paramagic.spell.arcane;

import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.spell.SpellUnion;
import com.qwaecd.paramagic.spell.caster.SpellCaster;
import com.qwaecd.paramagic.spell.client.CircleAssets;
import com.qwaecd.paramagic.spell.core.SessionManagers;
import com.qwaecd.paramagic.spell.core.SpellSessionRef;
import com.qwaecd.paramagic.spell.core.store.AllSessionDataKeys;
import com.qwaecd.paramagic.spell.core.store.SessionDataStore;
import com.qwaecd.paramagic.spell.core.store.SessionDataValue;
import com.qwaecd.paramagic.spell.server.ServerSession;
import com.qwaecd.paramagic.spell.server.ServerSessionManager;
import com.qwaecd.paramagic.thaumaturgy.ParaCrystalData;
import com.qwaecd.paramagic.thaumaturgy.node.ParaTree;
import com.qwaecd.paramagic.world.entity.SpellAnchorEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.HitResult;
import org.joml.Vector3f;

import javax.annotation.Nullable;

@PlatformScope(PlatformScopeType.SERVER)
public final class ArcaneSpellCaster {
    private ArcaneSpellCaster() {
    }

    @Nullable
    public static ServerSession castOnServer(ServerLevel level, SpellCaster caster, ParaCrystalData crystal) {
        ParaTree paraTree = new ParaTree(crystal.getParaData());
        paraTree.updateAll(crystal);

        ServerSessionManager manager = SessionManagers.getForServer(level);
        ServerSession session = manager.tryCreateRuntimeSession(level, caster, new ArcaneRuntime(paraTree));
        if (session == null) {
            return null;
        }

        processDataStore(session, level, caster);

        SpellAnchorEntity spellAnchorEntity = new SpellAnchorEntity(level);
        spellAnchorEntity.moveTo(caster.position());

        CircleAssets circleAssets = new CircleAssets(crystal.getParaData(), null);
        SpellUnion union = SpellUnion.ofPara(SpellSessionRef.fromSession(session), circleAssets);
        connect(session, spellAnchorEntity, union);

        level.addFreshEntity(spellAnchorEntity);
        session.start();
        return session;
    }

    private static void processDataStore(ServerSession session, ServerLevel level, SpellCaster caster) {
        Entity casterEntity = level.getEntity(caster.getEntityNetworkId());
        Vector3f position;
        if (casterEntity == null) {
            position = caster.position().toVector3f();
        } else {
            HitResult hitResult = casterEntity.pick(128.0D, 0.0F, false);
            position = hitResult.getLocation().toVector3f();
        }
        SessionDataStore dataStore = session.getDataStore();
        dataStore.setValue(AllSessionDataKeys.firstPosition, SessionDataValue.of(AllSessionDataKeys.firstPosition.id, position));
        session.syncDataStore();
    }

    private static void connect(ServerSession session, SpellAnchorEntity entity, SpellUnion spellUnion) {
        session.connectAnchor(entity);
        entity.attachSessionData(session, spellUnion);
    }
}
