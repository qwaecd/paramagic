package com.qwaecd.paramagic.debug;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.assembler.AssemblyException;
import com.qwaecd.paramagic.assembler.ParaComposer;
import com.qwaecd.paramagic.client.material.EnergyFlowMaterial;
import com.qwaecd.paramagic.client.obj.laser.EnergyCoreSphere;
import com.qwaecd.paramagic.client.obj.sun.Sun;
import com.qwaecd.paramagic.client.renderbase.SharedMeshes;
import com.qwaecd.paramagic.client.renderbase.Sphere;
import com.qwaecd.paramagic.core.particle.ParticleSystem;
import com.qwaecd.paramagic.core.particle.builder.PhysicsParamBuilder;
import com.qwaecd.paramagic.core.particle.effect.GPUParticleEffect;
import com.qwaecd.paramagic.core.particle.emitter.ParticleBurst;
import com.qwaecd.paramagic.core.particle.emitter.impl.*;
import com.qwaecd.paramagic.core.particle.emitter.property.type.ParticlePrimitiveTypeStates;
import com.qwaecd.paramagic.core.particle.emitter.property.type.VelocityModeStates;
import com.qwaecd.paramagic.core.render.ModRenderSystem;
import com.qwaecd.paramagic.core.render.Transform;
import com.qwaecd.paramagic.core.render.api.IRenderable;
import com.qwaecd.paramagic.core.render.geometricmask.DistortionGeometricMaskEffect;
import com.qwaecd.paramagic.core.render.geometricmask.GeometricEffectCaster;
import com.qwaecd.paramagic.core.render.shader.ShaderManager;
import com.qwaecd.paramagic.core.render.texture.Material;
import com.qwaecd.paramagic.data.animation.property.AllAnimatableProperties;
import com.qwaecd.paramagic.data.animation.struct.AnimationBinding;
import com.qwaecd.paramagic.data.animation.struct.AnimationBindingConfig;
import com.qwaecd.paramagic.data.animation.struct.AnimatorData;
import com.qwaecd.paramagic.data.animation.struct.track.KeyframeData;
import com.qwaecd.paramagic.data.animation.struct.track.KeyframeTrackData;
import com.qwaecd.paramagic.data.animation.struct.track.TrackData;
import com.qwaecd.paramagic.data.para.converter.ConversionException;
import com.qwaecd.paramagic.data.para.struct.ParaData;
import com.qwaecd.paramagic.data.para.struct.components.CurvyStarParaData;
import com.qwaecd.paramagic.data.para.struct.components.PolygonParaData;
import com.qwaecd.paramagic.data.para.struct.components.RingParaData;
import com.qwaecd.paramagic.data.para.struct.components.VoidParaData;
import com.qwaecd.paramagic.feature.circle.MagicCircle;
import com.qwaecd.paramagic.feature.circle.MagicCircleManager;
import lombok.experimental.UtilityClass;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static com.qwaecd.paramagic.core.particle.emitter.property.key.AllEmitterProperties.*;

@UtilityClass
public class DebugTools {
    private static final List<GPUParticleEffect> testEffects = new ArrayList<>();
    public static void test() {
        IRenderable ball;
        Material material = new Material(ShaderManager.getInstance().getBaseBallInShader());
        ball = new Sphere(material);
        ball.getTransform().getModelMatrix().translate(0, 100, 0).scale(4.0f, 4.0f, 4.0f);
//        ModRenderSystem.getInstance().addRenderable(ball);

//        spawnDebugLaserSphere();

        IRenderable sun = new Sun(ShaderManager.getInstance().getShader("sun"));
        sun.getTransform().setPosition(0, 80, 10).setScale(5.0f, 5.0f, 5.0f);
//        ModRenderSystem.getInstance().addRenderable(sun);

//        spawnDebugBlackHole();
//        testGpuMagicCircleEffect();
//        paraTest();
        effectTest();
    }

    private static void spawnDebugLaserSphere() {
        Vector3f center = new Vector3f(0.0f, 0.0f, 0.0f);
        {
            EnergyFlowMaterial energyFlowMaterial = new EnergyFlowMaterial()
                    .setColor(1.35f, 0.75f, 1.0f)
                    .setAlpha(0.85f)
                    .setEmissiveIntensity(0.4f)
                    .setFlowSpeed(0.3f, -1.1f)
                    .setNoiseSpeed(0.08f, -0.05f)
                    .setNoiseScale(2.8f)
                    .setNoiseStrength(1.0f)
                    .setThreshold(0.24f)
                    .setSoftness(0.28f);
            EnergyCoreSphere energyCoreSphere = new EnergyCoreSphere(energyFlowMaterial)
                    .setSphere(center, 6.0f);
            ModRenderSystem.getInstance().addRenderable(energyCoreSphere);
        }
        {
            EnergyFlowMaterial energyFlowMaterial = new EnergyFlowMaterial(EnergyFlowMaterial.DEFAULT_NOISE_TEXTURE, EnergyFlowMaterial.DEFAULT_NOISE_TEXTURE)
                    .setColor(1.8f, 0.55f, 0.35f)
                    .setAlpha(0.95f)
                    .setEmissiveIntensity(1.2f)
                    .setFlowSpeed(0.35f, -0.03f)
                    .setNoiseSpeed(0.12f, -0.65f)
                    .setNoiseScale(1.4f)
                    .setNoiseStrength(1.0f)
                    .setThreshold(0.18f)
                    .setSoftness(0.22f);
            EnergyCoreSphere energyCoreSphere = new EnergyCoreSphere(energyFlowMaterial)
                    .setSphere(center, 5.35f);
            ModRenderSystem.getInstance().addRenderable(energyCoreSphere);
        }
    }

    private static void testGpuMagicCircleEffect() {
        GPUParticleEffect effect = DebugGpuMagicCircleEffect.create(new Vector3f(0.0f, 110.05f, 0.0f));
        if (ParticleSystem.getInstance().spawnEffect(effect)) {
            testEffects.add(effect);
        }
    }

    private static void spawnDebugBlackHole() {
        {
            DistortionGeometricMaskEffect effect = new DistortionGeometricMaskEffect()
                    .setDistortionStrength(0.05f)
                    .setInnerRadius(0.018f)
                    .setOuterRadius(2.0f)
                    .setMaxOffset(0.4f);
            Transform transform = new Transform();
            float r = 16.0f;
            transform.setPosition(15.0f, -30.0f, 0.0f).setScale(r, r, r);
            GeometricEffectCaster caster = new GeometricEffectCaster(
                    SharedMeshes.sphere(),
                    transform,
                    effect
            );
            ModRenderSystem.getInstance().addGeometricEffectCaster(caster);
        }
        {
            DistortionGeometricMaskEffect effect = new DistortionGeometricMaskEffect()
                    .setDistortionStrength(0.014f)
                    .setInnerRadius(0.018f)
                    .setOuterRadius(2.0f)
                    .setMaxOffset(0.1f);
            Transform transform = new Transform();
            float r = 4.0f;
            transform.setPosition(0.0f, -30.0f, 0.0f).setScale(r, r, r);
            GeometricEffectCaster caster = new GeometricEffectCaster(
                    SharedMeshes.sphere(),
                    transform,
                    effect
            );
            ModRenderSystem.getInstance().addGeometricEffectCaster(caster);
        }
    }

    private static void effectTest() {
        PhysicsParamBuilder physicsParamBuilder = new PhysicsParamBuilder();
        Vector3f centerForcePos = new Vector3f(20.0f, 115.0f, 2.0f);

        physicsParamBuilder
                .centerForcePos(centerForcePos).primaryForceEnabled(false).secondaryForceEnabled(false).sinusoidalForceEnabled(false)
                .primaryForceParam(4.6f, -2.0f).primaryForceMaxRadius(1000.0f)
                .secondaryForceParam(-2.6f, -4.0f).secondaryForceMaxRadius(1000.0f)
                .sinusoidalForceParam(40.0f, 1.0f, -2.0f).sinusoidalExtraParam(0.0f).sinusoidalForceMaxRadius(10000.0f)
                .linearForceEnabled(false)
                .linearForce(0.01f, -0.0981f / 1000.0f, 0.0f)
                .dragCoefficient(1.6f);

        // circle emitter
        CircleEmitter circleEmitter = new CircleEmitter(
                centerForcePos,
                0.0f
        );
        circleEmitter.modifyProp(BASE_VELOCITY, v -> v.set(4.0f, 0.0f, 0.0f));
        circleEmitter.modifyProp(LIFE_TIME_RANGE, v -> v.set(0.2f, 1.6f));
        circleEmitter.modifyProp(COLOR, v -> v.set(1.0f, 0.0f, 0.4f, 1.0f));
        circleEmitter.modifyProp(SIZE_RANGE, v -> v.set(0.011f, 0.023f));
        circleEmitter.trySet(BLOOM_INTENSITY, 1.0f);
        circleEmitter.trySet(VELOCITY_MODE, VelocityModeStates.RADIAL_FROM_CENTER);
        circleEmitter.modifyProp(NORMAL, v -> v.set(0.0f, 1.0f, 0.0f));
        circleEmitter.modifyProp(INNER_OUTER_RADIUS, v -> v.set(0.4f, 0.5f));

        LineEmitter lineEmitter = new LineEmitter(new Vector3f(20.0f, 90.0f, 0.0f), 1300.0f);
        lineEmitter.modifyProp(END_POSITION, v -> v.set(20.0f, 90.0f, -300.0f));
        lineEmitter.modifyProp(BASE_VELOCITY, v -> v.set(0.0f, 0.0f, 0.0f));
        lineEmitter.modifyProp(LIFE_TIME_RANGE, v -> v.set(0.2f, 1.6f));
        lineEmitter.modifyProp(COLOR, v -> v.set(0.3f, 0.1f, 0.8f, 1.0f));
        lineEmitter.modifyProp(SIZE_RANGE, v -> v.set(0.1f, 0.3f));
        lineEmitter.trySet(BLOOM_INTENSITY, 1.0f);
        lineEmitter.trySet(PARTICLE_PRIMITIVE_TYPE, ParticlePrimitiveTypeStates.TRIANGLE);

        LineEmitter lineEmitterPoint = new LineEmitter(new Vector3f(30.0f, 90.0f, 0.0f), 1300.0f);
        lineEmitterPoint.modifyProp(END_POSITION, v -> v.set(20.0f, 90.0f, 300.0f));
        lineEmitterPoint.modifyProp(BASE_VELOCITY, v -> v.set(0.0f, 0.0f, 0.0f));
        lineEmitterPoint.modifyProp(LIFE_TIME_RANGE, v -> v.set(0.2f, 1.6f));
        lineEmitterPoint.modifyProp(COLOR, v -> v.set(0.3f, 0.1f, 0.8f, 1.0f));
        lineEmitterPoint.modifyProp(SIZE_RANGE, v -> v.set(0.01f, 0.03f));
        lineEmitterPoint.trySet(BLOOM_INTENSITY, 1.0f);
        lineEmitterPoint.trySet(PARTICLE_PRIMITIVE_TYPE, ParticlePrimitiveTypeStates.POINT);

        // effect
        GPUParticleEffect effect = new GPUParticleEffect(
                List.of(circleEmitter, lineEmitter, lineEmitterPoint),
                100_0000,
                60.0f,
                physicsParamBuilder.build()
        );

        effect.setConsumer(new GPUParticleEffect.EffectConsumer() {
            float time = 0.0f;
            @Override
            public void accept(GPUParticleEffect effect, float deltaTime) {
                this.time += deltaTime;
                if (this.time >= 0.2f) {
                    this.time = 0.0f;
                    effect.forEachEmitter(emitter -> {
                        if (emitter instanceof CircleEmitter ce) {
                            ce.addBurst(new ParticleBurst(0.0f, 300));
                        }
                    });
                }
            }
        });

        if (ParticleSystem.getInstance().spawnEffect(effect)) {
            testEffects.add(effect);
        }
    }

    public static void clearTestEffects() {
        for (GPUParticleEffect testEffect : testEffects) {
            ParticleSystem.getInstance().removeEffect(testEffect);
        }
        testEffects.clear();
    }

    private static void paraTest() {
        ParaData paraData = genParaData();
        try {

            MagicCircle circle = testInjectAnimation(paraData);

            modifyMagicCircle(circle);

            MagicCircleManager.getInstance().addCircle(circle);
        } catch (ConversionException e) {
            Paramagic.LOG.error("Para conversion error: ", e);
        } catch (AssemblyException e) {
            Paramagic.LOG.error("Assembly error: ", e);
        }
    }

    private MagicCircle testInjectAnimation(ParaData paraData) throws AssemblyException, ConversionException {
        ParaComposer composer = ParaComposer.getINSTANCE();
        MagicCircle circle = composer.assemble(paraData, genAnimationBindingConfig(), null);

        return circle;
    }

    private AnimationBindingConfig genAnimationBindingConfig() {
        List<AnimationBinding> animationBindingList = new ArrayList<>();

        AnimatorData animatingCenter;
        {
            TrackData<?> rotationTrack = new KeyframeTrackData<>(
                    AllAnimatableProperties.ROTATION,
                    List.of(
                            new KeyframeData<>(1.0f, new Quaternionf().rotateY((float)Math.toRadians(180))),
                            new KeyframeData<>(0f, new Quaternionf().rotateY((float)Math.toRadians(0))),
                            new KeyframeData<>(2.0f, new Quaternionf().rotateY((float)Math.toRadians(359)))
                    ),
                    true
            );

            TrackData<?> scaleTrack = new KeyframeTrackData<>(
                    AllAnimatableProperties.SCALE,
                    List.of(
                            new KeyframeData<>(6.0f, new Vector3f(1)),
                            new KeyframeData<>(0f, new Vector3f(0.0f, 0.0f, 0.0f)),
                            new KeyframeData<>(3.0f, new Vector3f(0.5f)),
                            new KeyframeData<>(24.0f, new Vector3f(2))
                    ),
                    false
            );

            animatingCenter = new AnimatorData(List.of(rotationTrack, scaleTrack));
        }

        AnimationBinding data1 = new AnimationBinding(
                "root.3",
                null,
                animatingCenter
        );
        animationBindingList.add(data1);
        // --------------------------------------------
        AnimatorData animatingColor;
        {
            TrackData<?> colorTrack = new KeyframeTrackData<>(
                    AllAnimatableProperties.EMISSIVE_COLOR,
                    List.of(
                            new KeyframeData<>(0.0f, new Vector3f(1.0f, 1.0f, 0.0f)),
                            new KeyframeData<>(20.0f, new Vector3f(1.0f, 0.0f, 1.0f)),
                            new KeyframeData<>(30.0f, new Vector3f(1.0f, 1.0f, 1.0f)),
                            new KeyframeData<>(40.0f, new Vector3f(0.0f, 0.0f, 1.0f))
                    ),
                    true
            );

            TrackData<?> intensity = new KeyframeTrackData<>(
                    AllAnimatableProperties.EMISSIVE_INTENSITY,
                    List.of(
                            new KeyframeData<>(0.0f, 0.1f),
                            new KeyframeData<>(5.0f, 1.0f),
                            new KeyframeData<>(10.0f, 3.0f),
                            new KeyframeData<>(15.0f, 2.0f),
                            new KeyframeData<>(20.0f, 0.1f)
                    ),
                    true
            );
            animatingColor = new AnimatorData(List.of(colorTrack, intensity));
        }
        AnimationBinding data2 = new AnimationBinding(
                "root.3.2",
                null,
                animatingColor
        );
        animationBindingList.add(data2);

        return new AnimationBindingConfig(animationBindingList);
    }

    private void modifyMagicCircle(MagicCircle circle) throws ConversionException {
        circle.getTransform()
                .setPosition(0 , 110.01f , 0)
                .setRotationDegrees(0.0f, 0, 0)
                .setScale(1.0f, 1.0f, 1.0f);
    }

    private ParaData genParaData() {
        VoidParaData rootPara = new VoidParaData();

        rootPara.addChild(new RingParaData(
                4.0f, 4.2f,
                64
        ));
        rootPara.addChild(new RingParaData(
                3.8f, 3.9f,
                64
        ));


        RingParaData ring2 = new RingParaData(
                2.0f, 2.1f,
                64
        );
        ring2.position.set(0, 0.09f, 0);
        ring2.color.set(0.7f, 0.7f, 0.2f);
        rootPara.addChild(ring2);

        {
            VoidParaData group = new VoidParaData();

            group.position.set(0, 0.2f, 0);

            PolygonParaData polygon1 = new PolygonParaData(
                    3.0f,
                    3,
                    0.0f,
                    0.1f
            );
            polygon1.position.set(0, 0.1f, 0);
            polygon1.color.set(0.2f, 0.7f, 0.7f);
            group.addChild(polygon1);

            PolygonParaData polygon2 = new PolygonParaData(
                    3.0f,
                    3,
                    (float) Math.toRadians(60),
                    0.1f
            );
            polygon2.position.set(0, 0.2f, 0);
            polygon2.color.set(0.6f, 0.1f, 0.3f);
            group.addChild(polygon2);
            {
                CurvyStarParaData curvy = new CurvyStarParaData(
                        2.0f,
                        7,
                        -1.0f,
                        0.0f,
                        0.05f
                );
                curvy.color.set(0.8f, 0.4f, 0.9f, 0.8f);
                curvy.position.set(0, -0.3f, 0);
                group.addChild(curvy);
            }

            rootPara.addChild(group);
        }



        ParaData paraData = new ParaData(rootPara);
        return paraData;
    }
}
