package com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.engine;

import org.joml.Vector3d;

public final class PhysicsEngine {
    /**
     * Gravity acceleration in blocks/tick^2.
     */
    public static final double worldGravity = 0.00943d;

    public static void update(PhysicsState state, KineticsAccumulator accumulator) {
        Vector3d velocity = state.getVelocity(new Vector3d());
        if (!PhysicsMath.isFinite(velocity)) {
            velocity.zero();
            state.setVelocity(velocity);
            return;
        }
        // Tick-driven update (v in blocks/tick, a in blocks/tick^2):
        // v += a
        velocity.add(accumulator.getTransientAcceleration(new Vector3d()));
        velocity.add(accumulator.getDeltaVelocity(new Vector3d()));

        velocity.y -= worldGravity * state.getGravityScale();

        double drag = clamp01(state.getDragCoefficient());
        if (drag > 0.0d) {
            velocity.mul(1.0d - drag);
        }
        if (!PhysicsMath.isFinite(velocity)) {
            velocity.zero();
            state.setVelocity(velocity);
            return;
        }
        double maxSpeed = state.getMaxSpeed();
        double speedCapOverride = accumulator.getSpeedCapOverride();
        if (Double.isFinite(speedCapOverride)) {
            maxSpeed = Math.min(maxSpeed, speedCapOverride);
        }
        if (Double.isFinite(maxSpeed) && maxSpeed >= 0.0d) {
            double speedSquared = velocity.lengthSquared();
            double maxSpeedSquared = maxSpeed * maxSpeed;
            if (Double.isFinite(speedSquared) && Double.isFinite(maxSpeedSquared) && speedSquared > maxSpeedSquared) {
                Vector3d limitedVelocity = new Vector3d();
                if (PhysicsMath.tryNormalize(velocity, maxSpeed, limitedVelocity)) {
                    velocity.set(limitedVelocity);
                } else {
                    velocity.zero();
                }
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
}
