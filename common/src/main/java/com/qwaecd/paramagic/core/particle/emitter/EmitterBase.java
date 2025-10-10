package com.qwaecd.paramagic.core.particle.emitter;

import com.qwaecd.paramagic.core.particle.data.EmissionRequest;
import com.qwaecd.paramagic.core.particle.emitter.prop.EmitterProperty;
import com.qwaecd.paramagic.core.particle.emitter.prop.EmitterType;
import com.qwaecd.paramagic.core.particle.emitter.prop.ParticleBurst;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public abstract class EmitterBase implements Emitter {
    protected float particlesToEmitAccumulated = 0.0f;

    protected final List<EmitterProperty<?>> properties = new ArrayList<>();

    protected final List<ParticleBurst> bursts = new ArrayList<>();
    protected float emitterAge = 0.0f;
    protected int nextBurstIndex = 0;

    /**
     * The number of particles emitted per second. For one-time bursts, this value is 0.<br>
     * 每秒发射的粒子数，对于一次性发射，其值为 0.0f。
     */
    protected float particlesPerSecond;

    /**
     * 发射器位置
     */
    protected Vector3f emitterPosition;
    // --- 新生粒子初始状态 ---
    /**
     * 新生粒子基础速度方向（单位向量），实际速度会在此基础上有一定随机偏差
     */
    protected Vector3f baseVelocity;

    /**
     * 新生粒子最小生命周期，单位秒
     */
    protected float minLifetime;
    /**
     * 新生粒子最大生命周期，单位秒
     */
    protected float maxLifetime;

    // 用于缓存的发射请求对象，避免每帧都创建新对象
    protected final EmissionRequest request;

    protected EmitterBase(EmitterType emitterType, Vector3f emitterPosition, float particlesPerSecond) {
        this.particlesPerSecond = particlesPerSecond;
        this.emitterPosition = emitterPosition;
        this.baseVelocity = new Vector3f(0.0f, 0.1f, 0.0f);

        this.request = new EmissionRequest(
                0,
                emitterType.ID,
                -1,
                new Vector4f(),
                new Vector4f(),
                new Vector4f(),
                new Vector4f(),
                new Vector4f()
        );
    }

    protected void registerProperty(EmitterProperty<?> property) {
        this.properties.add(property);
    }

    @Override
    public void update(float deltaTime) {
        this.particlesToEmitAccumulated += this.particlesPerSecond * deltaTime;
        this.emitterAge += deltaTime;
        for (EmitterProperty<?> p : this.properties) {
            p.updateRequestIfDirty(this.request);
        }
    }

    @Override
    public @Nullable EmissionRequest getEmissionRequest() {
        int totalParticlesToEmit = 0;

        // 持续生成
        if (this.particlesToEmitAccumulated >= 1.0f) {
            int continuousParticles = (int) this.particlesToEmitAccumulated;
            this.particlesToEmitAccumulated -= continuousParticles;
            totalParticlesToEmit += continuousParticles;
        }

        // 单次爆发
        while (this.nextBurstIndex < bursts.size() && emitterAge >= bursts.get(nextBurstIndex).getTimeInEmitterLife()) {
            totalParticlesToEmit += bursts.get(nextBurstIndex).getCount();
            ++this.nextBurstIndex;
        }

        if (totalParticlesToEmit > 0) {
            this.request.setCount(totalParticlesToEmit);
            return this.request;
        }

        return null;
    }

    public void addBurst(ParticleBurst burst) {
        this.bursts.add(burst);
        this.bursts.sort((b1, b2) -> Float.compare(b1.getTimeInEmitterLife(), b2.getTimeInEmitterLife()));
    }

    /**
     * 清除所有脉冲发射事件。
     */
    public void clearBursts() {
        this.bursts.clear();
        this.nextBurstIndex = 0;
    }
}
