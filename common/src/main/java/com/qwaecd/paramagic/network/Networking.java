package com.qwaecd.paramagic.network;

import com.qwaecd.paramagic.network.api.PlatformNetworking;
import com.qwaecd.paramagic.network.handler.ServerSessionHandlers;
import com.qwaecd.paramagic.network.handler.ServerSpellTreeHandler;
import com.qwaecd.paramagic.network.packet.effect.S2CEffectKill;
import com.qwaecd.paramagic.network.packet.effect.S2CEffectSpawn;
import com.qwaecd.paramagic.network.packet.inventory.C2SOpenSpellEditMenuPacket;
import com.qwaecd.paramagic.network.packet.inventory.C2SSpellTreeEditPacket;
import com.qwaecd.paramagic.network.packet.inventory.S2CSpellTreeEditRejectedPacket;
import com.qwaecd.paramagic.network.packet.mana.S2CManaSyncPacket;
import com.qwaecd.paramagic.network.packet.session.C2SSessionAttachPacket;
import com.qwaecd.paramagic.network.packet.session.S2CSessionDataSyncPacket;
import com.qwaecd.paramagic.network.packet.session.S2CSessionStopPacket;

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

        networking.registerClientbound(S2CEffectSpawn.IDENTIFIER, S2CEffectSpawn.class, S2CEffectSpawn::decode);
        networking.registerClientbound(S2CEffectKill.IDENTIFIER, S2CEffectKill.class, S2CEffectKill::decode);
        networking.registerClientbound(S2CManaSyncPacket.IDENTIFIER, S2CManaSyncPacket.class, S2CManaSyncPacket::decode);
        networking.registerClientbound(S2CSpellTreeEditRejectedPacket.IDENTIFIER, S2CSpellTreeEditRejectedPacket.class, S2CSpellTreeEditRejectedPacket::decode);
        networking.registerClientbound(S2CSessionDataSyncPacket.IDENTIFIER, S2CSessionDataSyncPacket.class, S2CSessionDataSyncPacket::decode);
        networking.registerClientbound(S2CSessionStopPacket.IDENTIFIER, S2CSessionStopPacket.class, S2CSessionStopPacket::decode);
    }

    public static PlatformNetworking get() {
        if (NETWORKING == null) {
            throw new IllegalStateException("Networking not initialized yet!");
        }
        return NETWORKING;
    }

    public static boolean isInitialized() {
        return NETWORKING != null;
    }
}
