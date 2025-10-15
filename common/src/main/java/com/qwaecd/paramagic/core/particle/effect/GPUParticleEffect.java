package com.qwaecd.paramagic.core.particle.effect;

import com.qwaecd.paramagic.core.particle.data.EffectPhysicsParameter;
import com.qwaecd.paramagic.core.particle.data.EmissionRequest;
import com.qwaecd.paramagic.core.particle.emitter.Emitter;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


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

    /**
     * Call this function only within the rendering loop, not in the game logic loop.<br>
     * 仅在渲染循环内调用该函数，不要在游戏逻辑循环内调用。<br>
     * 更新粒子效果的状态，包括其所有发射器的状态。<br>
     * @param deltaTime Seconds of time increment (time since last frame).<br>
     * 时间增量，单位秒（为距离上一帧的时间）<br>
     */
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

    public void forEachEmitter(Consumer<Emitter> action) {
        for (Emitter e : emitters) {
            action.accept(e);
        }
    }

    final void setEffectId(int effectId) {
        if (this.effectId != -1) {
            throw new IllegalStateException("Effect ID has already been assigned.");
        }
        this.effectId = effectId;
    }
}
