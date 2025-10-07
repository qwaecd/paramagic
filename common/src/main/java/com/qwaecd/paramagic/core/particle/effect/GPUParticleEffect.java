package com.qwaecd.paramagic.core.particle.effect;

import com.qwaecd.paramagic.core.particle.data.EffectPhysicsParameter;
import com.qwaecd.paramagic.core.particle.data.EmissionRequest;
import com.qwaecd.paramagic.core.particle.emitter.Emitter;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


public class GPUParticleEffect {
    @Getter
    private final float maxLifeTime;
    @Getter
    private float currentLifeTime = 0.0f;
    private final List<Emitter> emitters;
    @Getter
    private final int maxParticleCount;
    @Getter
    private int effectId = -1;
    @Getter
    private final EffectPhysicsParameter physicsParameter;

    @Getter
    @Setter
    private int effectFlag = EffectFlags.IS_ALIVE.get();

    private final List<EmissionRequest> emissionRequests;

    public GPUParticleEffect(
            List<Emitter> emitters,
            int maxParticleCount,
            float maxLifeTime
    ) {
        this.maxLifeTime = maxLifeTime;
        this.emitters = emitters;
        this.maxParticleCount = maxParticleCount;
        this.emissionRequests = new ArrayList<>(emitters.size());
        this.physicsParameter = new EffectPhysicsParameter();
    }

    public GPUParticleEffect(
            List<Emitter> emitters,
            int maxParticleCount,
            float maxLifeTime,
            EffectPhysicsParameter physicsParameter
    ) {
        this.maxLifeTime = maxLifeTime;
        this.emitters = emitters;
        this.maxParticleCount = maxParticleCount;
        this.emissionRequests = new ArrayList<>(emitters.size());
        this.physicsParameter = physicsParameter;
    }

    public void update(float deltaTime) {
        this.currentLifeTime += deltaTime;
        for (Emitter e : emitters) {
            e.update(deltaTime);
        }
    }

    public List<EmissionRequest> getEmissionRequests() {
        this.emissionRequests.clear();
        for (Emitter e : emitters) {
            EmissionRequest req = e.getEmissionRequest();
            if (req != null && req.getCount() > 0) {
                req.setEffectId(this.effectId);
                this.emissionRequests.add(req);
            }
        }
        return this.emissionRequests;
    }

    public boolean isAlive() {
        return (!EffectFlags.KILL_ALL.is(this.effectId)) && (this.maxLifeTime <= 0.0f || this.currentLifeTime < this.maxLifeTime);
    }

    void setEffectId(int effectId) {
        if (this.effectId != -1) {
            throw new IllegalStateException("Effect ID has already been assigned.");
        }
        this.effectId = effectId;
    }
}
