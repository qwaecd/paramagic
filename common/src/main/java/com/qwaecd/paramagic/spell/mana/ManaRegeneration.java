package com.qwaecd.paramagic.spell.mana;

import com.qwaecd.paramagic.spell.caster.PlayerCaster;
import com.qwaecd.paramagic.spell.core.SessionManagers;
import com.qwaecd.paramagic.spell.server.ServerSessionManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public final class ManaRegeneration {
    public static void tick(ServerLevel level) {
//        ServerSessionManager manager = SessionManagers.getForServer(level);
        for (ServerPlayer player : level.players()) {
//            if (!manager.getSessionsByUUID(PlayerCaster.getCasterIdFromPlayer(player)).isEmpty()) {
//                continue;
//            }
            int currentMana = ManaAccess.getMana(player);
            int maxMana = ManaAccess.getMaxMana(player);
            if (currentMana < maxMana) {
                ManaAccess.tryConsumeMana(player, -10);
            }
        }
    }
}
