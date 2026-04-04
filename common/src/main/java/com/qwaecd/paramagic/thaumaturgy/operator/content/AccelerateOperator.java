package com.qwaecd.paramagic.thaumaturgy.operator.content;

import com.qwaecd.paramagic.thaumaturgy.ProjectileEntity;
import com.qwaecd.paramagic.thaumaturgy.kinetics.ProjectileSpeedLimitMutable;
import com.qwaecd.paramagic.thaumaturgy.kinetics.ProjectileVelocityMutable;
import com.qwaecd.paramagic.thaumaturgy.operator.OperatorType;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.thaumaturgy.runtime.ParaContext;
import com.qwaecd.paramagic.tools.ModRL;
import com.qwaecd.paramagic.world.item.ModItems;
import org.joml.Vector3f;

public class AccelerateOperator extends ProjectileOperator {
    public static final ParaOpId OP_ID = ParaOpId.of(
            ModRL.inModSpace("accelerate_operator"),
            new ParaOpId.Properties(OperatorType.MODIFIER, 0.04f, 0.03f)
    );

    private static final float IMPULSE = 0.8f;

    public AccelerateOperator() {
        super(OP_ID, ModItems.ACCELERATE_OPERATOR);
    }

    @Override
    public boolean apply(ParaContext context) {
        context.forEachProjectiles((projectile, index) -> this.tryAccelerate(projectile));
        return true;
    }

    private void tryAccelerate(ProjectileEntity projectile) {
        if (!(projectile instanceof ProjectileVelocityMutable velocityMutable)) {
            return;
        }
        Vector3f velocity = velocityMutable.getVelocity();
        float speed = velocity.length();
        if (speed <= 1.0e-4f) {
            return;
        }

        Vector3f impulse = velocity.normalize(new Vector3f()).mul(IMPULSE);
        velocityMutable.addVelocity(impulse);

        if (projectile instanceof ProjectileSpeedLimitMutable speedLimitMutable) {
            float currentLimit = speedLimitMutable.getMaxSpeed();
            float requiredLimit = speed + IMPULSE;
            if (!Float.isFinite(currentLimit) || currentLimit < requiredLimit) {
                speedLimitMutable.setMaxSpeed(requiredLimit);
            }
        }
    }
}
