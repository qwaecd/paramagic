package com.qwaecd.paramagic.network.handler;

import com.qwaecd.paramagic.network.api.NetworkContext;
import com.qwaecd.paramagic.network.packet.particle.S2CEffectSpawn;
import com.qwaecd.paramagic.network.particle.EffectSpawnData;
import com.qwaecd.paramagic.particle.client.ClientEffectRepository;
import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;

@PlatformScope(PlatformScopeType.CLIENT)
public class ClientEffectHandlers {
    public static void spawnOnClient(S2CEffectSpawn packet, NetworkContext context) {
        EffectSpawnData spawnData = packet.getSpawnData();
        ClientEffectRepository.getInstance().spawnEffect(spawnData);
    }
}

