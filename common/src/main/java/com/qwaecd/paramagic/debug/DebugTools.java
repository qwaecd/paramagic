package com.qwaecd.paramagic.debug;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.assembler.AssemblyException;
import com.qwaecd.paramagic.assembler.ParaComposer;
import com.qwaecd.paramagic.client.obj.sun.Sun;
import com.qwaecd.paramagic.client.renderbase.factory.SphereFactory;
import com.qwaecd.paramagic.core.particle.ParticleManager;
import com.qwaecd.paramagic.core.particle.builder.PhysicsParamBuilder;
import com.qwaecd.paramagic.core.particle.effect.GPUParticleEffect;
import com.qwaecd.paramagic.core.particle.emitter.impl.*;
import com.qwaecd.paramagic.core.particle.emitter.prop.ParticleBurst;
import com.qwaecd.paramagic.core.render.ModRenderSystem;
import com.qwaecd.paramagic.core.render.api.IRenderable;
import com.qwaecd.paramagic.core.render.shader.ShaderManager;
import com.qwaecd.paramagic.core.render.texture.Material;
import com.qwaecd.paramagic.data.animation.AnimationBindingData;
import com.qwaecd.paramagic.data.animation.AnimatorData;
import com.qwaecd.paramagic.data.animation.BindingData;
import com.qwaecd.paramagic.data.animation.PropertyType;
import com.qwaecd.paramagic.data.animation.track.KeyframeData;
import com.qwaecd.paramagic.data.animation.track.KeyframeTrackData;
import com.qwaecd.paramagic.data.animation.track.TrackData;
import com.qwaecd.paramagic.data.para.*;
import com.qwaecd.paramagic.feature.MagicCircle;
import com.qwaecd.paramagic.feature.MagicCircleManager;
import lombok.experimental.UtilityClass;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

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

        physicsParamBuilder
                .centerForceEnabled(false)
                .centerForceParam(13.6f, -2.0f)
                .centerForcePos(20.0f, 115.0f, 2.0f)
                .centerForceMaxRadius(1000.0f)
                .linearForceEnabled(false)
                .linearForce(0.01f, -0.0981f / 1000.0f, 0.0f)
                .dragCoefficient(0.0f);

        // Point Emitter
        PointEmitter pointEmitter = new PointEmitter(
                new Vector3f(0, 80, 10),
                10.0f
        );

        pointEmitter.bloomIntensityProp.set(1.8f);

        // Line Emitter
        LineEmitter lineEmitter = new LineEmitter(
                new Vector3f(0.0f, 130.0f, 0.0f),
                0.0f
        );
        lineEmitter.startPositionProp.modify(v -> v.set(-10.0f, 130.0f, 1.0f));
        lineEmitter.endPositionProp.modify(v -> v.set(10.0f, 130.0f, 1.0f));
        lineEmitter.baseVelocityProp.modify(v -> v.set(0.0f, 0.05f, 0.0f));
        lineEmitter.lifetimeRangeProp.modify(v -> v.set(0.1f, 1.3f));
        lineEmitter.colorProp.modify(v -> v.set(0.4f, 0.5f, 1.0f, 1.0f));
        lineEmitter.bloomIntensityProp.set(0.5f);
        lineEmitter.velocityModeProp.set(VelocityModeStates.RANDOM);
        lineEmitter.addBurst(new ParticleBurst(0.1f, 3000));

        // Sphere Emitter
        SphereEmitter sphereEmitter = new SphereEmitter(
                new Vector3f(20.0f, 120.0f, 0.0f),
                0.0f
        );
        sphereEmitter.sphereRadiusProp.set(1.0f);
        sphereEmitter.baseVelocityProp.modify(v -> v.set(0.0f, 9.3f, 0.0f));
        sphereEmitter.lifetimeRangeProp.modify(v -> v.set(1.0f, 10.0f));
        sphereEmitter.colorProp.modify(v -> v.set(1.0f, 0.0f, 0.0f, 1.0f));
        sphereEmitter.bloomIntensityProp.set(1.0f);
        sphereEmitter.emitFromVolumeProp.set(true);
        sphereEmitter.velocitySpreadProp.set(1.0f);
        sphereEmitter.velocityModeProp.set(VelocityModeStates.RADIAL_FROM_CENTER);
        int numBursts = 1;
        for (int i = 0; i < numBursts; i ++) {
//            sphereEmitter.addBurst(new ParticleBurst(0.05f * i + 0.1f, 10000));
        }


        // Cube Emitter
        CubeEmitter cubeEmitter = new CubeEmitter(
                new Vector3f(0.0f),
                1.0f
        );
        cubeEmitter.cubeAABBProp.modify(v -> v.setAABB(
                20.0f, 120.0f, 3.0f,
                20.0f + 0.5f, 120.0f + 0.5f, 3.0f + 0.5f
        ));
        cubeEmitter.baseVelocityProp.modify(v -> v.set(0.0f, -0.3f, 1.91f));
        cubeEmitter.lifetimeRangeProp.modify(v -> v.set(5.0f, 7.0f).add(40.0f, 40.0f));
        cubeEmitter.colorProp.modify(v -> v.set(0.2f, 1.0f, 0.5f, 1.0f));
        cubeEmitter.bloomIntensityProp.set(1.8f);
        cubeEmitter.emitFromVolumeProp.set(true);
        cubeEmitter.velocityModeProp.set(VelocityModeStates.DIRECT);

        // effect
        GPUParticleEffect effect = new GPUParticleEffect(
                List.of(cubeEmitter, sphereEmitter, lineEmitter),
                100_0000,
                3.0f,
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
        MagicCircle circle = composer.assemble(paraData, genAnimationBindingData(), null);

        return circle;
    }

    private AnimationBindingData genAnimationBindingData() {
        List<BindingData> bindingDataList = new ArrayList<>();

        AnimatorData animatingCenter;
        {
            TrackData<?> rotationTrack = new KeyframeTrackData<>(
                    PropertyType.ROTATION,
                    List.of(
                            new KeyframeData<>(1.0f, new Quaternionf().rotateY((float)Math.toRadians(180))),
                            new KeyframeData<>(0f, new Quaternionf().rotateY((float)Math.toRadians(0))),
                            new KeyframeData<>(2.0f, new Quaternionf().rotateY((float)Math.toRadians(359)))
                    ),
                    true
            );

            TrackData<?> scaleTrack = new KeyframeTrackData<>(
                    PropertyType.SCALE,
                    List.of(
                            new KeyframeData<>(6.0f, new Vector3f(1)),
                            new KeyframeData<>(0f, new Vector3f(0.0f, 0.0f, 0.0f)),
                            new KeyframeData<>(3.0f, new Vector3f(0.5f)),
                            new KeyframeData<>(24.0f, new Vector3f(2))
                    ),
                    true
            );

            animatingCenter = new AnimatorData(List.of(rotationTrack, scaleTrack));
        }

        BindingData data1 = new BindingData(
                "root.3",
                null,
                animatingCenter
        );
        bindingDataList.add(data1);
        // --------------------------------------------
        AnimatorData animatingColor;
        {
            TrackData<?> colorTrack = new KeyframeTrackData<>(
                    PropertyType.EMISSIVE_COLOR,
                    List.of(
                            new KeyframeData<>(0.0f, new Vector3f(1.0f, 1.0f, 0.0f)),
                            new KeyframeData<>(20.0f, new Vector3f(1.0f, 0.0f, 1.0f)),
                            new KeyframeData<>(30.0f, new Vector3f(1.0f, 1.0f, 1.0f)),
                            new KeyframeData<>(40.0f, new Vector3f(0.0f, 0.0f, 1.0f))
                    ),
                    true
            );

            TrackData<?> intensity = new KeyframeTrackData<>(
                    PropertyType.EMISSIVE_INTENSITY,
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
        BindingData data2 = new BindingData(
                "root.0",
                null,
                animatingColor
        );
        bindingDataList.add(data2);

        return new AnimationBindingData(bindingDataList);
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

            rootPara.addChild(group);
        }

        ParaData paraData = new ParaData(rootPara);
        return paraData;
    }
}
