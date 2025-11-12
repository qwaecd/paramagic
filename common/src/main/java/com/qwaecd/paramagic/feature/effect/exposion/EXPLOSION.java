package com.qwaecd.paramagic.feature.effect.exposion;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.assembler.AssemblyException;
import com.qwaecd.paramagic.assembler.ParaComposer;
import com.qwaecd.paramagic.core.particle.builder.PhysicsParamBuilder;
import com.qwaecd.paramagic.core.particle.effect.GPUParticleEffect;
import com.qwaecd.paramagic.core.particle.emitter.Emitter;
import com.qwaecd.paramagic.core.particle.emitter.impl.SphereEmitter;
import com.qwaecd.paramagic.core.particle.emitter.property.type.VelocityModeStates;
import com.qwaecd.paramagic.data.animation.struct.AnimationBindingConfig;
import com.qwaecd.paramagic.data.para.struct.ParaData;
import com.qwaecd.paramagic.feature.circle.MagicCircle;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import static com.qwaecd.paramagic.core.particle.emitter.property.key.AllEmitterProperties.*;

/**
 * 爆裂魔法！小子！
 */
public final class EXPLOSION {
    public static final float EFFECT_TIME = 180.0f;
    private final Random random = new Random();
    private final List<GPUParticleEffect> particleEffects = new ArrayList<>();
    @Getter
    private final MagicCircle magicCircle;


    // 所有 effect 字段
    private GPUParticleEffect centerMahoBallEffect;
    private GPUParticleEffect mahoLineEffect;


    public EXPLOSION(Vector3f emitterCenter, Vector3f eyePosition, Vector3f lookAngle) {
        this.magicCircle = createMagicCircle(emitterCenter, eyePosition, lookAngle);
        initializeParticleEffects(emitterCenter, eyePosition, lookAngle);
    }


    private MagicCircle createMagicCircle(Vector3f emitterCenter, Vector3f eyePosition, Vector3f lookAngle) {
        ParaData paraData = new ParaData(ExplosionParaNode.createParaData(ParaBingingKey.underPlayer));
        MagicCircle circle;
        try {
            ParaComposer composer = ParaComposer.getINSTANCE();
            AnimationBindingConfig bindingConfig = ExplosionParaNode.createAnim(ParaBingingKey.underPlayer);
            circle = composer.assemble(paraData, bindingConfig, null);
        } catch (AssemblyException e) {
            Paramagic.LOG.error("Assembly error: ", e);
            circle = new MagicCircle();
        }
        final float scale = 1.0f;
        circle.getTransform()
                .setPosition(eyePosition.x, eyePosition.y - 1.6f, eyePosition.z)
                .setRotationDegrees(0.0f, 0.0f, 0.0f)
                .setScale(scale);
        return circle;
    }

    private void initializeParticleEffects(Vector3f emitterCenter, Vector3f eyePosition, Vector3f lookAngle) {
        // ----------------------中心法力汇聚球----------------------
        {
            PhysicsParamBuilder physicsParamBuilder = new PhysicsParamBuilder();
            physicsParamBuilder
                    .primaryForceEnabled(true)
                    .primaryForceParam(0.2f, -1.0f)
                    .centerForcePos(emitterCenter)
                    .primaryForceMaxRadius(1000.0f)
                    .linearForceEnabled(false)
                    .linearForce(0.01f, -0.0981f / 1000.0f, 0.0f)
                    .dragCoefficient(1.0f);
            // 在玩家面前的法力汇聚球
            SphereEmitter centerParticleBall = new SphereEmitter(
                    emitterCenter,
                    1000.0f
            );
            centerParticleBall.getProperty(SPHERE_RADIUS).set(0.5f);
            centerParticleBall.getProperty(BASE_VELOCITY).modify(v -> v.set(0.6f, 0.0f, 0.0f));
            centerParticleBall.getProperty(EMIT_FROM_VOLUME).set(true);
            centerParticleBall.getProperty(LIFE_TIME_RANGE).modify(v -> v.set(0.8f, 3.0f));
            centerParticleBall.getProperty(VELOCITY_MODE).set(VelocityModeStates.RANDOM);
            centerParticleBall.getProperty(SIZE_RANGE).modify(v -> v.set(1.0f, 2.0f));
            centerParticleBall.getProperty(COLOR).modify(v -> v.set(
                    0.9f,
                    0.4f,
                    0.5f,
                    1.0f
            ));
            centerParticleBall.getProperty(BLOOM_INTENSITY).set(1.4f);
            // 汇聚法力粒子特效
            GPUParticleEffect centerEffect = new GPUParticleEffect(
                    List.of(centerParticleBall),
                    1_0000,
                    EFFECT_TIME,
                    physicsParamBuilder.build()
            );
            submitEffect(centerEffect, 0);
            this.centerMahoBallEffect = centerEffect;
        }
        // ----------------------四周法力汇聚线----------------------
        {
            PhysicsParamBuilder physicsParamBuilder = new PhysicsParamBuilder();
            physicsParamBuilder
                    .centerForcePos(emitterCenter)
                    .primaryForceEnabled(true).primaryForceParam(10.2f, -1.2f).primaryForceMaxRadius(1000.0f)
                    .secondaryForceEnabled(true).secondaryForceParam(-0.8f, -3.0f).secondaryForceMaxRadius(1000.0f)
                    .linearForceEnabled(true)
                    .linearForce(0.0f, 0.0981f / 1000.0f, 0.0f)
                    .dragCoefficient(1.0f);

            float xOffset = 5.5f;
            float yOffset = 2.5f;
            final float zOffset = 4.5f;
            // A    B
            // C    D
            Vector3f posA = new Vector3f().add(-xOffset*random.nextFloat(0.5f, 1.0f),  yOffset*random.nextFloat(0.5f, 1.0f), 0.0f);
            Vector3f posB = new Vector3f().add( xOffset*random.nextFloat(0.5f, 1.0f),  yOffset*random.nextFloat(0.5f, 1.0f), 0.0f);
            Vector3f posC = new Vector3f().add(-xOffset*random.nextFloat(0.5f, 1.0f), -yOffset*random.nextFloat(0.5f, 1.0f), 0.0f);
            Vector3f posD = new Vector3f().add( xOffset*random.nextFloat(0.5f, 1.0f), -yOffset*random.nextFloat(0.5f, 1.0f), 0.0f);
            List<Vector3f> initialPoints = new ArrayList<>();
            initialPoints.add(posA);
            initialPoints.add(posB);
            initialPoints.add(posC);
            initialPoints.add(posD);
            float targetX = lookAngle.x;
            float targetZ = lookAngle.z;
            float angle = (float) Math.atan2(targetX, targetZ);
            Quaternionf rotation = new Quaternionf().fromAxisAngleRad(new Vector3f(0, 1, 0), angle);
            initialPoints.forEach(v -> {
                v.rotate(rotation);
                v.add(eyePosition);
                v.sub(lookAngle.x * zOffset, lookAngle.y * zOffset, lookAngle.z * zOffset);
            });

            // spawn sphere emitters
            List<Emitter> sphereEmitters = new ArrayList<>();
            initialPoints.forEach(v -> {
                SphereEmitter emitter = new SphereEmitter(
                        v,
                        300.0f
                );
                emitter.getProperty(SPHERE_RADIUS).set(0.2f);
                emitter.getProperty(BASE_VELOCITY).modify(vec -> vec.set(0.6f, 0.0f, 0.0f));
                emitter.getProperty(EMIT_FROM_VOLUME).set(true);
                emitter.getProperty(LIFE_TIME_RANGE).modify(vec -> vec.set(6.0f, 12.5f));
                emitter.getProperty(VELOCITY_MODE).set(VelocityModeStates.RANDOM);
                emitter.getProperty(SIZE_RANGE).modify(vec -> vec.set(1.0f, 3.2f));
                emitter.getProperty(COLOR).modify(vec -> vec.set(
                        0.6f,
                        0.5f,
                        0.7f,
                        1.0f
                ));
                emitter.getProperty(BLOOM_INTENSITY).set(0.3f);
                sphereEmitters.add(emitter);
            });

            Emitter centerBall = new SphereEmitter(
                    emitterCenter,
                    500.0f
            );
            centerBall.getProperty(SPHERE_RADIUS).set(1.5f);
            centerBall.getProperty(BASE_VELOCITY).modify(v -> v.set(0.9f, 0.0f, 0.0f));
            centerBall.getProperty(EMIT_FROM_VOLUME).set(true);
            centerBall.getProperty(LIFE_TIME_RANGE).modify(v -> v.set(10.0f, 13.0f));
            centerBall.getProperty(VELOCITY_MODE).set(VelocityModeStates.RANDOM);
            centerBall.getProperty(SIZE_RANGE).modify(v -> v.set(2.0f, 3.5f));
            centerBall.getProperty(COLOR).modify(v -> v.set(
                    0.4f,
                    0.6f,
                    0.9f,
                    1.0f
            ));
            centerBall.getProperty(BLOOM_INTENSITY).set(0.21f);


            List<Emitter> allEmitters = new ArrayList<>(sphereEmitters);
            allEmitters.add(centerBall);
            GPUParticleEffect mahoLine = new GPUParticleEffect(
                    allEmitters,
                    4_0000,
                    EFFECT_TIME,
                    physicsParamBuilder.build()
            );
            submitEffect(mahoLine, 1);
            this.mahoLineEffect = mahoLine;
        }
    }

    public void modifyProps(Vector3f newEmitterCenter, Vector3f newEyePos) {
        modifyMagicCircleProp();
        onCenterMahoBallEffect(newEmitterCenter);
        onMahoLineEffect();
    }

    public void forEachEffect(Consumer<GPUParticleEffect> consumer) {
        for (GPUParticleEffect effect : this.particleEffects) {
            consumer.accept(effect);
        }
    }

    private void modifyMagicCircleProp() {
        final Vector3f axis = new Vector3f(0.0f, 1.0f, 0.0f);
//        this.magicCircle.getTransform().rotate((float) Math.toRadians(0.5f), axis);
    }

    private void submitEffect(GPUParticleEffect e, int index) {
        this.particleEffects.add(index, e);
    }



    private void onCenterMahoBallEffect(Vector3f newEmitterCenter) {
        this.centerMahoBallEffect.forEachEmitter(emitter -> {
            emitter.moveTo(newEmitterCenter);
            if (emitter.hasProperty(COLOR)) {
                emitter.getProperty(COLOR).modify(v -> {
                    float r = random.nextFloat() * 0.8f + 0.2f;
                    float g = random.nextFloat();
                    float b = random.nextFloat();
                    v.set(r, g, b, 1.0f);
                });
            }
        });
        this.centerMahoBallEffect.getPhysicsParameter().setCFPos(newEmitterCenter);
    }

    private void onMahoLineEffect() {
        this.mahoLineEffect.forEachEmitter(emitter -> {
            if (emitter.hasProperty(COLOR)) {
                emitter.getProperty(COLOR).modify(v -> {
                    float r = random.nextFloat(0.6f, 1.2f);
                    float g = random.nextFloat(0.6f, 0.9f);
                    float b = random.nextFloat(0.8f, 2.4f);
                    v.set(r, g, b, v.w);
                });
            }
        });

        float strength = 5.6f;
        this.mahoLineEffect.getPhysicsParameter().setLinearForce(
                ((random.nextFloat() * 2.0f) - 1.0f) * strength,
                ((random.nextFloat() * 2.0f) - 1.0f) * strength,
                ((random.nextFloat() * 2.0f) - 1.0f) * strength
        );
    }

    @UtilityClass
    private static class ParaBingingKey {
        String underPlayer = "under_player";
    }
}
