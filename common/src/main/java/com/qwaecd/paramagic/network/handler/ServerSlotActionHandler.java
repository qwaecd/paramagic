package com.qwaecd.paramagic.network.handler;

import com.qwaecd.paramagic.network.api.NetworkContext;
import com.qwaecd.paramagic.network.packet.inventory.C2SClickTreeNodePacket;
import com.qwaecd.paramagic.ui.inventory.slot.SlotActionHandler;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class ServerSlotActionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerSlotActionHandler.class);

    private static boolean validatePlayerAndHandler(ServerPlayer player) {
        if (player == null) {
            LOGGER.warn("Received slot action packet without player context");
            return false;
        }

        if (!(player.containerMenu instanceof SlotActionHandler)) {
            LOGGER.warn("Player {} has menu that does not implement SlotActionHandler", player.getName().getString());
            return false;
        }
        return true;
    }

    public static void clickNode(C2SClickTreeNodePacket packet, NetworkContext context) {
        ServerPlayer player = context.getPlayer();
        if (!validatePlayerAndHandler(player)) {
            return;
        }

        SlotActionHandler handler = (SlotActionHandler) Objects.requireNonNull(player).containerMenu;
        handler.clickNode(player, packet.getNodePath());
    }
}
