package com.qwaecd.paramagic.ui.util;

import com.qwaecd.paramagic.network.Networking;
import com.qwaecd.paramagic.network.packet.inventory.C2SOpenSpellEditMenuPacket;
import com.qwaecd.paramagic.network.packet.inventory.C2SSpellTreeEditPacket;
import com.qwaecd.paramagic.network.packet.inventory.SpellTreeEditOperation;

import javax.annotation.Nonnull;

public final class UINetwork {
    private UINetwork() {}

    public static void requestOpenSpellEditMenu() {
        Networking.get().sendToServer(new C2SOpenSpellEditMenuPacket());
    }

    public static void sendSpellTreeEdit(
            int editEpoch,
            int requestId,
            int baseVersion,
            @Nonnull SpellTreeEditOperation operation,
            @Nonnull String nodeId,
            int childIndex
    ) {
        Networking.get().sendToServer(new C2SSpellTreeEditPacket(
                editEpoch, requestId, baseVersion, operation, nodeId, childIndex
        ));
    }
}
