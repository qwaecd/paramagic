package com.qwaecd.paramagic.network.handler;

import com.qwaecd.paramagic.network.api.NetworkContext;
import com.qwaecd.paramagic.network.packet.inventory.C2SOpenSpellEditMenuPacket;
import com.qwaecd.paramagic.network.packet.inventory.C2SSpellTreeEditPacket;
import com.qwaecd.paramagic.ui.menu.SpellEditMenu;
import com.qwaecd.paramagic.world.block.SpellEditTableBlock;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ServerSpellTreeHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerSpellTreeHandler.class);

    private ServerSpellTreeHandler() {}

    public static void openSpellEditMenu(C2SOpenSpellEditMenuPacket packet, NetworkContext context) {
        ServerPlayer player = context.getPlayer();
        if (player == null) {
            LOGGER.warn("Received spell edit menu request without player context");
            return;
        }
        SpellEditTableBlock.openMenu(player);
    }

    public static void spellTreeEdit(C2SSpellTreeEditPacket packet, NetworkContext context) {
        ServerPlayer player = context.getPlayer();
        if (player == null) {
            LOGGER.warn("Received spell tree edit packet without player context");
            return;
        }
        if (player.containerMenu instanceof SpellEditMenu menu) {
            menu.applySpellTreeEdit(player, packet);
            return;
        }
        LOGGER.warn("Player {} has an incompatible menu for spell tree editing", player.getName().getString());
    }
}
