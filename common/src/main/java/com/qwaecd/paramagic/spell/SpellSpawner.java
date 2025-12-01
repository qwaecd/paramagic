package com.qwaecd.paramagic.spell;

import com.qwaecd.paramagic.spell.caster.SpellCaster;
import com.qwaecd.paramagic.spell.session.SessionManagers;
import com.qwaecd.paramagic.spell.session.server.ServerSessionManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public final class SpellSpawner {
    public static void spawnSpell(Level level, SpellCaster caster, Spell spell) {
        if (level.isClientSide()) {
            spawnOnClient(level, caster, spell);
        } else {
            spawnOnServer((ServerLevel) level, caster, spell);
        }
    }

    private static void spawnOnServer(ServerLevel level, SpellCaster caster, Spell spell) {
        ServerSessionManager manager = SessionManagers.getForServer(level);
        manager.tryCreateSession(level, caster, spell);
    }

    private static void spawnOnClient(Level level, SpellCaster caster, Spell spell) {
    }
}
