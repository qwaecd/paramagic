package com.qwaecd.paramagic.client.animation;

import com.qwaecd.paramagic.tools.Interpolation;
import lombok.Getter;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;

public class AnimationTrack {
    @Getter
    private final List<Keyframe<?>> keyframeList;
    @Getter
    private final PropertyAccessor<Object> targetProperty;
    @Getter
    float duration = 0.0f;
    private final boolean loop;

    private final TempValue tempValue;

    @SuppressWarnings("unchecked")
    public AnimationTrack(PropertyAccessor<?> targetProperty, List<Keyframe<?>> keyframes, boolean loop) {
        keyframes.sort((k1, k2) -> Float.compare(k1.time(), k2.time()));

        this.keyframeList = keyframes;
        this.targetProperty = (PropertyAccessor<Object>) targetProperty;
        this.loop = loop;

        if (!keyframes.isEmpty()) {
            this.duration = keyframes.get(keyframes.size() - 1).time();
        }
        this.tempValue = new TempValue();
    }

    public AnimationTrack(PropertyAccessor<?> targetProperty, List<Keyframe<?>> keyframes) {
        this(targetProperty, keyframes, false);
    }

    public void apply(float time) {
        if (keyframeList.isEmpty()) {
            return;
        }

        float effectiveTime;
        if (this.loop) {
            effectiveTime = time % this.duration;
        } else {
            effectiveTime = Math.min(time, this.duration);
        }

        // --- 查找当前时间点前后的关键帧 ---
        Keyframe<?> prevFrame = keyframeList.get(0);
        Keyframe<?> nextFrame = null;

        for (int i = 1; i < keyframeList.size(); i++) {
            nextFrame = keyframeList.get(i);
            if (nextFrame.time() >= effectiveTime) {
                break;
            }
            prevFrame = nextFrame;
        }

        // 如果没有下一个关键帧（动画结束或只有一个关键帧），则直接应用前一个关键帧的值
        if (nextFrame == null) {
            targetProperty.setValue(prevFrame.value());
            return;
        }

        // --- 计算插值 ---
        float frameDuration = nextFrame.time() - prevFrame.time();
        float alpha = (frameDuration == 0) ? 0 : (effectiveTime - prevFrame.time()) / frameDuration;

        // 根据插值类型调整 alpha
        if ("ease-in-out".equalsIgnoreCase(prevFrame.interpolation())) {
            alpha = Interpolation.easeInOut(alpha);
        }
        // 可以添加更多 else if 来支持其他插值类型

        // --- 应用插值后的值 ---
        Object startValue = prevFrame.value();
        Object endValue = nextFrame.value();

        if (startValue instanceof Float f1 && endValue instanceof Float f2) {
            targetProperty.setValue(Interpolation.lerp(f1, f2, alpha));
        } else if (startValue instanceof Vector3f v1 && endValue instanceof Vector3f v2) {
            targetProperty.setValue(Interpolation.lerp(v1, v2, alpha, this.tempValue.vct3));
        } else if (startValue instanceof Quaternionf q1 && endValue instanceof Quaternionf q2) {
            targetProperty.setValue(Interpolation.slerp(q1, q2, alpha, this.tempValue.quat));
        } else if (startValue instanceof Vector4f v1 && endValue instanceof Vector4f v2) {
            targetProperty.setValue(Interpolation.lerp(v1, v2, alpha, this.tempValue.vct4));
        } else {
            // 对于不支持插值的类型（如布尔值、整数），或者 "step" 插值，直接使用前一个关键帧的值
            targetProperty.setValue(startValue);
        }
    }

    @SuppressWarnings({"LombokGetterMayBeUsed", "RedundantSuppression"})
    public boolean isLoop() {
        return this.loop;
    }

    public boolean isFinished(float time) {
        if (this.isLoop()) {
            return false;
        }
        return time >= this.duration;
    }

    @SuppressWarnings("InnerClassMayBeStatic")
    private class TempValue {
        Vector3f vct3 = new Vector3f();
        Vector4f vct4 = new Vector4f();
        Quaternionf quat = new Quaternionf();
    }
}
