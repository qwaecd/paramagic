package com.qwaecd.paramagic.tools.anim;

/**
 * 缓动函数，定义了动画进度 alpha（通常在 0 到 1 之间）的运动轨迹。
 */
@FunctionalInterface
public interface EasingFunction {
    float ease(float alpha);

    /**
     * A simple ease-in-out function.
     * For a given alpha [0, 1], it returns a smoothed alpha.
     */
    static float easeInOut(float alpha) {
        return alpha * alpha * (3.0f - 2.0f * alpha);
    }

    EasingFunction linear = alpha -> alpha;

    /**
     * Smoothstep 插值。
     *
     * <p>f(t) = 3t^2 - 2t^3
     * <p>特点：起点与终点的一阶导数为 0，过渡更平滑。
     */
    EasingFunction smoothstep = alpha -> alpha * alpha * (3f - 2f * alpha);

    /**
     * 二次 Ease-In（慢开始 → 加速）。
     *
     * <p>f(t) = t^2
     */
    EasingFunction easeInQuad = alpha -> alpha * alpha;


    /**
     * 二次 Ease-Out（减速 → 慢结束）。
     *
     * <p>f(t) = t(2 - t)
     */
    EasingFunction easeOutQuad = alpha -> alpha * (2f - alpha);

    /**
     * 二次 Ease-In-Out。
     *
     * <p>前半段加速，后半段减速。
     */
    EasingFunction easeInOutQuad = alpha -> {
        if (alpha < 0.5f) {
            return  2f * alpha * alpha;
        }
        return -1f + (4f - 2f * alpha) * alpha;
    };

    /**
     * 三次 Ease-In。
     *
     * <p>f(t) = t^3
     */
    EasingFunction easeInCubic = alpha -> alpha * alpha * alpha;

    /**
     * 三次 Ease-Out。
     *
     * <p>f(t) = 1 - (1 - t)^3
     */
    EasingFunction easeOutCubic = alpha -> {
        final float inv = 1f - alpha;
        return 1f - inv * inv * inv;
    };

    /**
     * 三次 Ease-In-Out。
     */
    EasingFunction easeInOutCubic = alpha -> {
        if (alpha < 0.5f) {
            return 4f * alpha * alpha * alpha;
        }
        final float f = 2f * alpha - 2f;
        return 1f + 0.5f * f * f * f;
    };

    /**
     * 正弦 Ease-In。
     *
     * <p>启动柔和。
     */
    EasingFunction easeInSine = alpha -> 1f - (float) Math.cos(alpha * Math.PI * 0.5f);

    /**
     * 正弦 Ease-Out。
     *
     * <p>结束柔和。
     */
    EasingFunction easeOutSine = alpha -> (float) Math.sin(alpha * Math.PI * 0.5f);

    /**
     * 正弦 Ease-In-Out。
     */
    EasingFunction easeInOutSine = alpha -> -0.5f * ((float) Math.cos(Math.PI * alpha) - 1f);
}
