package com.qwaecd.paramagic.tools.anim;

import lombok.experimental.UtilityClass;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

@UtilityClass
public final class Interpolation {
    /**
     * Performs linear interpolation (lerp) between two float values.
     */
    public static float lerp(float start, float end, float alpha) {
        return start + alpha * (end - start);
    }

    /**
     * Performs linear interpolation (lerp) between two Vector3f values.
     */
    public static Vector3f lerp(Vector3f start, Vector3f end, float alpha, Vector3f dest) {
        return start.lerp(end, alpha, dest);
    }

    /**
     * Performs linear interpolation (lerp) between two Vector4f values.
     */
    public static Vector4f lerp(Vector4f start, Vector4f end, float alpha, Vector4f dest) {
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
