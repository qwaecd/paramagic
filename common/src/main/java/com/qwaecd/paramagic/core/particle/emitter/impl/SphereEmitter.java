package com.qwaecd.paramagic.core.particle.emitter.impl;

import com.qwaecd.paramagic.core.particle.data.EmissionRequest;
import com.qwaecd.paramagic.core.particle.emitter.*;
import com.qwaecd.paramagic.tools.BitmaskUtils;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import javax.annotation.Nullable;

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
    private float particlesToEmitAccumulated = 0.0f;

    // 缓存 EmissionRequest
    private final EmissionRequest request;

    public final EmitterProperty<Vector3f> positionProp;
    public final EmitterProperty<Float>    sphereRadiusProp; // param1.w
    public final EmitterProperty<Vector3f> baseVelocityProp;
    public final EmitterProperty<Vector4f> colorProp;
    public final EmitterProperty<Vector2f> lifetimeRangeProp; // min, max
    public final EmitterProperty<Vector2f> sizeRangeProp;     // min, max
    public final EmitterProperty<Float>    velocitySpreadProp;
    public final EmitterProperty<Float>    bloomIntensityProp;
    public final EmitterProperty<Boolean>  emitFromVolumeProp;

    public SphereEmitter(Vector3f emitterPosition, float particlesPerSecond) {
        super(emitterPosition, particlesPerSecond);

        this.minLifetime = 1.0f;
        this.maxLifetime = 5.0f;

        this.request = new EmissionRequest(
                0,
                EmitterType.SPHERE.ID,
                -1,
                new Vector4f(), // param1: 发射源位置 (xyz), 球半径(w)
                new Vector4f(), // param2: 基础速度 (xyz)
                new Vector4f(), // param3: 颜色 (rgba)
                new Vector4f(), // param4: 粒子生命周期(min, max), 尺寸(min, max)
                new Vector4f()  // param5: 发射角度(x), bloom_intensity (y), 发射标识(z)
        );

        this.positionProp = new EmitterProperty<>(this.emitterPosition,
                (req, v) -> req.getParam1().set(v.x, v.y, v.z));
        this.sphereRadiusProp = new EmitterProperty<>(1.0f,
                (req, v) -> req.getParam1().w = v);
        this.baseVelocityProp = new EmitterProperty<>(this.baseVelocity,
                (req, v) -> req.getParam2().set(v.x, v.y, v.z));
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
        this.velocitySpreadProp = new EmitterProperty<>(180.0f,
                (req, v) -> req.getParam5().x = v);
        this.bloomIntensityProp = new EmitterProperty<>(0.0f,
                (req, v) -> req.getParam5().y = v
        );
        this.emitFromVolumeProp = new EmitterProperty<>(false,
                (req, v) -> {
                    Vector4f param5 = req.getParam5();
                    int currentFlags = Float.floatToIntBits(param5.z);
                    param5.z = Float.intBitsToFloat(
                            BitmaskUtils.setFlag(currentFlags, EmitterFlags.EMIT_FROM_VOLUME.get(), v)
                    );
                }
        );

        registerProperty(this.positionProp);
        registerProperty(this.baseVelocityProp);
        registerProperty(this.sphereRadiusProp);
        registerProperty(this.colorProp);
        registerProperty(this.lifetimeRangeProp);
        registerProperty(this.sizeRangeProp);
        registerProperty(this.velocitySpreadProp);
        registerProperty(this.bloomIntensityProp);
        registerProperty(this.emitFromVolumeProp);
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
        return EmitterType.SPHERE;
    }
}
