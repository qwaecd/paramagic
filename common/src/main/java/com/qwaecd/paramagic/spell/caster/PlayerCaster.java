package com.qwaecd.paramagic.spell.caster;

import com.qwaecd.paramagic.spell.Spell;
import com.qwaecd.paramagic.spell.session.server.ServerSession;
import com.qwaecd.paramagic.spell.session.server.ServerSessionManager;
import net.minecraft.world.entity.player.Player;

import java.util.Set;


public class PlayerCaster extends BaseSpellCaster implements SpellCaster {
    public PlayerCaster(Player player) {
        super(player.getUUID());
    }

    @Override
    public boolean canStartSession(Spell spell, ServerSessionManager manager) {
        Set<ServerSession> sessionSet = manager.getSessionsByCaster(this);
        return sessionSet.isEmpty();
    }
}
