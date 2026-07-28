package com.qwaecd.paramagic.thaumaturgy.operator.modifier;

import com.qwaecd.paramagic.thaumaturgy.ProjectileEntity;
import com.qwaecd.paramagic.thaumaturgy.operator.OperatorRegistration;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.PhysicsProvider;
import com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.ProjectileTargetingAlgorithms;
import com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.engine.KineticsAccumulator;
import com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.engine.PhysicsMath;
import com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.runtime.ProjectileRuntimeModifier;
import com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.runtime.ProjectileRuntimeModifierContext;
import com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.runtime.ProjectileRuntimeModifierHost;
import com.qwaecd.paramagic.thaumaturgy.runtime.ParaContext;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

public class TrackingOperator extends ModifierOperator {
    private static final float TRACKING_RANGE = 12.0f;
    private static final float TRACKING_STRENGTH = 0.2f;

    public TrackingOperator(ParaOpId id) {
        super(id, OperatorRegistration.itemOf("tracking_operator"));
    }

    @Override
    public boolean apply(ParaContext context) {
        context.forEachProjectiles((projectile, index) -> this.tryApplyTracking(projectile));
        return true;
    }

    @Override
    public void rebuild(ProjectileEntity entity) {
        this.tryApplyTracking(entity);
    }

    private void tryApplyTracking(ProjectileEntity projectile) {
        if (!(projectile instanceof ProjectileRuntimeModifierHost modifierHost)) {
            return;
        }
        modifierHost.addRuntimeModifier(new TrackingRuntimeModifier(TRACKING_RANGE, TRACKING_STRENGTH));
    }

    public static final class TrackingRuntimeModifier implements ProjectileRuntimeModifier {
        private final float range;
        private final float strength;

        public TrackingRuntimeModifier(float range, float strength) {
            this.range = range;
            this.strength = strength;
        }

        @Override
        public void applyTick(ProjectileRuntimeModifierContext context, KineticsAccumulator accumulator) {
            LivingEntity target = ProjectileTargetingAlgorithms.findNearestLivingTarget(context, this.range);
            if (target == null) {
                return;
            }

            Vec3 directionToTarget = target.getEyePosition().subtract(context.getPosition());
            if (directionToTarget.lengthSqr() <= 1.0e-8) {
                return;
            }

            Vector3d normalizedDirection = new Vector3d();
            if (!PhysicsMath.tryNormalize(
                    directionToTarget.x,
                    directionToTarget.y,
                    directionToTarget.z,
                    this.strength,
                    normalizedDirection
            )) {
                return;
            }
            PhysicsProvider physics = context.getProjectile().physics();
            physics.pushWithMomentum(
                    normalizedDirection.x,
                    normalizedDirection.y,
                    normalizedDirection.z,
                    false
            );
        }
    }
}
