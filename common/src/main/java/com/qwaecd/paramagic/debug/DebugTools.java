package com.qwaecd.paramagic.debug;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.assembler.AssemblyException;
import com.qwaecd.paramagic.assembler.ParaComposer;
import com.qwaecd.paramagic.client.obj.sun.Sun;
import com.qwaecd.paramagic.client.renderbase.factory.SphereFactory;
import com.qwaecd.paramagic.core.particle.ParticleManager;
import com.qwaecd.paramagic.core.particle.builder.PhysicsParamBuilder;
import com.qwaecd.paramagic.core.particle.effect.GPUParticleEffect;
import com.qwaecd.paramagic.core.particle.emitter.ParticleBurst;
import com.qwaecd.paramagic.core.particle.emitter.impl.*;
import com.qwaecd.paramagic.core.particle.emitter.property.type.VelocityModeStates;
import com.qwaecd.paramagic.core.render.ModRenderSystem;
import com.qwaecd.paramagic.core.render.api.IRenderable;
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
        SphereFactory sphereFactory = new SphereFactory();
        Material material = new Material(ShaderManager.getInstance().getBaseBallInShader());
        ball = sphereFactory.withMaterial(material).createInstance();
        ball.getTransform().getModelMatrix().translate(0, 100, 0).scale(4.0f, 4.0f, 4.0f);
//        ModRenderSystem.getInstance().addRenderable(ball);

        IRenderable sun = new Sun(ShaderManager.getInstance().getShader("sun"));
        sun.getTransform().setPosition(0, 80, 10).setScale(5.0f, 5.0f, 5.0f);
        ModRenderSystem.getInstance().addRenderable(sun);
        paraTest();
        effectTest();
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
                .dragCoefficient(0.0f);

        // Point Emitter
        PointEmitter pointEmitter = new PointEmitter(
                new Vector3f(0, 80, 10),
                10.0f
        );

        pointEmitter.getProperty(BLOOM_INTENSITY).set(1.8f);

        // Line Emitter
        LineEmitter lineEmitter = new LineEmitter(
                new Vector3f(0.0f, 130.0f, 0.0f),
                0.0f
        );
        lineEmitter.getProperty(POSITION).modify(v -> v.set(-10.0f, 130.0f, 1.0f));
        lineEmitter.getProperty(END_POSITION).modify(v -> v.set(10.0f, 130.0f, 1.0f));
        lineEmitter.getProperty(BASE_VELOCITY).modify(v -> v.set(0.0f, 0.05f, 0.0f));
        lineEmitter.getProperty(LIFE_TIME_RANGE).modify(v -> v.set(0.1f, 1.3f));
        lineEmitter.getProperty(COLOR).modify(v -> v.set(0.4f, 0.5f, 1.0f, 1.0f));
        lineEmitter.getProperty(BLOOM_INTENSITY).set(0.5f);
        lineEmitter.getProperty(VELOCITY_MODE).set(VelocityModeStates.RANDOM);
        lineEmitter.addBurst(new ParticleBurst(0.1f, 3000));

        // Sphere Emitter
        SphereEmitter sphereEmitter = new SphereEmitter(
                new Vector3f(centerForcePos),
                1000.0f
        );
        sphereEmitter.getProperty(SPHERE_RADIUS).set(20.0f);
        sphereEmitter.getProperty(BASE_VELOCITY).modify(v -> v.set(0.0f, 0.0f, 0.0f));
        sphereEmitter.getProperty(LIFE_TIME_RANGE).modify(v -> v.set(30.0f, 60.0f));
        sphereEmitter.getProperty(COLOR).modify(v -> v.set(1.0f, 0.0f, 0.4f, 1.0f));
        sphereEmitter.getProperty(BLOOM_INTENSITY).set(1.0f);
        sphereEmitter.getProperty(EMIT_FROM_VOLUME).set(true);
        sphereEmitter.getProperty(VELOCITY_SPREAD).set(1.0f);
        sphereEmitter.getProperty(VELOCITY_MODE).set(VelocityModeStates.DIRECT);


        // Cube Emitter
        CubeEmitter cubeEmitter = new CubeEmitter(
                new Vector3f(0.0f),
                100.0f
        );
        cubeEmitter.getProperty(CUBE_AABB).modify(v -> v.setAABB(
                20.0f, 120.0f, 3.0f,
                20.0f + 0.5f, 120.0f + 0.5f, 3.0f + 0.5f
        ));
        cubeEmitter.getProperty(BASE_VELOCITY).modify(v -> v.set(0.0f, -0.3f, 1.91f));
        cubeEmitter.getProperty(LIFE_TIME_RANGE).modify(v -> v.set(5.0f, 7.0f).add(40.0f, 40.0f));
        cubeEmitter.getProperty(COLOR).modify(v -> v.set(0.2f, 1.0f, 0.5f, 1.0f));
        cubeEmitter.getProperty(BLOOM_INTENSITY).set(1.8f);
        cubeEmitter.getProperty(EMIT_FROM_VOLUME).set(true);
        cubeEmitter.getProperty(VELOCITY_MODE).set(VelocityModeStates.DIRECT);

        // circle emitter
        CircleEmitter circleEmitter = new CircleEmitter(
                centerForcePos,
                1000.0f
        );
        circleEmitter.getProperty(BASE_VELOCITY).modify(v -> v.set(0.0f, 1.0f, 0.0f));
        circleEmitter.getProperty(LIFE_TIME_RANGE).modify(v -> v.set(0.2f, 1.6f));
        circleEmitter.getProperty(COLOR).modify(v -> v.set(1.0f, 0.0f, 0.4f, 1.0f));
        circleEmitter.getProperty(SIZE_RANGE).modify(v -> v.set(1.1f, 2.3f));
        circleEmitter.getProperty(BLOOM_INTENSITY).set(2.0f);
        circleEmitter.getProperty(VELOCITY_MODE).set(VelocityModeStates.DIRECT);
        circleEmitter.getProperty(NORMAL).modify(v -> v.set(0.0f, 1.0f, 0.0f));
        circleEmitter.getProperty(INNER_OUTER_RADIUS).modify(v -> v.set(0.5f, 0.7f));

        // effect
        GPUParticleEffect effect = new GPUParticleEffect(
                List.of(circleEmitter),
                100_0000,
                600.0f,
                physicsParamBuilder.build()
        );
        if (ParticleManager.getInstance().spawnEffect(effect)) {
            testEffects.add(effect);
        }
    }

    public static void clearTestEffects() {
        for (GPUParticleEffect testEffect : testEffects) {
            ParticleManager.getInstance().removeEffect(testEffect);
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
