package com.qwaecd.paramagic.tools.anim;

import lombok.experimental.UtilityClass;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

@UtilityClass
public final class Interpolation {
    /**
     * Performs linear interpolation (lerp) between two float values.
     */
    public static float linear(float start, float end, float alpha) {
        return start + alpha * (end - start);
    }

    /**
     * Performs linear interpolation (lerp) between two Vector3f values.
     */
    public static Vector3f linear(Vector3f start, Vector3f end, float alpha, Vector3f dest) {
        return start.lerp(end, alpha, dest);
    }

    /**
     * Performs linear interpolation (lerp) between two Vector4f values.
     */
    public static Vector4f linear(Vector4f start, Vector4f end, float alpha, Vector4f dest) {
        return start.lerp(end, alpha, dest);
    }

    public static Vector2f linear(Vector2f start, Vector2f end, float alpha) {
        return start.lerp(end, alpha, new Vector2f());
    }

    /**
     * Performs spherical linear interpolation (slerp) between two Quaternionf values.
     * This is the correct way to interpolate rotations.
     */
    public static Quaternionf slerp(Quaternionf start, Quaternionf end, float alpha, Quaternionf dest) {
        return start.slerp(end, alpha, dest);
    }
}
