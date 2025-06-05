package com.qwaecd.paramagic.api.animation;

import java.util.function.Function;

/**
 * 插值动画
 */
public enum EasingType {
    /**
     * 线性缓动函数，输入值直接作为输出值，无加速或减速效果。
     * 公式: f(t) = t
     * 适用于需要匀速变化的动画场景。
     */
    LINEAR(t -> t),
    
    /**
     * 渐进缓动函数，从缓慢开始逐渐加速。
     * 公式: f(t) = t²
     * 适用于元素入场时的自然加速效果。
     */
    EASE_IN(t -> t * t),
    
    /**
     * 渐出缓动函数，从快速开始逐渐减速。
     * 公式: f(t) = 1 - (1 - t)²
     * 适用于元素退出时的平滑减速效果。
     */
    EASE_OUT(t -> 1 - (1 - t) * (1 - t)),
    
    /**
     * 渐进渐出缓动函数，先加速后减速。
     * 公式: 
     * t < 0.5: 2t²
     * t ≥ 0.5: 1 - 2(1-t)²
     * 适用于需要平滑过渡的完整动画周期。
     */
    EASE_IN_OUT(t -> t < 0.5f ? 2 * t * t : 1 - 2 * (1 - t) * (1 - t)),
    
    /**
     * 弹跳缓动函数，模拟物体落地反弹效果。
     * 四段分段函数实现跳跃衰减效果。
     * 适用于掉落/弹起的物理模拟动画。
     */
    BOUNCE(t -> {
        if (t < 1/2.75f) return 7.5625f * t * t;
        else if (t < 2/2.75f) return 7.5625f * (t -= 1.5f/2.75f) * t + 0.75f;
        else if (t < 2.5f/2.75f) return 7.5625f * (t -= 2.25f/2.75f) * t + 0.9375f;
        else return 7.5625f * (t -= 2.625f/2.75f) * t + 0.984375f;
    }),
    
    /**
     * 弹性缓动函数，模拟弹簧振荡效果。
     * 指数衰减正弦波函数实现弹性振荡。
     * 适用于需要回弹反馈的交互动画。
     */
    ELASTIC(t -> {
        if (t == 0 || t == 1) return t;
        return (float)(-Math.pow(2, 10 * (t - 1)) * Math.sin((t - 1.1) * 5 * Math.PI));
    });

    private final Function<Float, Float> function;

    EasingType(Function<Float, Float> function) {
        this.function = function;
    }

    public float apply(float t) {
        return function.apply(Math.max(0, Math.min(1, t)));
    }
}