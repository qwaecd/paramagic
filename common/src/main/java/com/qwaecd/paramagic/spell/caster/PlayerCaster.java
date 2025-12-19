package com.qwaecd.paramagic.spell.caster;

import com.qwaecd.paramagic.spell.core.Spell;
import com.qwaecd.paramagic.spell.session.server.ServerSession;
import com.qwaecd.paramagic.spell.session.server.ServerSessionManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.Set;


public class PlayerCaster extends BaseSpellCaster implements SpellCaster {
    private final Player source;

    protected PlayerCaster(Player player) {
        super(player.getUUID());
        this.source = player;
    }

    @Override
    public int getEntityNetworkId() {
        return this.source.getId();
    }

    @Override
    public boolean canStartSession(Spell spell, ServerSessionManager manager) {
        Set<ServerSession> sessionSet = manager.getSessionsByCaster(this);
        return sessionSet.isEmpty();
    }

    @Override
    public Vec3 position() {
        return this.source.position();
    }

    public static PlayerCaster create(Player player) {
        return new PlayerCaster(player);
    }
}
