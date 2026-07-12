package com.qwaecd.paramagic.network;

import com.qwaecd.paramagic.network.api.PlatformNetworking;
import com.qwaecd.paramagic.network.handler.ClientEffectHandlers;
import com.qwaecd.paramagic.network.handler.ClientManaHandlers;
import com.qwaecd.paramagic.network.handler.ClientSessionDataHandlers;
import com.qwaecd.paramagic.network.handler.ClientSpellTreeHandlers;
import com.qwaecd.paramagic.network.packet.effect.S2CEffectKill;
import com.qwaecd.paramagic.network.packet.effect.S2CEffectSpawn;
import com.qwaecd.paramagic.network.packet.inventory.S2CSpellTreeEditRejectedPacket;
import com.qwaecd.paramagic.network.packet.mana.S2CManaSyncPacket;
import com.qwaecd.paramagic.network.packet.session.S2CSessionDataSyncPacket;
import com.qwaecd.paramagic.network.packet.session.S2CSessionStopPacket;
import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;

@PlatformScope(PlatformScopeType.CLIENT)
public class ClientNetworking {
    public static void registerAllOnClient(PlatformNetworking networking) {
        networking.registerClientHandler(S2CEffectSpawn.IDENTIFIER, ClientEffectHandlers::spawnOnClient);
        networking.registerClientHandler(S2CEffectKill.IDENTIFIER, ClientEffectHandlers::killEffect);
        networking.registerClientHandler(S2CManaSyncPacket.IDENTIFIER, ClientManaHandlers::sync);
        networking.registerClientHandler(S2CSessionDataSyncPacket.IDENTIFIER, ClientSessionDataHandlers::syncSessionData);
        networking.registerClientHandler(S2CSessionStopPacket.IDENTIFIER, ClientSessionDataHandlers::stopSession);
        networking.registerClientHandler(S2CSpellTreeEditRejectedPacket.IDENTIFIER, ClientSpellTreeHandlers::spellTreeEditRejected);
    }
}
