package com.qwaecd.paramagic.thaumaturgy.kinetics;

import com.qwaecd.paramagic.thaumaturgy.kinetics.runtime.ProjectileKineticsAccumulator;
import org.joml.Vector3f;

public final class ProjectileKineticsUpdater {
    private static final float BASE_GRAVITY = 0.05f;
    private static final ProjectileKineticsAccumulator EMPTY_ACCUMULATOR = new ProjectileKineticsAccumulator();

    private ProjectileKineticsUpdater() {}

    public static void step(ProjectileKineticsState state) {
        step(state, EMPTY_ACCUMULATOR);
    }

    public static void step(ProjectileKineticsState state, ProjectileKineticsAccumulator accumulator) {
        Vector3f velocity = state.getVelocity();
        Vector3f persistentAcceleration = state.getPersistentAcceleration();
        velocity.add(persistentAcceleration);
        velocity.add(accumulator.getTransientAcceleration(new Vector3f()));
        velocity.add(accumulator.getDeltaVelocity(new Vector3f()));

        velocity.y -= BASE_GRAVITY * state.getGravityScale();

        float damping = clamp01(state.getLinearDamping());
        if (damping > 0.0f) {
            velocity.mul(1.0f - damping);
        }

        float maxSpeed = state.getMaxSpeed();
        float speedCapOverride = accumulator.getSpeedCapOverride();
        if (Float.isFinite(speedCapOverride)) {
            maxSpeed = Math.min(maxSpeed, speedCapOverride);
        }
        if (Float.isFinite(maxSpeed) && maxSpeed >= 0.0f) {
            float speedSquared = velocity.lengthSquared();
            float maxSpeedSquared = maxSpeed * maxSpeed;
            if (speedSquared > maxSpeedSquared) {
                velocity.normalize(maxSpeed);
            }
        }

        state.setVelocity(velocity.x, velocity.y, velocity.z);
    }

    private static float clamp01(float value) {
        if (value < 0.0f) {
            return 0.0f;
        }
        return Math.min(value, 1.0f);
    }
}
