package com.qwaecd.paramagic.network;

import com.qwaecd.paramagic.network.api.PlatformNetworking;
import com.qwaecd.paramagic.network.handler.ServerSessionHandlers;
import com.qwaecd.paramagic.network.handler.ServerSpellTreeHandler;
import com.qwaecd.paramagic.network.packet.inventory.C2SOpenSpellEditMenuPacket;
import com.qwaecd.paramagic.network.packet.inventory.C2SSpellTreeEditPacket;
import com.qwaecd.paramagic.network.packet.session.C2SSessionAttachPacket;

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
        networking.register(C2SOpenSpellEditMenuPacket.IDENTIFIER, C2SOpenSpellEditMenuPacket.class, C2SOpenSpellEditMenuPacket::decode, ServerSpellTreeHandler::openSpellEditMenu);
        networking.register(C2SSpellTreeEditPacket.IDENTIFIER, C2SSpellTreeEditPacket.class, C2SSpellTreeEditPacket::decode, ServerSpellTreeHandler::spellTreeEdit);
        networking.register(C2SSessionAttachPacket.IDENTIFIER, C2SSessionAttachPacket.class, C2SSessionAttachPacket::decode, ServerSessionHandlers::attachSession);
    }

    public static PlatformNetworking get() {
        if (NETWORKING == null) {
            throw new IllegalStateException("Networking not initialized yet!");
        }
        return NETWORKING;
    }
}
