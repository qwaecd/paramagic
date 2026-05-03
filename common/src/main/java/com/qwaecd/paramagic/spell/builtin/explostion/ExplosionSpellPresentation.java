package com.qwaecd.paramagic.spell.builtin.explostion;

import com.qwaecd.paramagic.assembler.ParaComposer;
import com.qwaecd.paramagic.core.particle.ParticleSystem;
import com.qwaecd.paramagic.core.particle.builder.PhysicsParamBuilder;
import com.qwaecd.paramagic.core.particle.effect.GPUParticleEffect;
import com.qwaecd.paramagic.core.particle.emitter.Emitter;
import com.qwaecd.paramagic.core.particle.emitter.impl.LineEmitter;
import com.qwaecd.paramagic.core.particle.emitter.property.type.VelocityModeStates;
import com.qwaecd.paramagic.data.animation.property.AllAnimatableProperties;
import com.qwaecd.paramagic.data.animation.struct.AnimationBinding;
import com.qwaecd.paramagic.data.animation.struct.AnimationBindingConfig;
import com.qwaecd.paramagic.data.animation.struct.AnimatorData;
import com.qwaecd.paramagic.data.animation.util.TimelineBuilder;
import com.qwaecd.paramagic.data.para.struct.ParaComponentData;
import com.qwaecd.paramagic.data.para.struct.ParaData;
import com.qwaecd.paramagic.data.para.struct.components.CurvyStarParaData;
import com.qwaecd.paramagic.data.para.struct.components.PolygonParaData;
import com.qwaecd.paramagic.data.para.struct.components.RingParaData;
import com.qwaecd.paramagic.data.para.util.ParaComponentBuilder;
import com.qwaecd.paramagic.feature.circle.MagicCircle;
import com.qwaecd.paramagic.feature.circle.MagicCircleManager;
import com.qwaecd.paramagic.spell.builtin.AllBuiltinSpellIds;
import com.qwaecd.paramagic.spell.client.CircleAssets;
import com.qwaecd.paramagic.spell.client.ClientSpellContext;
import com.qwaecd.paramagic.spell.client.SpellPresentation;
import com.qwaecd.paramagic.spell.core.EndSpellReason;
import com.qwaecd.paramagic.spell.core.store.AllSessionDataKeys;
import com.qwaecd.paramagic.spell.core.store.SessionDataValue;
import com.qwaecd.paramagic.spell.util.transform.BillboardFunction;
import com.qwaecd.paramagic.spell.view.CasterTransformSource;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.qwaecd.paramagic.core.particle.emitter.property.key.AllEmitterProperties.*;

import static com.qwaecd.paramagic.core.particle.emitter.property.key.AllEmitterProperties.BASE_VELOCITY;
import static com.qwaecd.paramagic.core.particle.emitter.property.key.AllEmitterProperties.BLOOM_INTENSITY;
import static com.qwaecd.paramagic.core.particle.emitter.property.key.AllEmitterProperties.COLOR;
import static com.qwaecd.paramagic.core.particle.emitter.property.key.AllEmitterProperties.VELOCITY_MODE;
import static com.qwaecd.paramagic.data.animation.property.AllAnimatableProperties.*;
import static com.qwaecd.paramagic.data.animation.property.AllAnimatableProperties.POSITION;
import static com.qwaecd.paramagic.spell.builtin.explostion.ExplosionSpellRuntime.CASTING_TICKS;
import static com.qwaecd.paramagic.spell.builtin.explostion.ExplosionSpellRuntime.CHANNELING_TICKS;

public class ExplosionSpellPresentation implements SpellPresentation {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExplosionSpellPresentation.class);
    private static final int TOTAL_TICKS = CASTING_TICKS + CHANNELING_TICKS;

    @Nullable
    private MagicCircle remoteCircle;

    private final AroundPlayerCircleHolder aroundPlayerCircleHolder = new AroundPlayerCircleHolder();

    private final ExplosionGPUEffect effect = new ExplosionGPUEffect();

    @Nullable
    private GPUParticleEffect canReleaseEffect;

    private int elapsedTicks = 0;
    private boolean frontBuilt = false;
    private boolean finished = false;

    @Override
    public void onStart(ClientSpellContext context) {
        this.elapsedTicks = 0;
        this.finished = false;
        if (!this.frontBuilt) {
            this.aroundPlayerCircleHolder.build(context.casterSource());
            this.frontBuilt = true;
        }
        this.effect.onStart(context);
    }

    @Override
    public void tick(ClientSpellContext context) {
        if (this.finished) {
            return;
        }

        this.elapsedTicks++;
        if (this.elapsedTicks >= CASTING_TICKS) {
            this.createRemoteCircle(context);
        }
        this.effect.tick(context);
        if (this.elapsedTicks >= TOTAL_TICKS && this.canReleaseEffect == null) {
            Emitter emitter = createEmitter(context);
            if (emitter != null) {
                PhysicsParamBuilder builder = new PhysicsParamBuilder();
                builder.linearForceEnabled(true)
                        .linearForce(0.0f, 0.3f, 0.0f)
                        .dragCoefficient(0.2f);
                this.canReleaseEffect = new GPUParticleEffect(List.of(emitter), 10_000, -1, builder.build());
                ParticleSystem.getInstance().spawnEffect(this.canReleaseEffect);
            }
        }
    }

    @Nullable
    private static Emitter createEmitter(ClientSpellContext context) {
        SessionDataValue<Vector3f> value = context.getDataStore().getValue(AllSessionDataKeys.firstPosition);
        if (value == null) {
            return null;
        }
        Vector3f pos = value.getValue();
        LineEmitter emitter = new LineEmitter(pos, 1000.0f);
        emitter.modifyProp(COLOR, v -> v.set(1.2f, 0.5f, 0.8f, 1.0f));
        emitter.modifyProp(LIFE_TIME_RANGE, v -> v.set(1.1f, 3.4f));
        emitter.modifyProp(SIZE_RANGE, v -> v.set(1.04f, 4.16f));
        emitter.modifyProp(END_POSITION, v -> v.set(pos.x, pos.y + 10.0f * 3.2f, pos.z));
        emitter.trySet(BLOOM_INTENSITY, 0.4f);
        emitter.trySet(VELOCITY_MODE, VelocityModeStates.RANDOM);
        emitter.modifyProp(BASE_VELOCITY, v -> v.set(3.8f));
        return emitter;
    }

    @Override
    public void onStop(ClientSpellContext context, EndSpellReason reason) {
        this.cleanup();
        this.finished = true;
    }

    @Override
    public boolean canDispose() {
        return this.finished;
    }

    @Override
    public void dispose(ClientSpellContext context) {
        this.cleanup();
        this.finished = true;
    }

    private void createRemoteCircle(ClientSpellContext context) {
        if (this.remoteCircle != null) {
            return;
        }

        try {
            SessionDataValue<Vector3f> dataValue = context.getDataStore().getValue(AllSessionDataKeys.firstPosition);
            if (dataValue == null) {
                return;
            }

            Vector3f pos = dataValue.getValue();
            this.remoteCircle = ParaComposer.assemble(RemoteCircleData.create());
            this.remoteCircle.getTransform().setPosition(pos.x, pos.y + 0.01f, pos.z);
        } catch (Exception e) {
            LOGGER.error("Failed to create {} spell visual: ", AllBuiltinSpellIds.EXPLOSION, e);
            return;
        }
        MagicCircleManager.getInstance().addCircle(this.remoteCircle);
    }

    private void cleanup() {
        if (this.remoteCircle != null) {
            this.remoteCircle.requestDestroy();
            this.remoteCircle = null;
        }
        this.aroundPlayerCircleHolder.close();
        this.effect.cleanup();
        this.frontBuilt = false;
        if (this.canReleaseEffect != null) {
            this.canReleaseEffect.setConsumer(new GPUParticleEffect.EffectConsumer() {
                private float elapsedTime = 0.0f;
                private boolean flag = false;
                @Override
                public void accept(GPUParticleEffect effect, float deltaTime) {
                    this.elapsedTime += deltaTime;
                    if (!flag) {
                        effect.setShouldUpdateEmitter(false);
                        effect.getPhysicsParameter()
                                .setLinearForce(0.0f, 0.8f, 0.0f);
                        flag = true;
                    }
                    if (this.elapsedTime >= 8.0f) {
                        ParticleSystem.getInstance().removeEffect(effect);
                    }
                }
            });
            this.canReleaseEffect = null;
        }
    }

    static class AroundPlayerCircleHolder {
        private static final float T1 = 0.8f;
        private static final float T2 = T1 + 0.1f;
        private static final float FRONT_LENGTH = 3.0f;

        private static final Vector4f RING_COLOR = new Vector4f(0.6f, 0.25f, 0.25f, 1.0f);
        private static final Vector4f STAR_COLOR = new Vector4f(0.5f, 0.25f, 0.25f, 1.0f);

        @Nullable
        private MagicCircle forward;

        void build(CasterTransformSource tfSource) {
            try {
                this.forward = ParaComposer.assemble(createFrontAssets());
                this.forward.getTransform().setScale(2.0f);
                BillboardFunction billboardFunction = new BillboardFunction(false, FRONT_LENGTH);
                this.forward.registerModifyTransform(transform -> billboardFunction.apply(transform, tfSource));
                MagicCircleManager.getInstance().addCircle(this.forward);
            } catch (Exception e) {
                LOGGER.error("Failed to create around player circle: ", e);
            }
        }

        private static CircleAssets createFrontAssets() {
            return new CircleAssets(new ParaData(createFrontParaData()), createFrontAnimConfig());
        }

        private static ParaComponentData createFrontParaData() {
            float intensity = 0.35f;
            float centerTriangleLineWidth = 0.04f;
            float starRadius = 0.4f;
            return new ParaComponentBuilder().withName("front")
                    .beginChild()
                    .withName("centerStar")
                    .beginChild(new PolygonParaData(starRadius, 3, 0.0f, centerTriangleLineWidth))
                    .withColor(STAR_COLOR)
                    .withIntensity(intensity)
                    .endChild()
                    .beginChild(new PolygonParaData(starRadius, 3, (float) Math.toRadians(60.0f), centerTriangleLineWidth))
                    .withColor(STAR_COLOR)
                    .withIntensity(intensity)
                    .endChild()
                    .endChild()
                    .beginChild(new CurvyStarParaData(0.4f, 6, -1.5f, 0.0f, 0.03f))
                    .withName("cStar")
                    .withColor(STAR_COLOR)
                    .withIntensity(intensity)
                    .endChild()
                    .beginChild(new RingParaData(1.5f, 1.55f, 8))
                    .withName("r0")
                    .withColor(RING_COLOR)
                    .withIntensity(intensity)
                    .endChild()
                    .beginChild(new RingParaData(1.6f, 1.63f, 8))
                    .withName("r01")
                    .withColor(RING_COLOR)
                    .withIntensity(intensity)
                    .endChild()
                    .beginChild(new PolygonParaData(1.4f, 4, 0.0f, 0.08f))
                    .withName("r1")
                    .withColor(RING_COLOR)
                    .withIntensity(intensity)
                    .endChild()
                    .beginChild(new PolygonParaData(1.4f, 4, (float) Math.toRadians(45.0f), 0.08f))
                    .withName("r2")
                    .withColor(RING_COLOR)
                    .withIntensity(intensity)
                    .endChild()
                    .build();
        }

        private static AnimationBindingConfig createFrontAnimConfig() {
            List<AnimationBinding> bindings = new ArrayList<>();
            bindings.add(new AnimationBinding("centerStar", null, buildScaleRotationAnimator(0.0f, T1, 1.0f, 45.0f)));
            bindings.add(new AnimationBinding("r0", null, buildScaleRotationAnimator(T1 - 0.4f, T2 + 0.4f, 1.0f, 45.0f)));
            bindings.add(new AnimationBinding("r01", null, buildScaleRotationAnimator(T1, T2 + 0.6f, 1.0f, 45.0f)));
            float outerStart = T1 - 0.1f;
            float outerEnd = T2 + 0.2f;
            bindings.add(new AnimationBinding("cStar", null, buildScaleRotationAnimator(outerStart, outerEnd, 1.0f, 45.0f)));
            bindings.add(new AnimationBinding("r1", null, buildScaleRotationAnimator(outerStart, outerEnd, 1.0f, 45.0f)));
            bindings.add(new AnimationBinding("r2", null, buildScaleRotationAnimator(outerStart, outerEnd, 1.0f, 45.0f)));
            return new AnimationBindingConfig(bindings);
        }

        private static AnimatorData buildScaleRotationAnimator(float startTime, float endTime, float targetScale, float degreesPerSecond) {
            TimelineBuilder timelineBuilder = new TimelineBuilder();
            float fullRotationDuration = 359.0f / Math.abs(degreesPerSecond);
            float angleSign = Math.signum(degreesPerSecond);

            timelineBuilder.at(0.0f)
                    .keyframe(ROTATION, new Quaternionf().identity(), true)
                    .keyframe(AllAnimatableProperties.SCALE, new Vector3f(0.0f));
            if (startTime > 0.0f) {
                timelineBuilder.at(startTime)
                        .keyframe(AllAnimatableProperties.SCALE, new Vector3f(0.0f));
            }
            timelineBuilder.at(endTime)
                    .keyframe(AllAnimatableProperties.SCALE, new Vector3f(targetScale));
            timelineBuilder.at(fullRotationDuration * 0.25f)
                    .keyframe(ROTATION, new Quaternionf().rotateY((float) Math.toRadians(90.0f * angleSign)));
            timelineBuilder.at(fullRotationDuration * 0.5f)
                    .keyframe(ROTATION, new Quaternionf().rotateY((float) Math.toRadians(180.0f * angleSign)));
            timelineBuilder.at(fullRotationDuration * 0.75f)
                    .keyframe(ROTATION, new Quaternionf().rotateY((float) Math.toRadians(270.0f * angleSign)));
            timelineBuilder.at(fullRotationDuration)
                    .keyframe(ROTATION, new Quaternionf().rotateY((float) Math.toRadians(359.0f * angleSign)));
            return timelineBuilder.build();
        }

        void close() {
            if (this.forward != null) {
                this.forward.requestDestroy();
                this.forward = null;
            }
        }
    }

    static class RemoteCircleData {
        static CircleAssets create() {
            float[] targetScales = new float[]{0.9f, 0.2f, 0.4f, 0.5f, 0.87f, 1.3f, 0.6f, 0.2f, 0.4f, 0.2f, 0.8f};
            RemoteCircleData helper = new RemoteCircleData();
            ParaComponentBuilder rootBuilder = new ParaComponentBuilder();
            for (int i = 0; i < targetScales.length; i++) {
                helper.appendRing(rootBuilder, "ring" + i, targetScales[i]);
            }
            float currentTime = 0.0f;
            final float yOffset = 3.2f;
            List<AnimationBinding> bindings = new ArrayList<>();
            for (int i = 0; i < targetScales.length; i++) {
                AnimatorData animatorData = genAnimData(currentTime, currentTime + 0.9f, targetScales[i] * 1.4f, i * yOffset);
                bindings.add(new AnimationBinding("ring" + i, null, animatorData));
                currentTime += 0.3f;
            }
            ParaData paraData = new ParaData(rootBuilder);
            AnimationBindingConfig animConfig = new AnimationBindingConfig(bindings);
            return new CircleAssets(paraData, animConfig);
        }

        private void appendRing(ParaComponentBuilder rootBuilder, String name, float targetScale) {
            rootBuilder.beginChild(genComponentData(name))
                    .withScale(targetScale)
                    .endChild();
        }

        private static AnimatorData genAnimData(float startTime, float endTime, float targetScale, float y) {
            TimelineBuilder builder = new TimelineBuilder();
            // rotate
            builder.at(0.0f)
                    .keyframe(ROTATION, new Quaternionf().rotateY((float) Math.toRadians(359)), true)
                    .at(5.0f)
                    .keyframe(ROTATION, new Quaternionf().rotateY((float) Math.toRadians(180)))
                    .at(10.0f)
                    .keyframe(ROTATION, new Quaternionf().rotateY((float) Math.toRadians(0)));
            // scale
            builder.at(0.0f)
                    .keyframe(SCALE, new Vector3f(0.0f))
                    .at(startTime)
                    .keyframe(SCALE, new Vector3f(0.0f))
                    .at(endTime)
                    .keyframe(SCALE, new Vector3f(targetScale));
            // position
            builder.at(0.0f)
                    .keyframe(POSITION, new Vector3f(0.0f, y == 0.0f ? 0.01f : y, 0.0f));
            return builder.build();
        }
    }

    static ParaComponentData genComponentData(String paraName) {
        float intensity = 0.7f;
        Vector4f ringColor = new Vector4f(1.1f, 0.6f, 0.5f, 0.8f);
        Vector4f starColor = new Vector4f(1.0f, 0.5f, 0.5f, 0.8f);
        return new ParaComponentBuilder().withName(paraName)
                .beginChild(new RingParaData(1.1f, 1.17f, 64))
                .withColor(ringColor)
                .withIntensity(intensity)
                .endChild()
                .beginChild(new RingParaData(1.2f, 1.25f, 64))
                .withColor(ringColor)
                .withIntensity(intensity)
                .endChild()
                .beginChild(new CurvyStarParaData(1.1f, 6, 0.3f, 0.0f, 0.05f))
                .withColor(starColor)
                .withIntensity(intensity)
                .endChild()
                .beginChild(new RingParaData(4.0f, 4.1f, 64))
                .withColor(ringColor)
                .withIntensity(intensity)
                .endChild()
                .beginChild(new RingParaData(4.2f, 4.28f, 64))
                .withColor(ringColor)
                .withIntensity(intensity)
                .endChild()
                .beginChild(new CurvyStarParaData(4.0f, 6, 2.0f, 0.0f, 0.05f))
                .withColor(starColor)
                .withIntensity(intensity)
                .endChild()
                .beginChild(new RingParaData(8.0f, 8.1f, 64))
                .withColor(ringColor)
                .withIntensity(intensity)
                .endChild()
                .beginChild(new RingParaData(8.2f, 8.28f, 64))
                .withColor(ringColor)
                .withIntensity(intensity)
                .endChild()
                .beginChild(new CurvyStarParaData(5.0f, 6, -2.6f, 0.0f, 0.05f))
                .withColor(starColor)
                .withIntensity(intensity)
                .endChild()
                .build();
    }
}
