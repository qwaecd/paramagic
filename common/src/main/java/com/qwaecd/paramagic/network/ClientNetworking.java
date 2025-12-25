package com.qwaecd.paramagic.network;

import com.qwaecd.paramagic.network.api.PlatformNetworking;
import com.qwaecd.paramagic.network.handler.EffectSpawnHandler;
import com.qwaecd.paramagic.network.packet.particle.S2CEffectSpawn;
import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;

@PlatformScope(PlatformScopeType.CLIENT)
public class ClientNetworking {
    public static void registerOnClient(PlatformNetworking networking) {
        networking.register(S2CEffectSpawn.IDENTIFIER, S2CEffectSpawn.class, S2CEffectSpawn::decode, EffectSpawnHandler::handle);
    }
}
