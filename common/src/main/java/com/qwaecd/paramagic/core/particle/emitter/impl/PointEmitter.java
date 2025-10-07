package com.qwaecd.paramagic.core.particle.emitter.impl;

import com.qwaecd.paramagic.core.particle.data.EmissionRequest;
import com.qwaecd.paramagic.core.particle.emitter.Emitter;
import com.qwaecd.paramagic.core.particle.emitter.EmitterBase;
import com.qwaecd.paramagic.core.particle.emitter.EmitterProperty;
import com.qwaecd.paramagic.core.particle.emitter.EmitterType;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import javax.annotation.Nullable;

public class PointEmitter extends EmitterBase implements Emitter {
    private float particlesToEmitAccumulated = 0.0f;

    // 缓存 EmissionRequest
    private final EmissionRequest request;

    public final EmitterProperty<Vector3f> positionProp;
    public final EmitterProperty<Vector3f> baseVelocityProp;
    public final EmitterProperty<Float>    velocitySpreadProp;
    public final EmitterProperty<Vector4f> colorProp;
    public final EmitterProperty<Vector2f> lifetimeRangeProp; // min, max
    public final EmitterProperty<Vector2f> sizeRangeProp;     // min, max
    public final EmitterProperty<Float>    bloomIntensityProp;

    public PointEmitter(Vector3f position, float particlesPerSecond) {
        super(position, particlesPerSecond);

        this.baseVelocity = new Vector3f(0.0f, 4.9f, 0.0f);
        this.velocitySpread = 180.0f; // degrees
        this.minLifetime = 1.0f;
        this.maxLifetime = 5.0f;

        this.request = new EmissionRequest(
                0,
                EmitterType.POINT.ID,
                -1,
                new Vector4f(), // param1: 发射源位置 (xyz)
                new Vector4f(), // param2: 基础速度 (xyz)
                new Vector4f(), // param3: 颜色 (rgba)
                new Vector4f(), // param4: 粒子生命周期(min, max), 尺寸(min, max)
                new Vector4f()  // param5: (for POINT) 发射角度(x), bloom_intensity (y)
        );

        this.positionProp = new EmitterProperty<>(this.emitterPosition,
                (req, v) -> req.getParam1().set(v.x, v.y, v.z));
        this.baseVelocityProp = new EmitterProperty<>(this.baseVelocity,
                (req, v) -> req.getParam2().set(v.x, v.y, v.z));
        this.velocitySpreadProp = new EmitterProperty<>(this.velocitySpread,
                (req, v) -> req.getParam5().x = v);
        this.colorProp = new EmitterProperty<>(new Vector4f(0.9f, 0.6f, 0.1f, 1.0f),
                (req, v) -> req.getParam3().set(v.x, v.y, v.z, v.w));
        this.lifetimeRangeProp = new EmitterProperty<>(new Vector2f(this.minLifetime, this.maxLifetime),
                (req, v) -> {
            req.getParam4().x = v.x;
            req.getParam4().y = v.y;
        });
        this.sizeRangeProp = new EmitterProperty<>(new Vector2f(0.8f, 1.4f),
                (req, v) -> {
            req.getParam4().z = v.x;
            req.getParam4().w = v.y;
        });
        this.bloomIntensityProp = new EmitterProperty<>(0.9f,
                (req, v) -> req.getParam5().y = v
        );

        registerProperty(this.positionProp);
        registerProperty(this.baseVelocityProp);
        registerProperty(this.velocitySpreadProp);
        registerProperty(this.colorProp);
        registerProperty(this.lifetimeRangeProp);
        registerProperty(this.sizeRangeProp);
        registerProperty(this.bloomIntensityProp);
    }

    @Override
    public void update(float deltaTime) {
        this.particlesToEmitAccumulated += this.particlesPerSecond * deltaTime;
        for (EmitterProperty<?> p : this.properties) {
            p.updateRequestIfDirty(this.request);
        }
    }

    @Override
    public @Nullable EmissionRequest getEmissionRequest() {
        if (this.particlesToEmitAccumulated >= 1.0f) {
            int particlesToEmit = (int) this.particlesToEmitAccumulated;
            this.particlesToEmitAccumulated -= particlesToEmit;
            this.request.setCount(particlesToEmit);
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
