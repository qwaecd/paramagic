package com.qwaecd.paramagic.thaumaturgy.operator.modifier;

import com.qwaecd.paramagic.thaumaturgy.ProjectileEntity;
import com.qwaecd.paramagic.thaumaturgy.operator.OperatorType;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.PhysicsProvider;
import com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.engine.KineticsAccumulator;
import com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.runtime.ProjectileRuntimeModifier;
import com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.runtime.ProjectileRuntimeModifierContext;
import com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.runtime.ProjectileRuntimeModifierHost;
import com.qwaecd.paramagic.thaumaturgy.runtime.ParaContext;
import com.qwaecd.paramagic.tools.ModRL;
import com.qwaecd.paramagic.world.item.ModItems;
import org.joml.Vector3d;

public class GradualAccelerationOperator extends ModifierOperator {
    public static final ParaOpId OP_ID = ParaOpId.of(
            ModRL.inModSpace("gradual_acceleration_operator"),
            new ParaOpId.Properties(OperatorType.MODIFIER, 0.06f, 0.03f)
    );

    private static final float FORWARD_ACCELERATION = 0.08f;
    private static final float MIN_TARGET_SPEED = 1.2f;
    private static final float TARGET_SPEED_MULTIPLIER = 1.5f;

    public GradualAccelerationOperator() {
        super(OP_ID, ModItems.GRADUAL_ACCELERATION_OPERATOR);
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
            double targetSpeed = Math.max(MIN_TARGET_SPEED, speed * TARGET_SPEED_MULTIPLIER);
            PhysicsProvider physics = context.getProjectile().physics();
            double currentLimit = physics.getMaxSpeed();
            if (Double.isInfinite(currentLimit) && currentLimit < targetSpeed) {
                accumulator.limitSpeedCap(targetSpeed);
            }
            if (speed < targetSpeed) {
                velocity.normalize(FORWARD_ACCELERATION);
                accumulator.addTransientAcceleration(velocity);
            }
        }
    }
}
