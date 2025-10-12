package com.qwaecd.paramagic.core.particle.emitter.impl;

import com.qwaecd.paramagic.core.particle.emitter.Emitter;
import com.qwaecd.paramagic.core.particle.emitter.EmitterBase;
import com.qwaecd.paramagic.core.particle.emitter.prop.EmitterProperty;
import com.qwaecd.paramagic.core.particle.emitter.prop.EmitterType;
import com.qwaecd.paramagic.tools.BitmaskUtils;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static com.qwaecd.paramagic.core.particle.emitter.prop.AllEmitterProperties.*;


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
 *       <td style="padding: 5px;">flags (velocityModeProp)</td>
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


        // start position
        registerProperty(POSITION, new EmitterProperty<>(this.emitterPosition,
                (req, v) -> req.getParam1().set(v.x, v.y, v.z)
                ));
        // end position
        registerProperty(END_POSITION, new EmitterProperty<>(new Vector3f(1.0f),
                (req, v) -> req.getParam2().set(v.x, v.y, v.z)
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
        EmitterProperty<Vector3f> startPositionProp = this.getProperty(POSITION);
        EmitterProperty<Vector3f> endPositionProp = this.getProperty(END_POSITION);
        startPositionProp.modify(v -> v.set(newPos));
        endPositionProp.modify(v -> {
            float newX = (v.x - startPositionProp.get().x) + newPos.x;
            float newY = (v.y - startPositionProp.get().y) + newPos.y;
            float newZ = (v.z - startPositionProp.get().z) + newPos.z;
            v.set(newX, newY, newZ);
        });
    }

    @Override
    public EmitterType getType() {
        return EmitterType.LINE;
    }
}
