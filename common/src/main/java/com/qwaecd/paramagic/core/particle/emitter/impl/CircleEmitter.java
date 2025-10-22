package com.qwaecd.paramagic.core.particle.emitter.impl;

import com.qwaecd.paramagic.core.particle.emitter.Emitter;
import com.qwaecd.paramagic.core.particle.emitter.EmitterBase;
import com.qwaecd.paramagic.core.particle.emitter.EmitterType;
import com.qwaecd.paramagic.core.particle.emitter.property.EmitterProperty;
import com.qwaecd.paramagic.core.particle.emitter.property.type.VelocityModeStates;
import com.qwaecd.paramagic.tools.BitmaskUtils;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static com.qwaecd.paramagic.core.particle.emitter.property.key.AllEmitterProperties.*;

public class CircleEmitter extends EmitterBase implements Emitter {

    public CircleEmitter(Vector3f emitterPosition, float particlesPerSecond) {
        super(EmitterType.CIRCLE, emitterPosition, particlesPerSecond);
        // param1: 发射源位置 (xyz)
        registerProperty(POSITION, new EmitterProperty<>(this.emitterPosition,
                (req, v) -> req.getParam1().set(v.x, v.y, v.z)));
        // param2: 基础速度 (xyz)
        registerProperty(BASE_VELOCITY, new EmitterProperty<>(this.baseVelocity,
                (req, v) -> req.getParam2().set(v.x, v.y, v.z)));
        // param3: 颜色 (rgba)
        registerProperty(COLOR, new EmitterProperty<>(new Vector4f(0.9f, 0.6f, 0.1f, 1.0f),
                (req, v) -> req.getParam3().set(v.x, v.y, v.z, v.w)));
        // param4: 粒子生命周期(min, max)
        registerProperty(LIFE_TIME_RANGE, new EmitterProperty<>(new Vector2f(this.minLifetime, this.maxLifetime),
                (req, v) -> {
                    req.getParam4().x = v.x;
                    req.getParam4().y = v.y;
                }));
        // param4: 尺寸(min, max)
        registerProperty(SIZE_RANGE, new EmitterProperty<>(new Vector2f(0.8f, 1.4f),
                (req, v) -> {
                    req.getParam4().z = v.x;
                    req.getParam4().w = v.y;
                }));
        // param5: 法线(xyz), bloom_intensity (w)
        registerProperty(NORMAL, new EmitterProperty<>(NORMAL.getDefaultValue(),
                (req, v) -> {
                    req.getParam5().x = v.x;
                    req.getParam5().y = v.y;
                    req.getParam5().z = v.z;
                },
                (v) -> {
                    v.normalize();
                    if (v.x == 0.0f && v.y == 0.0f && v.z == 0.0f) {
                        v.y = 1.0f;
                    }
                }));
        // param5: bloom_intensity (w)
        registerProperty(BLOOM_INTENSITY, new EmitterProperty<>(0.0f,
                (req, v) -> req.getParam5().z = v
        ));
        // param6: 圆环内外径(xy)
        registerProperty(INNER_OUTER_RADIUS, new EmitterProperty<>(INNER_OUTER_RADIUS.getDefaultValue(),
                (req, v) -> {
                    Vector4f param6 = req.getParam6();
                    param6.x = v.x;
                    param6.y = v.y;
                }));
        // param1: 发射标识(w)
        registerProperty(VELOCITY_MODE, new EmitterProperty<>(VelocityModeStates.DIRECT,
                (req, v) -> {
                    final int offset = 1;
                    Vector4f param1 = req.getParam1();
                    int currentFlags = Float.floatToIntBits(param1.w);
                    param1.w = Float.intBitsToFloat(
                            BitmaskUtils.setFlag(currentFlags, v.bit << offset)
                    );
                }
        ));
    }

    @Override
    public void moveTo(Vector3f newPos) {
        EmitterProperty<Vector3f> property = this.getProperty(POSITION);
        property.set(newPos);
    }

    @Override
    public EmitterType getType() {
        return EmitterType.CIRCLE;
    }
}
