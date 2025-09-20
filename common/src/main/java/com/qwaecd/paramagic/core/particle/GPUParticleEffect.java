package com.qwaecd.paramagic.core.particle;

import com.qwaecd.paramagic.core.particle.renderer.ParticleRendererType;


public class GPUParticleEffect {
    private final int effectParticles;

    private final ParticleRendererType rendererType;

    public GPUParticleEffect(int effectParticles, ParticleRendererType rendererType) {
        this.effectParticles = effectParticles;
        this.rendererType = rendererType;
    }
}
