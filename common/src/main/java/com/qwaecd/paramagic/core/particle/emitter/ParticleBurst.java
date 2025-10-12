package com.qwaecd.paramagic.core.particle.emitter;


import lombok.Getter;

@Getter
public class ParticleBurst {
    /**
     * 在 Emitter 生命周期的哪个时间点触发 (例如 0.0f 表示开始时)。
     */
    private final float timeInEmitterLife;
    /**
     * 触发时一次性发射的粒子数量。
     */
    private final int count;

    public ParticleBurst(float timeInEmitterLife, int count) {
        this.timeInEmitterLife = timeInEmitterLife;
        this.count = count;
    }
}
