package com.qwaecd.paramagic.lifecycle;

import com.qwaecd.paramagic.spell.caster.ManaSync;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

/** Fabric lifecycle hooks for initial and post-respawn mana snapshots. */
public final class FabricManaSyncEvents {
    private FabricManaSyncEvents() {
    }

    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> ManaSync.sync(handler.player));
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> ManaSync.sync(newPlayer));
    }
}
