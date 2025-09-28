package com.qwaecd.paramagic.core.particle;

import com.qwaecd.paramagic.core.particle.compute.ComputeShaderProvider;
import com.qwaecd.paramagic.core.particle.emitter.Emitter;
import lombok.Getter;

import java.util.List;


public class GPUParticleEffect {
    private final List<Emitter> emitter;
    @Getter
    private final int maxParticleCount;

    public GPUParticleEffect(
            List<Emitter> emitter,
            int maxParticleCount
    ) {
        this.emitter = emitter;
        this.maxParticleCount = maxParticleCount;
    }

    public void update(float deltaTime, ComputeShaderProvider shaderProvider) {
        for (Emitter e : emitter) {
            e.update(deltaTime, shaderProvider.getInitializeRequestShader());
        }
    }
}
