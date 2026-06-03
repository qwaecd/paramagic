package com.qwaecd.paramagic.network.handler;

import com.qwaecd.paramagic.network.api.NetworkContext;
import com.qwaecd.paramagic.network.packet.inventory.C2SAddSpellTreeNodePacket;
import com.qwaecd.paramagic.network.packet.inventory.C2SClickTreeNodePacket;
import com.qwaecd.paramagic.network.packet.inventory.C2SDeleteSpellTreeSubtreePacket;
import com.qwaecd.paramagic.network.packet.inventory.C2SSetSpellTreeNodeOperatorPacket;
import com.qwaecd.paramagic.network.packet.inventory.C2SSubmitEditedParaDataPacket;
import com.qwaecd.paramagic.ui.inventory.slot.SlotActionHandler;
import com.qwaecd.paramagic.ui.menu.SpellEditMenu;
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

    public static void submitEditedParaData(C2SSubmitEditedParaDataPacket packet, NetworkContext context) {
        ServerPlayer player = context.getPlayer();
        if (!validatePlayerAndHandler(player)) {
            return;
        }

        SlotActionHandler handler = (SlotActionHandler) Objects.requireNonNull(player).containerMenu;
        if (handler instanceof SpellEditMenu menu) {
            menu.submitEditedParaData(player, packet.getParaData(), packet.getCacheToken(), packet.getCacheVersion());
            return;
        }
        handler.submitEditedParaData(player, packet.getParaData());
    }

    public static void addSpellTreeNode(C2SAddSpellTreeNodePacket packet, NetworkContext context) {
        ServerPlayer player = context.getPlayer();
        if (!validatePlayerAndHandler(player)) {
            return;
        }
        if (Objects.requireNonNull(player).containerMenu instanceof SpellEditMenu menu) {
            menu.addSpellTreeNode(
                    player,
                    packet.getVersion(),
                    packet.getParentNodeId(),
                    packet.getChildIndex(),
                    packet.isUseCarriedOperator()
            );
        }
    }

    public static void deleteSpellTreeSubtree(C2SDeleteSpellTreeSubtreePacket packet, NetworkContext context) {
        ServerPlayer player = context.getPlayer();
        if (!validatePlayerAndHandler(player)) {
            return;
        }
        if (Objects.requireNonNull(player).containerMenu instanceof SpellEditMenu menu) {
            menu.deleteSpellTreeSubtree(player, packet.getVersion(), packet.getNodeId());
        }
    }

    public static void setSpellTreeNodeOperator(C2SSetSpellTreeNodeOperatorPacket packet, NetworkContext context) {
        ServerPlayer player = context.getPlayer();
        if (!validatePlayerAndHandler(player)) {
            return;
        }
        if (Objects.requireNonNull(player).containerMenu instanceof SpellEditMenu menu) {
            menu.setSpellTreeNodeOperator(player, packet.getVersion(), packet.getNodeId(), packet.getAction());
        }
    }
}
