package com.qwaecd.paramagic.spell.caster;

import com.qwaecd.paramagic.spell.Spell;
import com.qwaecd.paramagic.spell.session.server.ServerSessionManager;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public interface SpellCaster<T> {
    T get();
    UUID getCasterId();
    default int getEntityNetworkId() {
        return -1;
    }
    boolean canStartSession(Spell spell, ServerSessionManager manager);
    Vec3 position();
}
