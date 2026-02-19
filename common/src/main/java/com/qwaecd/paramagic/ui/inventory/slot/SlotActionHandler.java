package com.qwaecd.paramagic.ui.inventory.slot;

import net.minecraft.server.level.ServerPlayer;

public interface SlotActionHandler {
    void clickNode(ServerPlayer player, String nodePath);
}
