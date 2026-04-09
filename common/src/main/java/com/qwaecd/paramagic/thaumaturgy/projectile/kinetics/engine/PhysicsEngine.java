package com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.engine;

import org.joml.Vector3d;

public final class PhysicsEngine {
    // m/s^2
    public static final double worldGravity = 0.32d;
    public static final double maxDt = 2.0d;

    public static void update(PhysicsState state, KineticsAccumulator accumulator, double deltaTime) {
        final double dt = clamp(deltaTime, 0.0d, maxDt);
        if (dt <= 0.0d) {
            return;
        }
        Vector3d velocity = state.getVelocity(new Vector3d());
        // v += a * dt
        velocity.add(accumulator.getTransientAcceleration(new Vector3d())
                .mul(dt)
        );
        velocity.add(accumulator.getDeltaVelocity(new Vector3d()));

        // F * dt = m * dv
        // dv = F * dt / m = m*g * dt / m = g * dt
        velocity.y -= worldGravity * dt * state.getGravityScale();

        double drag = clamp01(state.getDragCoefficient());
        if (drag > 0.0d) {
            velocity.mul(1.0d - drag);
        }
        double maxSpeed = state.getMaxSpeed();
        double speedCapOverride = accumulator.getSpeedCapOverride();
        if (Double.isFinite(speedCapOverride)) {
            maxSpeed = Math.min(maxSpeed, speedCapOverride);
        }
        if (Double.isFinite(maxSpeed) && maxSpeed >= 0.0d) {
            double speedSquared = velocity.lengthSquared();
            double maxSpeedSquared = maxSpeed * maxSpeed;
            if (speedSquared > maxSpeedSquared) {
                velocity.normalize(maxSpeed);
            }
        }
        state.setVelocity(velocity);
    }

    private static double clamp01(double value) {
        if (value < 0.0d) {
            return 0.0d;
        }
        return Math.min(value, 1.0d);
    }

    public static double clamp(double value, double min, double max) {
        if (value < min) {
            return min;
        }
        return Math.min(value, max);
    }
}
