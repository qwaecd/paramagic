package com.qwaecd.paramagic.core.particle.emitter.impl;

import com.qwaecd.paramagic.core.particle.data.EmissionRequest;
import com.qwaecd.paramagic.core.particle.emitter.Emitter;
import com.qwaecd.paramagic.core.particle.emitter.EmitterBase;
import com.qwaecd.paramagic.core.particle.emitter.EmitterType;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class PointEmitter extends EmitterBase implements Emitter {
    private float particlesToEmitAccumulated = 0.0f;
    private final EmissionRequest request;

    public PointEmitter(Vector3f position, float particlesPerSecond) {
        super(position, particlesPerSecond);

        this.baseVelocity = new Vector3f(0.0f, 14.9f, 0.0f);
        this.velocitySpread = 180.0f; // degrees
        this.minLifetime = 1.0f;
        this.maxLifetime = 3.0f;
        this.request = new EmissionRequest(
                0,
                EmitterType.POINT.ID,
                -1,
                new Vector4f(position.x, position.y, position.z, 0.0f), // param1: 发射源位置 (xyz)
                new Vector4f(this.baseVelocity, 0f), // param2: 基础速度 (xyz)
                new Vector4f(0.9f, 0.6f, 0.3f, 0.6f), // param3: 颜色 (rgba)
                new Vector4f(this.minLifetime, this.maxLifetime, 0.8f, 1.4f), // param4: 粒子生命周期(min, max), 尺寸(min, max)
                new Vector4f(velocitySpread, 1.0f, 0, 0)  // param5: (for POINT) 发射角度(x), bloom_intensity (y)
        );
    }

    @Override
    public void update(float deltaTime) {
        this.particlesToEmitAccumulated += this.particlesPerSecond * deltaTime;
        float timeSeconds = (System.currentTimeMillis() & 0x3fffffff) / 1000.0f;
//        this.position.add((float) (Math.cos(timeSeconds) * 0.01f) * 3, 0.0f, (float) (Math.sin(timeSeconds) * 0.01f) * 3);
    }

    @Override
    public @Nullable EmissionRequest getEmissionRequest() {
        if (this.particlesToEmitAccumulated >= 1.0f) {
            int particlesToEmit = (int) this.particlesToEmitAccumulated;
            this.particlesToEmitAccumulated -= particlesToEmit;
            this.request.setCount(particlesToEmit);
            this.setRequestPos(this.position);
            return this.request;
        }
        return null;
    }

    @Override
    public EmitterType getType() {
        return EmitterType.POINT;
    }

    private void setRequestPos(Vector3f v) {
        this.request.getParam1().set(v.x, v.y, v.z);
    }
}
