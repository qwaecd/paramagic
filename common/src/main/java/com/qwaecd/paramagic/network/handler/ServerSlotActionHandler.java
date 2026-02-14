package com.qwaecd.paramagic.network.handler;

import com.qwaecd.paramagic.network.api.NetworkContext;
import com.qwaecd.paramagic.network.packet.inventory.C2SSlotActionPacket;
import com.qwaecd.paramagic.ui.inventory.SlotActionHandler;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerSlotActionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerSlotActionHandler.class);

    public static void handle(C2SSlotActionPacket packet, NetworkContext context) {
        ServerPlayer player = context.getPlayer();
        if (player == null) {
            LOGGER.warn("Received slot action packet without player context");
            return;
        }

        if (!(player.containerMenu instanceof SlotActionHandler handler)) {
            LOGGER.warn("Player {} has menu that does not implement SlotActionHandler", player.getName().getString());
            return;
        }

        int slotIndex = packet.getSlotIndex();
        // slotIndex == -1 is valid for operations not tied to a specific slot
        if (slotIndex != -1 && (slotIndex < 0 || slotIndex >= player.containerMenu.slots.size())) {
            LOGGER.warn("Invalid slot index {} from player {}", slotIndex, player.getName().getString());
            return;
        }

        handler.handleSlotAction(player, slotIndex, packet.getAction(), packet.getExtraData());
    }
}
