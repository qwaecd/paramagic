package com.qwaecd.paramagic.core.particle.effect;

import com.qwaecd.paramagic.core.particle.data.EffectPhysicsParameter;
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
    private int effectId = -1;
    @Getter
    private final EffectPhysicsParameter physicsParameter;

    private final List<EmissionRequest> emissionRequests;

    public GPUParticleEffect(
            List<Emitter> emitters,
            int maxParticleCount
    ) {
        this.emitters = emitters;
        this.maxParticleCount = maxParticleCount;
        this.emissionRequests = new ArrayList<>(emitters.size());
        this.physicsParameter = new EffectPhysicsParameter();
    }

    public GPUParticleEffect(
            List<Emitter> emitters,
            int maxParticleCount,
            EffectPhysicsParameter physicsParameter
    ) {
        this.emitters = emitters;
        this.maxParticleCount = maxParticleCount;
        this.emissionRequests = new ArrayList<>(emitters.size());
        this.physicsParameter = physicsParameter;
    }

    public void update(float deltaTime) {
        for (Emitter e : emitters) {
            e.update(deltaTime);
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

    void setEffectId(int effectId) {
        if (this.effectId != -1) {
            throw new IllegalStateException("Effect ID has already been assigned.");
        }
        this.effectId = effectId;
    }
}
