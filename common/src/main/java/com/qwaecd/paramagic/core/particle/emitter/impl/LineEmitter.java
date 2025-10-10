package com.qwaecd.paramagic.core.particle.emitter.impl;

import com.qwaecd.paramagic.core.particle.data.EmissionRequest;
import com.qwaecd.paramagic.core.particle.emitter.Emitter;
import com.qwaecd.paramagic.core.particle.emitter.EmitterBase;
import com.qwaecd.paramagic.core.particle.emitter.prop.EmitterProperty;
import com.qwaecd.paramagic.core.particle.emitter.prop.EmitterType;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import javax.annotation.Nullable;


/**
 * <table border="1" style="width:100%; border-collapse: collapse;">
 *   <caption>LineEmitter 参数映射</caption>
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
 *       <td style="padding: 5px;"><b>param1</b> (线起点)</td>
 *       <td style="padding: 5px;">startPosition.x</td>
 *       <td style="padding: 5px;">startPosition.y</td>
 *       <td style="padding: 5px;">startPosition.z</td>
 *       <td style="padding: 5px;">-</td>
 *     </tr>
 *     <tr>
 *       <td style="padding: 5px;"><b>param2</b> (线终点)</td>
 *       <td style="padding: 5px;">endPosition.x</td>
 *       <td style="padding: 5px;">endPosition.y</td>
 *       <td style="padding: 5px;">endPosition.z</td>
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
 *       <td style="padding: 5px;"><b>param5</b> (速度/Bloom)</td>
 *       <td style="padding: 5px;">baseVelocity.x</td>
 *       <td style="padding: 5px;">baseVelocity.y</td>
 *       <td style="padding: 5px;">baseVelocity.z</td>
 *       <td style="padding: 5px;">bloomIntensity</td>
 *     </tr>
 *   </tbody>
 * </table>
 */
public class LineEmitter extends EmitterBase implements Emitter {

    public final EmitterProperty<Vector3f> startPositionProp;
    public final EmitterProperty<Vector3f> endPositionProp;
    public final EmitterProperty<Vector4f> colorProp;
    public final EmitterProperty<Vector2f> lifetimeRangeProp; // min, max
    public final EmitterProperty<Vector2f> sizeRangeProp;     // min, max
    public final EmitterProperty<Vector3f> baseVelocityProp;
    public final EmitterProperty<Float>    bloomIntensityProp;

    public LineEmitter(Vector3f emitterPosition, float particlesPerSecond) {
        super(EmitterType.LINE, emitterPosition, particlesPerSecond);

        this.minLifetime = 1.0f;
        this.maxLifetime = 3.0f;

//        this.request = new EmissionRequest(
//                0,
//                EmitterType.LINE.ID,
//                -1,
//                new Vector4f(), // param1: 线起点位置 (xyz)
//                new Vector4f(), // param2: 线终点位置 (xyz)
//                new Vector4f(), // param3: 颜色 (rgba)
//                new Vector4f(), // param4: 粒子生命周期(min, max), 尺寸(min, max)
//                new Vector4f()  // param5: 基础速度(xyz), bloom_intensity (w)
//        );

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
    public EmitterType getType() {
        return EmitterType.LINE;
    }
}
