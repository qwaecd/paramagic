package com.qwaecd.paramagic.ui.inventory.slot;

import com.qwaecd.paramagic.data.para.struct.ParaData;
import net.minecraft.server.level.ServerPlayer;

public interface SlotActionHandler {
    void clickNode(ServerPlayer player, String nodePath);

    void submitEditedParaData(ServerPlayer player, ParaData paraData);
}
