package com.qwaecd.paramagic.client.animation;

import lombok.Getter;

import java.util.List;

public class AnimationTrack {
    @Getter
    private final List<Keyframe<?>> keyframeList;
    @Getter
    private final PropertyAccessor<?> targetProperty;
    @Getter
    float duration = 0.0f;
    @Getter
    private final boolean loop;

    public AnimationTrack(PropertyAccessor<?> targetProperty, List<Keyframe<?>> keyframes, boolean loop) {
        this.keyframeList = keyframes;
        this.targetProperty = targetProperty;
        this.loop = loop;
    }
    public AnimationTrack(PropertyAccessor<?> targetProperty, List<Keyframe<?>> keyframes) {
        this.keyframeList = keyframes;
        this.targetProperty = targetProperty;
        this.loop = false;
    }

    public void apply(float time) {
    }
}
