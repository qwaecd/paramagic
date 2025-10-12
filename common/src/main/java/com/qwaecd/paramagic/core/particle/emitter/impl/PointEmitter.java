package com.qwaecd.paramagic.core.particle.emitter.impl;

import com.qwaecd.paramagic.core.particle.emitter.Emitter;
import com.qwaecd.paramagic.core.particle.emitter.EmitterBase;
import com.qwaecd.paramagic.core.particle.emitter.prop.EmitterProperty;
import com.qwaecd.paramagic.core.particle.emitter.prop.EmitterType;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static com.qwaecd.paramagic.core.particle.emitter.prop.AllEmitterProperties.*;

/**
 * <table border="1" style="width:100%; border-collapse: collapse;">
 *   <caption>PointEmitter 参数映射</caption>
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
 *       <td style="padding: 5px;"><b>param1</b> (发射器位置)</td>
 *       <td style="padding: 5px;">position.x</td>
 *       <td style="padding: 5px;">position.y</td>
 *       <td style="padding: 5px;">position.z</td>
 *       <td style="padding: 5px;">-</td>
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
 *       <td style="padding: 5px;"><b>param5</b> (速度扩散/光晕)</td>
 *       <td style="padding: 5px;">velocitySpread</td>
 *       <td style="padding: 5px;">bloomIntensity</td>
 *       <td style="padding: 5px;">-</td>
 *       <td style="padding: 5px;">-</td>
 *     </tr>
 *   </tbody>
 * </table>
 */
public class PointEmitter extends EmitterBase implements Emitter {

    public PointEmitter(Vector3f position, float particlesPerSecond) {
        super(EmitterType.POINT, position, particlesPerSecond);

        this.baseVelocity = new Vector3f(0.0f, 4.9f, 0.0f);
        this.minLifetime = 1.0f;
        this.maxLifetime = 5.0f;

//        this.request = new EmissionRequest(
//                0,
//                EmitterType.POINT.ID,
//                -1,
//                new Vector4f(), // param1: 发射源位置 (xyz)
//                new Vector4f(), // param2: 基础速度 (xyz)
//                new Vector4f(), // param3: 颜色 (rgba)
//                new Vector4f(), // param4: 粒子生命周期(min, max), 尺寸(min, max)
//                new Vector4f()  // param5: (for POINT) 发射角度(x), bloom_intensity (y)
//        );

        registerProperty(POSITION, new EmitterProperty<>(this.emitterPosition,
                (req, v) -> req.getParam1().set(v.x, v.y, v.z)));
        registerProperty(BASE_VELOCITY, new EmitterProperty<>(this.baseVelocity,
                (req, v) -> req.getParam2().set(v.x, v.y, v.z)));
        registerProperty(VELOCITY_SPREAD, new EmitterProperty<>(0.0f,
                (req, v) -> req.getParam5().x = v));
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
                (req, v) -> req.getParam5().y = v
                ));
    }

    @Override
    public void moveTo(Vector3f newPos) {
        EmitterProperty<Vector3f> property = this.getProperty(POSITION);
        property.set(newPos);
    }

    @Override
    public EmitterType getType() {
        return EmitterType.POINT;
    }
}
