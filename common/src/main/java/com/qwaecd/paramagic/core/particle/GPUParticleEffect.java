package com.qwaecd.paramagic.core.particle;

import com.qwaecd.paramagic.core.particle.compute.IComputeShaderProvider;
import com.qwaecd.paramagic.core.particle.data.EmissionRequest;
import com.qwaecd.paramagic.core.particle.emitter.Emitter;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


public class GPUParticleEffect {
    private final List<Emitter> emitters;
    @Getter
    private final int maxParticleCount;
    @Getter
    private final int effectId;

    private final List<EmissionRequest> emissionRequests;

    public GPUParticleEffect(
            List<Emitter> emitters,
            int maxParticleCount,
            int effectId
    ) {
        this.emitters = emitters;
        this.maxParticleCount = maxParticleCount;
        this.effectId = effectId;
        this.emissionRequests = new ArrayList<>(emitters.size());
    }

    public void update(float deltaTime, IComputeShaderProvider shaderProvider) {
        if (!shaderProvider.isSupported()) {
            return;
        }

        for (Emitter e : emitters) {
            e.update(deltaTime, shaderProvider.particleUpdateShader());
        }
    }

    public List<EmissionRequest> getEmissionRequests() {
        this.emissionRequests.clear();
        for (Emitter e : emitters) {
            EmissionRequest req = e.getEmissionRequest();
            if (req != null && req.getCount() > 0) {
                this.emissionRequests.add(req);
            }
        }
        return this.emissionRequests;
    }
}
