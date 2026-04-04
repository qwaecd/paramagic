package com.qwaecd.paramagic.thaumaturgy.operator.content;

import com.qwaecd.paramagic.thaumaturgy.ProjectileEntity;
import com.qwaecd.paramagic.thaumaturgy.kinetics.ProjectilePersistentAccelerationMutable;
import com.qwaecd.paramagic.thaumaturgy.kinetics.ProjectileSpeedLimitMutable;
import com.qwaecd.paramagic.thaumaturgy.kinetics.ProjectileVelocityMutable;
import com.qwaecd.paramagic.thaumaturgy.operator.OperatorType;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.thaumaturgy.runtime.ParaContext;
import com.qwaecd.paramagic.tools.ModRL;
import com.qwaecd.paramagic.world.item.ModItems;
import org.joml.Vector3f;

public class GradualAccelerationOperator extends ProjectileOperator {
    public static final ParaOpId OP_ID = ParaOpId.of(
            ModRL.inModSpace("gradual_acceleration_operator"),
            new ParaOpId.Properties(OperatorType.MODIFIER, 0.2f, 0.6f)
    );

    private static final float FORWARD_ACCELERATION = 0.008f;
    private static final float MIN_TARGET_SPEED = 3.2f;
    private static final float TARGET_SPEED_MULTIPLIER = 1.5f;

    public GradualAccelerationOperator() {
        super(OP_ID, ModItems.GRADUAL_ACCELERATION_OPERATOR);
    }

    @Override
    public boolean apply(ParaContext context) {
        context.forEachProjectiles((projectile, index) -> this.tryApplyGradualAcceleration(projectile));
        return true;
    }

    private void tryApplyGradualAcceleration(ProjectileEntity projectile) {
        if (!(projectile instanceof ProjectileVelocityMutable velocityMutable)) {
            return;
        }
        if (!(projectile instanceof ProjectilePersistentAccelerationMutable accelerationMutable)) {
            return;
        }

        Vector3f velocity = velocityMutable.getVelocity();
        float speed = velocity.length();
        if (speed <= 1.0e-4f) {
            return;
        }
        velocityMutable.setVelocity(velocity.x * 0.5f, velocity.y * 0.5f, velocity.z * 0.5f);

        Vector3f acceleration = velocity.normalize(new Vector3f()).mul(FORWARD_ACCELERATION);
        accelerationMutable.addPersistentAcceleration(acceleration);

        if (projectile instanceof ProjectileSpeedLimitMutable speedLimitMutable) {
            float targetSpeed = Math.max(MIN_TARGET_SPEED, speed * TARGET_SPEED_MULTIPLIER);
            float currentLimit = speedLimitMutable.getMaxSpeed();
            if (!Float.isFinite(currentLimit) || currentLimit < targetSpeed) {
                speedLimitMutable.setMaxSpeed(targetSpeed);
            }
        }
    }
}
