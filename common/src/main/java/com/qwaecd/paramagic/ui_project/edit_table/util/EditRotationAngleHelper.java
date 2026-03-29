package com.qwaecd.paramagic.ui_project.edit_table.util;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nonnull;

public final class EditRotationAngleHelper {
    private static final float GIMBAL_LOCK_EPSILON = 1.0E-5f;

    private EditRotationAngleHelper() {
    }

    /**
     * Converts UI Euler angles in degrees to a quaternion using the same Y-X-Z order
     * as {@code Transform.setRotationDegrees()}.
     */
    @Nonnull
    public static Quaternionf eulerDegreesToQuaternion(float xDeg, float yDeg, float zDeg, @Nonnull Quaternionf dest) {
        return dest.identity()
                .rotateYXZ(
                        (float) Math.toRadians(yDeg),
                        (float) Math.toRadians(xDeg),
                        (float) Math.toRadians(zDeg)
                )
                .normalize();
    }

    /**
     * Extracts UI Euler angles in degrees from a quaternion using the Y-X-Z order
     * used by {@code Transform.setRotationDegrees()}.
     */
    @Nonnull
    public static Vector3f quaternionToEulerDegrees(@Nonnull Quaternionf rotation, @Nonnull Vector3f dest) {
        Quaternionf normalized = new Quaternionf(rotation).normalize();

        float x = normalized.x;
        float y = normalized.y;
        float z = normalized.z;
        float w = normalized.w;

        float xx = x * x;
        float yy = y * y;
        float zz = z * z;
        float xy = x * y;
        float xz = x * z;
        float yz = y * z;
        float xw = x * w;
        float yw = y * w;
        float zw = z * w;

        float m00 = 1.0f - 2.0f * (yy + zz);
        float m01 = 2.0f * (xy - zw);
        float m02 = 2.0f * (xz + yw);
        float m10 = 2.0f * (xy + zw);
        float m11 = 1.0f - 2.0f * (xx + zz);
        float m12 = 2.0f * (yz - xw);
        float m22 = 1.0f - 2.0f * (xx + yy);

        float xRad = (float) Math.asin(clamp(-m12, -1.0f, 1.0f));
        float cosX = (float) Math.cos(xRad);

        float yRad;
        float zRad;
        if (Math.abs(cosX) > GIMBAL_LOCK_EPSILON) {
            yRad = (float) Math.atan2(m02, m22);
            zRad = (float) Math.atan2(m10, m11);
        } else {
            zRad = 0.0f;
            yRad = xRad >= 0.0f
                    ? (float) Math.atan2(m01, m00)
                    : (float) Math.atan2(-m01, m00);
        }

        return dest.set(
                (float) Math.toDegrees(xRad),
                (float) Math.toDegrees(yRad),
                (float) Math.toDegrees(zRad)
        );
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}
