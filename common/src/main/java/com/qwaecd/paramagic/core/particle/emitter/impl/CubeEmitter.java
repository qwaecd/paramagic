package com.qwaecd.paramagic.core.particle.emitter.impl;

import com.qwaecd.paramagic.core.particle.emitter.Emitter;
import com.qwaecd.paramagic.core.particle.emitter.EmitterBase;
import com.qwaecd.paramagic.core.particle.emitter.EmitterType;
import com.qwaecd.paramagic.core.particle.emitter.property.EmitterProperty;
import com.qwaecd.paramagic.core.particle.emitter.property.type.CubeAABB;
import com.qwaecd.paramagic.core.particle.emitter.property.type.EmitterFlags;
import com.qwaecd.paramagic.core.particle.emitter.property.type.VelocityModeStates;
import com.qwaecd.paramagic.tools.BitmaskUtils;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static com.qwaecd.paramagic.core.particle.emitter.property.key.AllEmitterProperties.*;

/**
 * <table border="1" style="width:100%; border-collapse: collapse;">
 *   <caption>CubeEmitter 参数映射</caption>
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
 *       <td style="padding: 5px;"><b>param1</b> (最小AABB/发射标识)</td>
 *       <td style="padding: 5px;">minAABB.x</td>
 *       <td style="padding: 5px;">minAABB.y</td>
 *       <td style="padding: 5px;">minAABB.z</td>
 *       <td style="padding: 5px;">flags (e.g., emitFromVolume)</td>
 *     </tr>
 *     <tr>
 *       <td style="padding: 5px;"><b>param2</b> (最大AABB)</td>
 *       <td style="padding: 5px;">maxAABB.x</td>
 *       <td style="padding: 5px;">maxAABB.y</td>
 *       <td style="padding: 5px;">maxAABB.z</td>
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
 *       <td style="padding: 5px;"><b>param5</b> (基础速度/光晕)</td>
 *       <td style="padding: 5px;">baseVelocity.x</td>
 *       <td style="padding: 5px;">baseVelocity.y</td>
 *       <td style="padding: 5px;">baseVelocity.z</td>
 *       <td style="padding: 5px;">bloomIntensity</td>
 *     </tr>
 *   </tbody>
 * </table>
 */
public class CubeEmitter extends EmitterBase implements Emitter {

    public CubeEmitter(Vector3f emitterPosition, float particlesPerSecond) {
        super(EmitterType.CUBE, emitterPosition, particlesPerSecond);

        this.minLifetime = 1.0f;
        this.maxLifetime = 5.0f;

//        this.request = new EmissionRequest(
//                0,
//                EmitterType.CUBE.ID,
//                -1,
//                new Vector4f(), // param1: minAABB (xyz), 发射标识(w)
//                new Vector4f(), // param2: maxAABB (xyz)
//                new Vector4f(), // param3: 颜色 (rgba)
//                new Vector4f(), // param4: 粒子生命周期(min, max), 尺寸(min, max)
//                new Vector4f()  // param5: 基础速度(xyz), bloom_intensity (w)
//        );

        registerProperty(CUBE_AABB, new EmitterProperty<>(new CubeAABB(),
                (req, v) -> {
                    req.getParam1().set(v.getMinPos(), req.getParam1().w);
                    req.getParam2().set(v.getMaxPos(), req.getParam2().w);
                },
                (aabb) -> {
                    // call back to update emitterPosition to the center of AABB
                    aabb.getCenter(this.emitterPosition);
                }
        ));
        registerProperty(BASE_VELOCITY, new EmitterProperty<>(this.baseVelocity,
                (req, v) -> req.getParam5().set(v.x, v.y, v.z)));
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
        registerProperty(BLOOM_INTENSITY, new EmitterProperty<>(0.0f,
                (req, v) -> req.getParam5().w = v
        ));
        registerProperty(EMIT_FROM_VOLUME, new EmitterProperty<>(false,
                (req, v) -> {
                    final int offset = 0;
                    Vector4f param1 = req.getParam1();
                    int currentFlags = Float.floatToIntBits(param1.w);
                    param1.w = Float.intBitsToFloat(
                            BitmaskUtils.setFlag(currentFlags, EmitterFlags.EMIT_FROM_VOLUME.get() << offset, v)
                    );
                }
        ));
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
        EmitterProperty<CubeAABB> cubeAABBProp = this.getProperty(CUBE_AABB);
        cubeAABBProp.modify(v -> v.moveCenterTo(newPos));
    }

    @Override
    public EmitterType getType() {
        return EmitterType.CUBE;
    }
}
