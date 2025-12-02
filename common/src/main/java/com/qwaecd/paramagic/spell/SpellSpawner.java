package com.qwaecd.paramagic.spell;

import com.qwaecd.paramagic.entity.ModEntityTypes;
import com.qwaecd.paramagic.entity.SpellAnchorEntity;
import com.qwaecd.paramagic.spell.caster.SpellCaster;
import com.qwaecd.paramagic.spell.session.SessionManagers;
import com.qwaecd.paramagic.spell.session.SpellSession;
import com.qwaecd.paramagic.spell.session.server.ServerSession;
import com.qwaecd.paramagic.spell.session.server.ServerSessionManager;
import com.qwaecd.paramagic.spell.state.event.AllMachineEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public final class SpellSpawner {
    @Nullable
    @SuppressWarnings("UnusedReturnValue")
    public static SpellSession spawnSpell(Level level, SpellCaster<?> caster, Spell spell) {
        if (level.isClientSide()) {
            return spawnOnClient(level, caster, spell);
        } else {
            return spawnOnServer((ServerLevel) level, caster, spell);
        }
    }

    @Nullable
    public static SpellSession spawnOnServer(ServerLevel level, SpellCaster<?> caster, Spell spell) {
        ServerSessionManager manager = SessionManagers.getForServer(level);
        ServerSession serverSession = manager.tryCreateSession(level, caster, spell);

        if (serverSession != null) {
            SpellAnchorEntity spellAnchorEntity = new SpellAnchorEntity(level);

            spellAnchorEntity.moveTo(caster.position());
            spellAnchorEntity.attachSpell(spell);
            spellAnchorEntity.postEvent(AllMachineEvents.START_CASTING);

            level.addFreshEntity(spellAnchorEntity);
        }

        return serverSession;
    }

    @Nullable
    public static SpellSession spawnOnClient(Level level, SpellCaster<?> caster, Spell spell) {
        return null;
    }
}
