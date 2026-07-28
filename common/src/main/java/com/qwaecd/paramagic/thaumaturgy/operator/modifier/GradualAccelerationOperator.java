package com.qwaecd.paramagic.thaumaturgy.operator.modifier;

import com.qwaecd.paramagic.thaumaturgy.ProjectileEntity;
import com.qwaecd.paramagic.thaumaturgy.operator.OperatorRegistration;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.PhysicsProvider;
import com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.engine.KineticsAccumulator;
import com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.engine.PhysicsMath;
import com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.runtime.ProjectileRuntimeModifier;
import com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.runtime.ProjectileRuntimeModifierContext;
import com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.runtime.ProjectileRuntimeModifierHost;
import com.qwaecd.paramagic.thaumaturgy.runtime.ParaContext;
import org.joml.Vector3d;

public class GradualAccelerationOperator extends ModifierOperator {
    private static final float FORWARD_ACCELERATION = 8.0E-3f;
    private static final float MIN_TARGET_SPEED = 1.2f;
    private static final float TARGET_SPEED_MULTIPLIER = 1.5f;

    public GradualAccelerationOperator(ParaOpId id) {
        super(id, OperatorRegistration.itemOf("gradual_acceleration_operator"));
    }

    @Override
    public boolean apply(ParaContext context) {
        context.forEachProjectiles((projectile, index) -> this.tryApplyGradualAcceleration(projectile));
        return true;
    }

    @Override
    public void rebuild(ProjectileEntity entity) {
        this.tryApplyGradualAcceleration(entity);
    }

    private void tryApplyGradualAcceleration(ProjectileEntity projectile) {
        if (!(projectile instanceof ProjectileRuntimeModifierHost host)) {
            return;
        }
        PhysicsProvider physics = projectile.physics();
        physics.setVelocity(physics.getVelocity(new Vector3d()).mul(0.5d));
        host.addRuntimeModifier(new Modifier());
    }

    static final class Modifier implements ProjectileRuntimeModifier {
        @Override
        public void applyTick(ProjectileRuntimeModifierContext context, KineticsAccumulator accumulator) {
            Vector3d velocity = context.getVelocity(new Vector3d());
            double speed = velocity.length();
            if (!Double.isFinite(speed)) {
                return;
            }
            double targetSpeed = Math.max(MIN_TARGET_SPEED, speed * TARGET_SPEED_MULTIPLIER);
            PhysicsProvider physics = context.getProjectile().physics();
            double currentLimit = physics.getMaxSpeed();
            if (Double.isInfinite(currentLimit) && currentLimit < targetSpeed) {
                accumulator.limitSpeedCap(targetSpeed);
            }
            if (speed < targetSpeed) {
                Vector3d acceleration = new Vector3d();
                if (!PhysicsMath.tryNormalize(velocity, FORWARD_ACCELERATION, acceleration)) {
                    return;
                }
                accumulator.addTransientAcceleration(acceleration);
            }
        }
    }
}
