package com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.engine;

import org.joml.Vector3d;

public final class PhysicsMath {
    private static final double NORMALIZE_EPSILON = 1.0e-12d;

    private PhysicsMath() {}

    public static boolean isFinite(double x, double y, double z) {
        return Double.isFinite(x) && Double.isFinite(y) && Double.isFinite(z);
    }

    public static boolean isFinite(Vector3d vector) {
        return isFinite(vector.x, vector.y, vector.z);
    }

    public static boolean tryNormalize(Vector3d source, double magnitude, Vector3d dest) {
        return tryNormalize(source.x, source.y, source.z, magnitude, dest);
    }

    public static boolean tryNormalize(double x, double y, double z, double magnitude, Vector3d dest) {
        if (!Double.isFinite(magnitude) || !isFinite(x, y, z)) {
            return false;
        }
        double lengthSqr = x * x + y * y + z * z;
        if (!Double.isFinite(lengthSqr) || lengthSqr <= NORMALIZE_EPSILON) {
            return false;
        }
        double scale = magnitude / Math.sqrt(lengthSqr);
        if (!Double.isFinite(scale)) {
            return false;
        }
        dest.set(x * scale, y * scale, z * scale);
        return isFinite(dest);
    }
}
