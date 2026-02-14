package com.qwaecd.paramagic.network;

import com.qwaecd.paramagic.network.api.PlatformNetworking;
import com.qwaecd.paramagic.network.handler.ServerSlotActionHandler;
import com.qwaecd.paramagic.network.packet.inventory.C2SSlotActionPacket;

public class Networking {
    public static final String PROTOCOL_VERSION = "1";
    private static PlatformNetworking NETWORKING = null;

    public static void init(PlatformNetworking networking) {
        if (NETWORKING != null) {
            return;
        }
        NETWORKING = networking;
        registerAll(networking);
    }

    private static void registerAll(PlatformNetworking networking) {
        networking.register(C2SSlotActionPacket.IDENTIFIER, C2SSlotActionPacket.class, C2SSlotActionPacket::decode, ServerSlotActionHandler::handle);
    }

    public static PlatformNetworking get() {
        if (NETWORKING == null) {
            throw new IllegalStateException("Networking not initialized yet!");
        }
        return NETWORKING;
    }
}
