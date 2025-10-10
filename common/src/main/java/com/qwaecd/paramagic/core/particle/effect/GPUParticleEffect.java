package com.qwaecd.paramagic.core.particle.effect;

import com.qwaecd.paramagic.core.particle.data.EffectPhysicsParameter;
import com.qwaecd.paramagic.core.particle.data.EmissionRequest;
import com.qwaecd.paramagic.core.particle.emitter.Emitter;
import com.qwaecd.paramagic.core.particle.emitter.prop.ParticleBurst;
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
    private int effectId = -1; // 效果的唯一 ID（非位掩码）
    @Getter
    private final EffectPhysicsParameter physicsParameter;

    @Getter
    @Setter
    private int effectFlag = EffectFlags.IS_ALIVE.get(); // 效果的状态标志位掩码（bitmask）

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

    /**
     * 判断粒子效果当前是否处于存活状态。<br>
     * 规则：当未设置 KILL_ALL 标志，且寿命未超出或寿命无限（maxLifeTime <= 0），则认为存活。<br>
     * 注意：in() 的入参是位掩码，应传入 {@link #effectFlag}，而不是 {@link #effectId}。
     */
    public boolean isAlive() {
        return (!EffectFlags.KILL_ALL.in(this.effectFlag))
                && (this.maxLifeTime <= 0.0f || this.currentLifeTime < this.maxLifeTime);
    }

    final void setEffectId(int effectId) {
        if (this.effectId != -1) {
            throw new IllegalStateException("Effect ID has already been assigned.");
        }
        this.effectId = effectId;
    }
}
