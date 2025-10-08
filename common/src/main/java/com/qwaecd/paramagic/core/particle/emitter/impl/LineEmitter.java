package com.qwaecd.paramagic.core.particle.emitter.impl;

import com.qwaecd.paramagic.core.particle.data.EmissionRequest;
import com.qwaecd.paramagic.core.particle.emitter.Emitter;
import com.qwaecd.paramagic.core.particle.emitter.EmitterBase;
import com.qwaecd.paramagic.core.particle.emitter.EmitterProperty;
import com.qwaecd.paramagic.core.particle.emitter.EmitterType;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class LineEmitter extends EmitterBase implements Emitter {
    private float particlesToEmitAccumulated = 0.0f;


    public final EmitterProperty<Vector3f> startPositionProp;
    public final EmitterProperty<Vector3f> endPositionProp;
    public final EmitterProperty<Vector4f> colorProp;
    public final EmitterProperty<Vector2f> lifetimeRangeProp; // min, max
    public final EmitterProperty<Vector2f> sizeRangeProp;     // min, max
    public final EmitterProperty<Vector3f> baseVelocityProp;
    public final EmitterProperty<Float>    bloomIntensityProp;
    // 缓存 EmissionRequest
    private final EmissionRequest request;

    public LineEmitter(Vector3f emitterPosition, float particlesPerSecond) {
        super(emitterPosition, particlesPerSecond);

        this.minLifetime = 1.0f;
        this.maxLifetime = 3.0f;

        this.request = new EmissionRequest(
                0,
                EmitterType.LINE.ID,
                -1,
                new Vector4f(), // param1: 线起点位置 (xyz)
                new Vector4f(), // param2: 线终点位置 (xyz)
                new Vector4f(), // param3: 颜色 (rgba)
                new Vector4f(), // param4: 粒子生命周期(min, max), 尺寸(min, max)
                new Vector4f()  // param5: 基础速度(xyz), bloom_intensity (w)
        );

        this.startPositionProp = new EmitterProperty<>(this.emitterPosition,
                (req, v) -> req.getParam1().set(v.x, v.y, v.z)
        );
        this.endPositionProp = new EmitterProperty<>(new Vector3f(1.0f),
                (req, v) -> req.getParam2().set(v.x, v.y, v.z)
        );
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
        this.baseVelocityProp = new EmitterProperty<>(this.baseVelocity,
                (req, v) -> req.getParam5().set(v.x, v.y, v.z));
        this.bloomIntensityProp = new EmitterProperty<>(0.0f,
                (req, v) -> req.getParam5().w = v
        );

        registerProperty(this.startPositionProp);
        registerProperty(this.endPositionProp);
        registerProperty(this.baseVelocityProp);
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
        return EmitterType.LINE;
    }
}
