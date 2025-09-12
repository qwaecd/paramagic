package com.qwaecd.paramagic.data.animation;

import com.qwaecd.paramagic.client.animation.Animator;
import lombok.Getter;

import java.util.List;

/**
 * A data structure representing an animator.
 * <p>
 * 一个动画器对应的根数据结构。
 * @see Animator
 */
public class AnimationData {
    public static final String schemaVersion = "1.0";
    @Getter
    private final List<AnimationComponentData> animationComponentDataList;

    public AnimationData(List<AnimationComponentData> animations) {
        this.animationComponentDataList = animations;
    }
}
