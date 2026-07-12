package com.qwaecd.paramagic.spell.mana;

import com.qwaecd.paramagic.network.Networking;
import com.qwaecd.paramagic.network.packet.mana.S2CManaSyncPacket;
import net.minecraft.server.level.ServerPlayer;

/** Sends the current server-side mana snapshot to its owning client. */
public final class ManaSync {
    private ManaSync() {
    }

    public static void sync(ServerPlayer player) {
        if (!Networking.isInitialized()) {
            return;
        }
        Networking.get().sendToPlayer(player, new S2CManaSyncPacket(
                ManaAccess.getMana(player),
                ManaAccess.getMaxMana(player)
        ));
    }
}
