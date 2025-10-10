package com.qwaecd.paramagic.core.particle.emitter.impl;

import com.qwaecd.paramagic.core.particle.emitter.Emitter;
import com.qwaecd.paramagic.core.particle.emitter.EmitterBase;
import com.qwaecd.paramagic.core.particle.emitter.prop.CubeAABB;
import com.qwaecd.paramagic.core.particle.emitter.prop.EmitterFlags;
import com.qwaecd.paramagic.core.particle.emitter.prop.EmitterProperty;
import com.qwaecd.paramagic.core.particle.emitter.prop.EmitterType;
import com.qwaecd.paramagic.tools.BitmaskUtils;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;


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

    public final EmitterProperty<CubeAABB> cubeAABBProp;
    public final EmitterProperty<Vector3f> baseVelocityProp;
    public final EmitterProperty<Vector4f> colorProp;
    public final EmitterProperty<Vector2f> lifetimeRangeProp; // min, max
    public final EmitterProperty<Vector2f> sizeRangeProp;     // min, max
    public final EmitterProperty<Float>    bloomIntensityProp;
    // 发射标识位，包含 是否表面发射、速度模式等
    public final EmitterProperty<Boolean>  emitFromVolumeProp;
    public final EmitterProperty<VelocityModeStates> velocityModeProp;


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

        this.cubeAABBProp = new EmitterProperty<>(new CubeAABB(),
                (req, v) -> {
                    req.getParam1().set(v.getMinPos(), req.getParam1().w);
                    req.getParam2().set(v.getMaxPos(), req.getParam2().w);
                },
                (aabb) -> {
                    // call back to update emitterPosition to the center of AABB
                    aabb.getCenter(this.emitterPosition);
                }
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
        this.emitFromVolumeProp = new EmitterProperty<>(false,
                (req, v) -> {
                    final int offset = 0;
                    Vector4f param1 = req.getParam1();
                    int currentFlags = Float.floatToIntBits(param1.w);
                    param1.w = Float.intBitsToFloat(
                            BitmaskUtils.setFlag(currentFlags, EmitterFlags.EMIT_FROM_VOLUME.get() << offset, v)
                    );
                }
        );
        this.velocityModeProp = new EmitterProperty<>(VelocityModeStates.DIRECT,
                (req, v) -> {
                    final int offset = 1;
                    Vector4f param1 = req.getParam1();
                    int currentFlags = Float.floatToIntBits(param1.w);
                    param1.w = Float.intBitsToFloat(
                            BitmaskUtils.setFlag(currentFlags, v.bit << offset)
                    );
                }
        );

        registerProperty(this.cubeAABBProp);
        registerProperty(this.baseVelocityProp);
        registerProperty(this.colorProp);
        registerProperty(this.lifetimeRangeProp);
        registerProperty(this.sizeRangeProp);
        registerProperty(this.bloomIntensityProp);
        registerProperty(this.emitFromVolumeProp);
        registerProperty(this.velocityModeProp);
    }

    @Override
    public EmitterType getType() {
        return EmitterType.CUBE;
    }
}
