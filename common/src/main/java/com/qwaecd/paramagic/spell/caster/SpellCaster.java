package com.qwaecd.paramagic.spell.caster;

import com.qwaecd.paramagic.spell.server.ServerSessionManager;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public interface SpellCaster {
    UUID getCasterId();
    int getMana();
    int getMaxMana();
    void setMana(int mana);
    void setMaxMana(int maxMana);

    default boolean tryConsumeMana(int amount) {
        int currentMana = this.getMana();
        if (currentMana < amount) {
            return false;
        }
        this.setMana(currentMana - amount);
        return true;
    }

    default int getEntityNetworkId() {
        return -1;
    }
    boolean canStartSession(ServerSessionManager manager);
    boolean shouldContinueSession(ServerSessionManager manager);
    Vec3 position();
    Vec3 eyePosition();
    Vec3 forwardVector();
}
