package com.qwaecd.paramagic.tools.anim;

import lombok.experimental.UtilityClass;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

@UtilityClass
public final class Interpolation {
    /**
     * Smoothstep 插值。
     *
     * <p>f(t) = 3t^2 - 2t^3
     * <p>特点：起点与终点的一阶导数为 0，过渡更平滑。
     */
    public static float smoothstep(float start, float end, float alpha) {
        float t = alpha * alpha * (3f - 2f * alpha);
        return start + t * (end - start);
    }

    /**
     * 二次 Ease-In（慢开始 → 加速）。
     *
     * <p>f(t) = t^2
     */
    public static float easeInQuad(float start, float end, float alpha) {
        float t = alpha * alpha;
        return start + t * (end - start);
    }

    /**
     * 二次 Ease-Out（减速 → 慢结束）。
     *
     * <p>f(t) = t(2 - t)
     */
    public static float easeOutQuad(float start, float end, float alpha) {
        float t = alpha * (2f - alpha);
        return start + t * (end - start);
    }

    /**
     * 二次 Ease-In-Out。
     *
     * <p>前半段加速，后半段减速。
     */
    public static float easeInOutQuad(float start, float end, float alpha) {
        float t;
        if (alpha < 0.5f) {
            t = 2f * alpha * alpha;
        } else {
            t = -1f + (4f - 2f * alpha) * alpha;
        }
        return start + t * (end - start);
    }

    /**
     * 三次 Ease-In。
     *
     * <p>f(t) = t^3
     */
    public static float easeInCubic(float start, float end, float alpha) {
        float t = alpha * alpha * alpha;
        return start + t * (end - start);
    }

    /**
     * 三次 Ease-Out。
     *
     * <p>f(t) = 1 - (1 - t)^3
     */
    public static float easeOutCubic(float start, float end, float alpha) {
        float inv = 1f - alpha;
        float t = 1f - inv * inv * inv;
        return start + t * (end - start);
    }

    /**
     * 三次 Ease-In-Out。
     */
    public static float easeInOutCubic(float start, float end, float alpha) {
        float t;
        if (alpha < 0.5f) {
            t = 4f * alpha * alpha * alpha;
        } else {
            float f = 2f * alpha - 2f;
            t = 1f + 0.5f * f * f * f;
        }
        return start + t * (end - start);
    }

    /**
     * 正弦 Ease-In。
     *
     * <p>启动柔和。
     */
    public static float easeInSine(float start, float end, float alpha) {
        float t = 1f - (float) Math.cos(alpha * Math.PI * 0.5f);
        return start + t * (end - start);
    }

    /**
     * 正弦 Ease-Out。
     *
     * <p>结束柔和。
     */
    public static float easeOutSine(float start, float end, float alpha) {
        float t = (float) Math.sin(alpha * Math.PI * 0.5f);
        return start + t * (end - start);
    }

    /**
     * 正弦 Ease-In-Out。
     */
    public static float easeInOutSine(float start, float end, float alpha) {
        float t = -0.5f * ((float) Math.cos(Math.PI * alpha) - 1f);
        return start + t * (end - start);
    }

    /**
     * Performs linear interpolation (lerp) between two float values.
     */
    public static float liner(float start, float end, float alpha) {
        return start + alpha * (end - start);
    }

    /**
     * Performs linear interpolation (lerp) between two Vector3f values.
     */
    public static Vector3f liner(Vector3f start, Vector3f end, float alpha, Vector3f dest) {
        return start.lerp(end, alpha, dest);
    }

    /**
     * Performs linear interpolation (lerp) between two Vector4f values.
     */
    public static Vector4f liner(Vector4f start, Vector4f end, float alpha, Vector4f dest) {
        return start.lerp(end, alpha, dest);
    }

    /**
     * Performs spherical linear interpolation (slerp) between two Quaternionf values.
     * This is the correct way to interpolate rotations.
     */
    public static Quaternionf slerp(Quaternionf start, Quaternionf end, float alpha, Quaternionf dest) {
        return start.slerp(end, alpha, dest);
    }

    /**
     * A simple ease-in-out function.
     * For a given alpha [0, 1], it returns a smoothed alpha.
     */
    public static float easeInOut(float alpha) {
        return alpha * alpha * (3.0f - 2.0f * alpha);
    }
}
