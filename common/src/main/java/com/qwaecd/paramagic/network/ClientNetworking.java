package com.qwaecd.paramagic.network;

import com.qwaecd.paramagic.network.api.PlatformNetworking;
import com.qwaecd.paramagic.network.handler.ClientEffectHandlers;
import com.qwaecd.paramagic.network.packet.particle.S2CEffectSpawn;
import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;

@PlatformScope(PlatformScopeType.CLIENT)
public class ClientNetworking {
    public static void registerAllOnClient(PlatformNetworking networking) {
        networking.register(S2CEffectSpawn.IDENTIFIER, S2CEffectSpawn.class, S2CEffectSpawn::decode, ClientEffectHandlers::spawnOnClient);
    }
}
