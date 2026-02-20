package com.qwaecd.paramagic.spell.api;

import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.spell.BuiltinSpellId;
import com.qwaecd.paramagic.spell.SpellUnion;
import com.qwaecd.paramagic.spell.builtin.BuiltinSpell;
import com.qwaecd.paramagic.spell.builtin.BuiltinSpellEntry;
import com.qwaecd.paramagic.spell.builtin.BuiltinSpellRegistry;
import com.qwaecd.paramagic.spell.caster.SpellCaster;
import com.qwaecd.paramagic.spell.config.CircleAssets;
import com.qwaecd.paramagic.spell.session.SessionManagers;
import com.qwaecd.paramagic.spell.session.SpellSessionRef;
import com.qwaecd.paramagic.spell.session.server.ArcSessionServer;
import com.qwaecd.paramagic.spell.session.server.MachineSessionServer;
import com.qwaecd.paramagic.spell.session.server.ServerSession;
import com.qwaecd.paramagic.spell.session.server.ServerSessionManager;
import com.qwaecd.paramagic.spell.session.store.AllSessionDataKeys;
import com.qwaecd.paramagic.spell.session.store.SessionDataStore;
import com.qwaecd.paramagic.spell.session.store.SessionDataValue;
import com.qwaecd.paramagic.spell.state.AllMachineEvents;
import com.qwaecd.paramagic.spell.state.SpellStateMachine;
import com.qwaecd.paramagic.thaumaturgy.ParaCrystalData;
import com.qwaecd.paramagic.thaumaturgy.node.ParaTree;
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
    public static ArcSessionServer spawnOnServer(
            ServerLevel level,
            SpellCaster caster,
            ParaCrystalData crystal
    ) {
        ParaTree paraTree = new ParaTree(crystal.getParaData());
        paraTree.updateAll(crystal);
        ServerSessionManager manager = SessionManagers.getForServer(level);
        ArcSessionServer session = manager.tryCreateArcSession(level, caster, paraTree);
        if (session != null) {
            processDataStore(session, level, caster);

            SpellAnchorEntity spellAnchorEntity = new SpellAnchorEntity(level);
            spellAnchorEntity.moveTo(caster.position());
            level.addFreshEntity(spellAnchorEntity);

            CircleAssets circleAssets = new CircleAssets(crystal.getParaData(), null);
            SpellUnion union = SpellUnion.ofPara(SpellSessionRef.fromSession(session), circleAssets);
            connect(session, spellAnchorEntity, union);
        }
        return session;
    }

    @Nullable
    public static MachineSessionServer spawnInternalOnServer(ServerLevel level, SpellCaster caster, BuiltinSpellId spellId) {
        BuiltinSpellEntry entry = BuiltinSpellRegistry.getSpell(spellId);

        if (entry == null) {
            return null;
        }
        BuiltinSpell spell = entry.getSpell();
        SpellStateMachine machine = spell.createMachine();

        ServerSessionManager manager = SessionManagers.getForServer(level);
        MachineSessionServer session = manager.tryCreateMachineSession(level, caster, machine, entry.createExecutor());

        if (session != null) {
            session.postEvent(AllMachineEvents.START_CASTING);

            SpellAnchorEntity spellAnchorEntity = new SpellAnchorEntity(level);
            spellAnchorEntity.moveTo(caster.position());
            SpellUnion spellUnion = SpellUnion.ofBuiltin(SpellSessionRef.fromSession(session), spellId);
            connect(session, spellAnchorEntity, spellUnion);

            level.addFreshEntity(spellAnchorEntity);
            processDataStore(session, level, caster);
            spell.onCreateSession(session);
        }

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
