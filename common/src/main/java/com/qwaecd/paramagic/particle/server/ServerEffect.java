package com.qwaecd.paramagic.particle.server;

import com.qwaecd.paramagic.network.particle.EffectSpawnData;
import lombok.Getter;

public class ServerEffect {
    @Getter
    public final int netId;
    @Getter
    public final EffectSpawnData spawnData;

    public final float maxLifeTime;

    private float age = 0.0f;

    public ServerEffect(int netId, EffectSpawnData spawnData) {
        this.netId = netId;
        this.spawnData = spawnData;
        this.maxLifeTime = spawnData.maxLifeTime;
    }

    public void tick(float deltaTime) {
        this.age += deltaTime;
    }

    public boolean isExpired() {
        return this.age >= this.maxLifeTime;
    }
}
