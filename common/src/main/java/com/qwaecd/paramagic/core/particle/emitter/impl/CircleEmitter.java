package com.qwaecd.paramagic.core.particle.emitter.impl;

import com.qwaecd.paramagic.core.particle.emitter.Emitter;
import com.qwaecd.paramagic.core.particle.emitter.EmitterBase;
import com.qwaecd.paramagic.core.particle.emitter.EmitterType;
import com.qwaecd.paramagic.core.particle.emitter.property.EmitterProperty;
import com.qwaecd.paramagic.core.particle.emitter.property.type.ParticleShapeFlags;
import com.qwaecd.paramagic.core.particle.emitter.property.type.VelocityModeStates;
import com.qwaecd.paramagic.tools.BitmaskUtils;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static com.qwaecd.paramagic.core.particle.emitter.property.key.AllEmitterProperties.*;

/**
 * <table border="1" style="width:100%; border-collapse: collapse;">
 *   <caption>CircleEmitter 参数映射（effect-local）</caption>
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
 *       <td style="padding: 5px;"><b>param1</b> (局部位置/标识)</td>
 *       <td style="padding: 5px;">position.x</td>
 *       <td style="padding: 5px;">position.y</td>
 *       <td style="padding: 5px;">position.z</td>
 *       <td style="padding: 5px;">flags (velocityMode)</td>
 *     </tr>
 *     <tr>
 *       <td style="padding: 5px;"><b>param2</b> (局部基础速度)</td>
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
 *       <td style="padding: 5px;"><b>param5</b> (局部法线/Bloom)</td>
 *       <td style="padding: 5px;">normal.x</td>
 *       <td style="padding: 5px;">normal.y</td>
 *       <td style="padding: 5px;">normal.z</td>
 *       <td style="padding: 5px;">bloomIntensity</td>
 *     </tr>
 *     <tr>
 *       <td style="padding: 5px;"><b>param6</b> (内外半径)</td>
 *       <td style="padding: 5px;">innerRadius</td>
 *       <td style="padding: 5px;">outerRadius</td>
 *       <td style="padding: 5px;">-</td>
 *       <td style="padding: 5px;">-</td>
 *     </tr>
 *   </tbody>
 * </table>
 */
public class CircleEmitter extends EmitterBase implements Emitter {

    public CircleEmitter(Vector3f emitterPosition, float particlesPerSecond) {
        super(EmitterType.CIRCLE, emitterPosition, particlesPerSecond);
        registerProperty(POSITION, new EmitterProperty<>(this.emitterPosition,
                (req, v) -> req.getParam1().set(v.x, v.y, v.z)));
        registerProperty(BASE_VELOCITY, new EmitterProperty<>(this.baseVelocity,
                (req, v) -> req.getParam2().set(v.x, v.y, v.z)));
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
        registerProperty(NORMAL, new EmitterProperty<>(new Vector3f(0.0f, 1.0f, 0.0f),
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
        registerProperty(BLOOM_INTENSITY, new EmitterProperty<>(0.0f,
                (req, v) -> req.getParam5().w = v
        ));
        registerProperty(INNER_OUTER_RADIUS, new EmitterProperty<>(new Vector2f(0.1f, 0.5f),
                (req, v) -> {
                    Vector4f param6 = req.getParam6();
                    param6.x = v.x;
                    param6.y = v.y;
                }));
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
        registerProperty(PARTICLE_SHAPE_FLAGS, new EmitterProperty<>(ParticleShapeFlags.FIXED,
                (req, v) -> {
                    // param1.w bits: [5:4]=shapeMode, [3:1]=velocityMode
                    Vector4f param1 = req.getParam1();
                    int currentFlags = Float.floatToIntBits(param1.w);
                    int nextFlags = BitmaskUtils.clearFlag(currentFlags, ParticleShapeFlags.REQUEST_MASK);
                    param1.w = Float.intBitsToFloat(v.applyToRequest(nextFlags));
                }));
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
