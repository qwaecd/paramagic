package com.qwaecd.paramagic.ui.util;

import com.qwaecd.paramagic.network.Networking;
import com.qwaecd.paramagic.network.packet.inventory.*;

import javax.annotation.Nonnull;

public final class UINetwork {
    private UINetwork() {}
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
