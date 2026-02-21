package com.qwaecd.paramagic.spell.caster;

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
    public boolean canStartSession(ServerSessionManager manager) {
        Set<ServerSession> sessionSet = manager.getSessionsByCaster(this);
        return sessionSet.isEmpty();
    }

    @Override
    public boolean shouldContinueSession(ServerSessionManager manager) {
        return this.source.isUsingItem() && !this.source.isRemoved();
    }

    @Override
    public Vec3 position() {
        return this.source.position();
    }

    @Override
    public Vec3 eyePosition() {
        return this.source.getEyePosition();
    }

    @Override
    public Vec3 forwardVector() {
        return this.source.getLookAngle();
    }

    public static PlayerCaster create(Player player) {
        return new PlayerCaster(player);
    }
}
