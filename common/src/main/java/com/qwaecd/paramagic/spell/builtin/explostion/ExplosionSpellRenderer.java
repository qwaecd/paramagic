package com.qwaecd.paramagic.spell.builtin.explostion;

import com.qwaecd.paramagic.assembler.ParaComposer;
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
import com.qwaecd.paramagic.spell.builtin.client.SpellRenderer;
import com.qwaecd.paramagic.spell.config.CircleAssets;
import com.qwaecd.paramagic.spell.phase.SpellPhaseType;
import com.qwaecd.paramagic.spell.session.client.ClientSessionView;
import com.qwaecd.paramagic.spell.core.store.AllSessionDataKeys;
import com.qwaecd.paramagic.spell.core.store.SessionDataStore;
import com.qwaecd.paramagic.spell.core.store.SessionDataValue;
import com.qwaecd.paramagic.spell.util.transform.BillboardFunction;
import com.qwaecd.paramagic.spell.view.CasterTransformSource;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ExplosionSpellRenderer extends SpellRenderer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExplosionSpellRenderer.class);

    private MagicCircle remoteCircle;

    private final AroundPlayerCircle aroundPlayerCircle = new AroundPlayerCircle();

    @Override
    public void onPhaseChanged(ClientSessionView session, SpellPhaseType oldPhase, SpellPhaseType currentPhase) {
        if (currentPhase == SpellPhaseType.CASTING) {
            this.aroundPlayerCircle.build(session.casterSource());
        }
        if (currentPhase == SpellPhaseType.CHANNELING) {
            this.createRemoteCircle(session.getDataStore());
        }
    }

    private void createRemoteCircle(SessionDataStore dataStore) {
        if (this.remoteCircle != null) {
            return;
        }

        try {
            SessionDataValue<Vector3f> dataValue = dataStore.getValue(AllSessionDataKeys.firstPosition);
            if (dataValue == null) {
                return;
            }

            Vector3f pos = dataValue.value;
            this.remoteCircle = ParaComposer.assemble(RemoteCircleData.create());

            this.remoteCircle.getTransform().setPosition(pos.x, pos.y + 0.01f, pos.z);
        } catch (Exception e) {
            LOGGER.error("Failed to create {} spell visual: ", ExplosionSpell.SPELL_ID, e);
            return;
        }
        MagicCircleManager.getInstance().addCircle(this.remoteCircle);
    }

    @Override
    public void onSessionClose() {
        if (this.remoteCircle != null) {
            this.remoteCircle.requestDestroy();
            this.remoteCircle = null;
        }
        this.aroundPlayerCircle.close();
    }

    @Override
    public void onInterrupt() {
        if (this.remoteCircle != null) {
            this.remoteCircle.requestDestroy();
            this.remoteCircle = null;
        }
        this.aroundPlayerCircle.close();
    }

    static class AroundPlayerCircle {
        // T1: front/under 法阵从无到有的过渡结束时刻
        private static final float T1 = 0.8f;
        // T2: front 的中层图元完成显现的时刻
        private static final float T2 = T1 + 0.1f;
        // frontLength: front 法阵位于玩家视线前方的距离
        private static final float FRONT_LENGTH = 3.0f;

        private static final Vector4f RING_COLOR = new Vector4f(0.6f, 0.25f, 0.25f, 1.0f);
        private static final Vector4f STAR_COLOR = new Vector4f(0.5f, 0.25f, 0.25f, 1.0f);

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
            final float intensity = 0.35f;
            final float centerTriangleLineWidth = 0.04f; // centerStar 的单个三角形线宽

            final float starRadius = 0.4f;
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


        private static AnimatorData buildScaleAnimator(float startTime, float endTime, float targetScale) {
            TimelineBuilder timelineBuilder = new TimelineBuilder();
            timelineBuilder.at(0.0f)
                    .keyframe(AllAnimatableProperties.SCALE, new Vector3f(0.0f));
            if (startTime > 0.0f) {
                timelineBuilder.at(startTime)
                        .keyframe(AllAnimatableProperties.SCALE, new Vector3f(0.0f));
            }
            timelineBuilder.at(endTime)
                    .keyframe(AllAnimatableProperties.SCALE, new Vector3f(targetScale));
            return timelineBuilder.build();
        }

        private static AnimatorData buildScaleRotationAnimator(float startTime, float endTime, float targetScale, float degreesPerSecond) {
            TimelineBuilder timelineBuilder = new TimelineBuilder();
            float fullRotationDuration = 359.0f / Math.abs(degreesPerSecond);
            float angleSign = Math.signum(degreesPerSecond);

            timelineBuilder.at(0.0f)
                    .keyframe(AllAnimatableProperties.ROTATION, new Quaternionf().identity(), true)
                    .keyframe(AllAnimatableProperties.SCALE, new Vector3f(0.0f));
            if (startTime > 0.0f) {
                timelineBuilder.at(startTime)
                        .keyframe(AllAnimatableProperties.SCALE, new Vector3f(0.0f));
            }
            timelineBuilder.at(endTime)
                    .keyframe(AllAnimatableProperties.SCALE, new Vector3f(targetScale));

            // Quaternion 插值会走最短路径，不能只给 0 -> 359/-359 两个端点，否则看起来几乎不转。
            timelineBuilder.at(fullRotationDuration * 0.25f)
                    .keyframe(AllAnimatableProperties.ROTATION, new Quaternionf().rotateY((float) Math.toRadians(90.0f * angleSign)));
            timelineBuilder.at(fullRotationDuration * 0.5f)
                    .keyframe(AllAnimatableProperties.ROTATION, new Quaternionf().rotateY((float) Math.toRadians(180.0f * angleSign)));
            timelineBuilder.at(fullRotationDuration * 0.75f)
                    .keyframe(AllAnimatableProperties.ROTATION, new Quaternionf().rotateY((float) Math.toRadians(270.0f * angleSign)));
            timelineBuilder.at(fullRotationDuration)
                    .keyframe(AllAnimatableProperties.ROTATION, new Quaternionf().rotateY((float) Math.toRadians(359.0f * angleSign)));
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
            RemoteCircleData helper = new RemoteCircleData();
            ParaComponentBuilder rootBuilder = new ParaComponentBuilder();

            helper.appendRing(rootBuilder, "ring0", 0.9f);
            helper.appendRing(rootBuilder, "ring1", 0.5f);
            helper.appendRing(rootBuilder, "ring2", 1.0f);
            helper.appendRing(rootBuilder, "ring3", 0.6f);
            helper.appendRing(rootBuilder, "ring4", 0.4f);

            ParaData paraData = new ParaData(rootBuilder);
            AnimationBindingConfig animConfig = helper.createAnimConfig();
            return new CircleAssets(paraData, animConfig);
        }

        private void appendRing(ParaComponentBuilder rootBuilder, String name, float targetScale) {
            rootBuilder.beginChild(genComponentData(name))
                    .withScale(targetScale)
                    .endChild();
        }

        private AnimationBindingConfig createAnimConfig() {
            final float moveDuration = 0.5f;
            final float startDelay = 0.4f;
            final float yOffset = 6.0f;

            List<AnimationBinding> bindings = new ArrayList<>();
            float currentStart = 0.0f;

            bindings.add(createBinding("ring0", currentStart, currentStart + moveDuration, 1.1f, yOffset * 0));
            currentStart += startDelay + moveDuration;

            bindings.add(createBinding("ring1", currentStart, currentStart + moveDuration, 0.5f, yOffset * 1));
            currentStart += startDelay + moveDuration;

            bindings.add(createBinding("ring2", currentStart, currentStart + moveDuration, 1.4f, yOffset * 2));
            currentStart += startDelay + moveDuration;

            bindings.add(createBinding("ring3", currentStart, currentStart + moveDuration, 0.6f, yOffset * 3));
            currentStart += startDelay + moveDuration;

            bindings.add(createBinding("ring4", currentStart, currentStart + moveDuration, 0.8f, yOffset * 4));
            return new AnimationBindingConfig(bindings);
        }

        private AnimationBinding createBinding(String nodeName, float startTime, float endTime, float targetScale, float yOffset) {
            AnimatorData animatorData = buildAnimator(startTime, endTime, targetScale, yOffset);
            return new AnimationBinding(nodeName, null, animatorData);
        }

        private AnimatorData buildAnimator(float startTime, float endTime, float targetScale, float yOffset) {
            TimelineBuilder timelineBuilder = new TimelineBuilder();
            timelineBuilder.at(0.0f)
                    .keyframe(AllAnimatableProperties.ROTATION, new Quaternionf().rotateY((float) Math.toRadians(359)), true)
                    .keyframe(AllAnimatableProperties.SCALE, new Vector3f(0.0f));
            if (startTime > 0.0f) {
                timelineBuilder.at(startTime)
                        .keyframe(AllAnimatableProperties.SCALE, new Vector3f(0.0f));
            }
            timelineBuilder.at(endTime)
                    .keyframe(AllAnimatableProperties.SCALE, new Vector3f(targetScale));

            if (yOffset != 0.0f) {
                timelineBuilder.at(0.0f)
                        .keyframe(AllAnimatableProperties.POSITION, new Vector3f(0.0f, 0.0f, 0.0f));
                if (startTime > 0.0f) {
                    timelineBuilder.at(startTime)
                            .keyframe(AllAnimatableProperties.POSITION, new Vector3f(0.0f, 0.0f, 0.0f));
                }
                timelineBuilder.at(endTime)
                        .keyframe(AllAnimatableProperties.POSITION, new Vector3f(0.0f, yOffset, 0.0f));
            }

            timelineBuilder.at(5.0f)
                    .keyframe(AllAnimatableProperties.ROTATION, new Quaternionf().rotateY((float) Math.toRadians(180)));
            timelineBuilder.at(10.0f)
                    .keyframe(AllAnimatableProperties.ROTATION, new Quaternionf().rotateY((float) Math.toRadians(0)));
            return timelineBuilder.build();
        }
    }

    static ParaComponentData genComponentData(String paraName) {
        final float intensity = 0.7f;
        final Vector4f ringColor = new Vector4f(1.1f, 0.6f, 0.5f, 0.8f);
        final Vector4f starColor = new Vector4f(1.0f, 0.5f, 0.5f, 0.8f);
        return new ParaComponentBuilder().withName(paraName)
                // 中心结构
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

                // 中层结构
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

                // 外层结构
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
