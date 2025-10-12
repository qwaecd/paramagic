package com.qwaecd.paramagic.core.particle.emitter.impl;

import com.qwaecd.paramagic.core.particle.emitter.Emitter;
import com.qwaecd.paramagic.core.particle.emitter.EmitterBase;
import com.qwaecd.paramagic.core.particle.emitter.prop.EmitterFlags;
import com.qwaecd.paramagic.core.particle.emitter.prop.EmitterProperty;
import com.qwaecd.paramagic.core.particle.emitter.prop.EmitterType;
import com.qwaecd.paramagic.tools.BitmaskUtils;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static com.qwaecd.paramagic.core.particle.emitter.prop.AllEmitterProperties.*;

/**
 * <table border="1" style="width:100%; border-collapse: collapse;">
 *   <caption>SphereEmitter 参数映射</caption>
 *   <thead>
 *     <tr>
 *       <th style="text-align:left; padding: 5px;">参数</th>
 *       <th style="text-align:left; padding: 5px;">X</th>
 *       <th style="text-align:left; padding: 5px;">Y</th>
 *       <th style="text-align:left; padding: 5px;">Z</th>
 *       <th style="text-align:left; padding: 5px;">W</th>
 *     </tr>
 *   </thead>
 *   <tbody>
 *     <tr>
 *       <td style="padding: 5px;"><b>param1</b> (位置/半径)</td>
 *       <td style="padding: 5px;">position.x</td>
 *       <td style="padding: 5px;">position.y</td>
 *       <td style="padding: 5px;">position.z</td>
 *       <td style="padding: 5px;">sphereRadius</td>
 *     </tr>
 *     <tr>
 *       <td style="padding: 5px;"><b>param2</b> (基础速度)</td>
 *       <td style="padding: 5px;">baseVelocity.x</td>
 *       <td style="padding: 5px;">baseVelocity.y</td>
 *       <td style="padding: 5px;">baseVelocity.z</td>
 *       <td style="padding: 5px;">-</td>
 *     </tr>
 *     <tr>
 *       <td style="padding: 5px;"><b>param3</b> (颜色)</td>
 *       <td style="padding: 5px;">color.r</td>
 *       <td style="padding: 5px;">color.g</td>
 *       <td style="padding: 5px;">color.b</td>
 *       <td style="padding: 5px;">color.a</td>
 *     </tr>
 *     <tr>
 *       <td style="padding: 5px;"><b>param4</b> (生命周期/尺寸)</td>
 *       <td style="padding: 5px;">lifetime.min</td>
 *       <td style="padding: 5px;">lifetime.max</td>
 *       <td style="padding: 5px;">size.min</td>
 *       <td style="padding: 5px;">size.max</td>
 *     </tr>
 *     <tr>
 *       <td style="padding: 5px;"><b>param5</b> (速度扩散/Bloom/标识)</td>
 *       <td style="padding: 5px;">velocitySpread</td>
 *       <td style="padding: 5px;">bloomIntensity</td>
 *       <td style="padding: 5px;">flags (e.g., emitFromVolume)</td>
 *       <td style="padding: 5px;">-</td>
 *     </tr>
 *   </tbody>
 * </table>
 */
public class SphereEmitter extends EmitterBase implements Emitter {

    public SphereEmitter(Vector3f emitterPosition, float particlesPerSecond) {
        super(EmitterType.SPHERE, emitterPosition, particlesPerSecond);

        this.minLifetime = 1.0f;
        this.maxLifetime = 5.0f;

//        this.request = new EmissionRequest(
//                0,
//                EmitterType.SPHERE.ID,
//                -1,
//                new Vector4f(), // param1: 发射源位置 (xyz), 球半径(w)
//                new Vector4f(), // param2: 基础速度 (xyz)
//                new Vector4f(), // param3: 颜色 (rgba)
//                new Vector4f(), // param4: 粒子生命周期(min, max), 尺寸(min, max)
//                new Vector4f()  // param5: 发射角度(x), bloom_intensity (y), 发射标识(z)
//        );

        registerProperty(POSITION, new EmitterProperty<>(this.emitterPosition,
                (req, v) -> req.getParam1().set(v.x, v.y, v.z)));
        registerProperty(BASE_VELOCITY, new EmitterProperty<>(this.baseVelocity,
                (req, v) -> req.getParam2().set(v.x, v.y, v.z)));
        registerProperty(SPHERE_RADIUS, new EmitterProperty<>(1.0f,
                (req, v) -> req.getParam1().w = v));
        registerProperty(COLOR, new EmitterProperty<>(new Vector4f(0.9f, 0.6f, 0.1f, 1.0f),
                (req, v) -> req.getParam3().set(v.x, v.y, v.z, v.w)));
        registerProperty(LIFE_TIME_RANGE, new EmitterProperty<>(new Vector2f(this.minLifetime, this.maxLifetime),
                (req, v) -> {
                    req.getParam4().x = v.x;
                    req.getParam4().y = v.y;
                }));
        registerProperty(SIZE_RANGE, new EmitterProperty<>(new Vector2f(0.8f, 1.4f),
                (req, v) -> {
                    req.getParam4().z = v.x;
                    req.getParam4().w = v.y;
                }));
        registerProperty(VELOCITY_SPREAD, new EmitterProperty<>(180.0f,
                (req, v) -> req.getParam5().x = v));
        registerProperty(BLOOM_INTENSITY, new EmitterProperty<>(0.0f,
                (req, v) -> req.getParam5().y = v
        ));
        registerProperty(EMIT_FROM_VOLUME, new EmitterProperty<>(false,
                (req, v) -> {
                    final int offset = 0;
                    Vector4f param5 = req.getParam5();
                    int currentFlags = Float.floatToIntBits(param5.z);
                    param5.z = Float.intBitsToFloat(
                            BitmaskUtils.setFlag(currentFlags, EmitterFlags.EMIT_FROM_VOLUME.get() << offset, v)
                    );
                }
        ));
        registerProperty(VELOCITY_MODE, new EmitterProperty<>(VelocityModeStates.CONE,
                (req, v) -> {
                    final int offset = 1;
                    Vector4f param5 = req.getParam5();
                    int currentFlags = Float.floatToIntBits(param5.z);
                    param5.z = Float.intBitsToFloat(
                            BitmaskUtils.setFlag(currentFlags, v.bit << offset)
                    );
                }
        ));
    }

    @Override
    public void moveTo(Vector3f newPos) {
        EmitterProperty<Vector3f> positionProp = this.getProperty(POSITION);
        positionProp.set(newPos);
    }

    @Override
    public EmitterType getType() {
        return EmitterType.SPHERE;
    }
}
