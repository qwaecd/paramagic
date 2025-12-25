package com.qwaecd.paramagic.particle.client;

import com.qwaecd.paramagic.core.particle.ParticleSystem;
import com.qwaecd.paramagic.core.particle.effect.GPUParticleEffect;
import lombok.Getter;

public class ClientEffect implements AutoCloseable {
    @Getter
    public final int netId;
    private final GPUParticleEffect particleEffect;

    public ClientEffect(int netId, GPUParticleEffect particleEffect) {
        this.netId = netId;
        this.particleEffect = particleEffect;
    }

    @Override
    public void close() {
        ParticleSystem.getInstance().removeEffect(this.particleEffect);
    }
}
