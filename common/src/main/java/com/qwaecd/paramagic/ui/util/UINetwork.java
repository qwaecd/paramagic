package com.qwaecd.paramagic.ui.util;

import com.qwaecd.paramagic.network.Networking;
import com.qwaecd.paramagic.network.packet.inventory.C2SClickTreeNodePacket;

public final class UINetwork {
    private UINetwork() {}
    public static void sendClickTreeNode(String nodePath) {
        Networking.get().sendToServer(new C2SClickTreeNodePacket(nodePath));
    }
}
