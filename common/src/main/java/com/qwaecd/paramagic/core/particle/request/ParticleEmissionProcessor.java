package com.qwaecd.paramagic.core.particle.request;

import com.qwaecd.paramagic.core.particle.compute.IComputeShaderProvider;
import com.qwaecd.paramagic.core.particle.data.EmissionRequest;
import com.qwaecd.paramagic.core.particle.memory.ParticleMemoryManager;

import java.util.List;


public class ParticleEmissionProcessor {
    private final IComputeShaderProvider shaderProvider;
    public ParticleEmissionProcessor(IComputeShaderProvider shaderProvider) {
        this.shaderProvider = shaderProvider;
    }

    public void reserveParticles(List<EmissionRequest> reqs, ParticleMemoryManager memoryManager) {

    }
}
