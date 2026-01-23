package com.qwaecd.paramagic.feature.effect.explosion;

import com.qwaecd.paramagic.data.animation.property.AllAnimatableProperties;
import com.qwaecd.paramagic.data.animation.struct.AnimationBinding;
import com.qwaecd.paramagic.data.animation.struct.AnimationBindingConfig;
import com.qwaecd.paramagic.data.animation.struct.AnimatorData;
import com.qwaecd.paramagic.data.animation.util.TimelineBuilder;
import com.qwaecd.paramagic.data.para.struct.ParaComponentData;
import com.qwaecd.paramagic.data.para.struct.components.CurvyStarParaData;
import com.qwaecd.paramagic.data.para.struct.components.RingParaData;
import com.qwaecd.paramagic.data.para.util.ParaComponentBuilder;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class ExplosionParaNode {
    private final ParaComponentData paraComponentData;

    private ExplosionParaNode(String paraName) {
        paraComponentData = genComponentData(paraName);
    }

    public static ParaComponentData createParaData(String paraName) {
        return new ExplosionParaNode(paraName).get();
    }

    public static AnimationBindingConfig createAnim(String paraName) {
        List<AnimationBinding> animationBindingList = new ArrayList<>();

        {
            AnimatorData animatorData;
            TimelineBuilder timelineBuilder = new TimelineBuilder();
            timelineBuilder
                    .at(0.0f)
                    .keyframe(AllAnimatableProperties.ROTATION, new Quaternionf().rotateY((float)Math.toRadians(359)), true)
                    .keyframe(AllAnimatableProperties.SCALE, new Vector3f(0.0f), false)
                    .timeStep(0.5f)
                    .keyframe(AllAnimatableProperties.SCALE, new Vector3f(1.0f))
                    .at(5.0f)
                    .keyframe(AllAnimatableProperties.ROTATION, new Quaternionf().rotateY((float)Math.toRadians(180)))
                    .at(10.0f)
                    .keyframe(AllAnimatableProperties.ROTATION, new Quaternionf().rotateY((float)Math.toRadians(0)));

            animatorData = timelineBuilder.build();
            AnimationBinding bindingData = new AnimationBinding(
                    paraName,
                    null,
                    animatorData
            );
            animationBindingList.add(bindingData);
        }
        return new AnimationBindingConfig(animationBindingList);
    }

    ParaComponentData genComponentData(String paraName) {
        final float intensity = 0.4f;
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

    private ParaComponentData get() {
        return this.paraComponentData;
    }
}
