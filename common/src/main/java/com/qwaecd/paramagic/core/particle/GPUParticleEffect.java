package com.qwaecd.paramagic.core.particle;

import com.qwaecd.paramagic.core.particle.renderer.ParticleRendererType;


public class GPUParticleEffect {
    private final int maxParticles;

    private final ParticleRendererType rendererType;

    public GPUParticleEffect(int maxParticles, ParticleRendererType rendererType) {
        this.maxParticles = maxParticles;
        this.rendererType = rendererType;
    }
}
