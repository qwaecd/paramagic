package com.qwaecd.paramagic.particle.server;

import com.qwaecd.paramagic.network.particle.EffectSpawnData;
import lombok.Getter;

public class ServerEffect {
    @Getter
    public final int netId;
    @Getter
    public final EffectSpawnData spawnData;

    public ServerEffect(int netId, EffectSpawnData spawnData) {
        this.netId = netId;
        this.spawnData = spawnData;
    }
}
