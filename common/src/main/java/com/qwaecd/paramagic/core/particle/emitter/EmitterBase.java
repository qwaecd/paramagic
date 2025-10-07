package com.qwaecd.paramagic.core.particle.emitter;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public abstract class EmitterBase implements Emitter {
    protected final List<EmitterProperty<?>> properties = new ArrayList<>();
    /**
     * 发射器是否已经停止发射新粒子
     */
    protected boolean finished = false;
    /**
     * 发射器停止发射新粒子后的经过时间，单位秒
     */
    protected float timeSinceFinished = 0.0f;

    /**
     * 每秒发射的粒子数，对于一次性发射，其值为 0
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
     * 新生粒子速度偏差角度范围（与baseVelocity的夹角），单位度，范围0到180度
     */
    protected float velocitySpread; // 0 to 180 degrees
    /**
     * 新生粒子最小生命周期，单位秒
     */
    protected float minLifetime;
    /**
     * 新生粒子最大生命周期，单位秒
     */
    protected float maxLifetime;

    protected EmitterBase(Vector3f emitterPosition, float particlesPerSecond) {
        this.particlesPerSecond = particlesPerSecond;
        this.emitterPosition = emitterPosition;
        this.baseVelocity = new Vector3f(0.0f, 0.1f, 0.0f);
    }

    protected void registerProperty(EmitterProperty<?> property) {
        this.properties.add(property);
    }
}
