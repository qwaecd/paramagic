package com.qwaecd.paramagic.thaumaturgy.operator.modifier;

import com.qwaecd.paramagic.thaumaturgy.ProjectileEntity;
import com.qwaecd.paramagic.thaumaturgy.operator.OperatorType;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.PhysicsProvider;
import com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.ProjectileTargetingAlgorithms;
import com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.engine.KineticsAccumulator;
import com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.engine.PhysicsMath;
import com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.runtime.ProjectileRuntimeModifier;
import com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.runtime.ProjectileRuntimeModifierContext;
import com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.runtime.ProjectileRuntimeModifierHost;
import com.qwaecd.paramagic.thaumaturgy.runtime.ParaContext;
import com.qwaecd.paramagic.tools.ModRL;
import com.qwaecd.paramagic.world.item.ModItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

public class ShortTrackingOperator extends ModifierOperator {
    public static final ParaOpId OP_ID = ParaOpId.of(
            ModRL.inModSpace("short_tracking_operator"),
            new ParaOpId.Properties(OperatorType.MODIFIER, 0.1f, 0.08f)
    );

    private static final float TRACKING_RANGE = 8.0f;
    private static final float TRACKING_STRENGTH = 0.6f;

    public ShortTrackingOperator() {
        super(OP_ID, ModItems.SHORT_TRACKING_OPERATOR);
    }

    @Override
    public boolean apply(ParaContext context) {
        context.forEachProjectiles((projectile, index) -> this.tryApplyShortTracking(projectile));
        return true;
    }

    @Override
    public void rebuild(ProjectileEntity entity) {
        this.tryApplyShortTracking(entity);
    }

    private void tryApplyShortTracking(ProjectileEntity projectile) {
        if (!(projectile instanceof ProjectileRuntimeModifierHost modifierHost)) {
            return;
        }
        modifierHost.addRuntimeModifier(new Modifier(TRACKING_RANGE, TRACKING_STRENGTH));
    }

    public static final class Modifier implements ProjectileRuntimeModifier {
        private final float range;
        private final float maxStrength;

        public Modifier(float range, float maxStrength) {
            this.range = range;
            this.maxStrength = maxStrength;
        }
        @Override
        public void applyTick(ProjectileRuntimeModifierContext context, KineticsAccumulator accumulator) {
            LivingEntity target = ProjectileTargetingAlgorithms.findNearestLivingTarget(context, this.range);
            if (target == null) {
                return;
            }

            Vec3 directionToTarget = target.getEyePosition().subtract(context.getPosition());
            final double dir = directionToTarget.lengthSqr();
            if (!Double.isFinite(dir) || dir <= 1.0e-8) {
                return;
            }

            float s;
            final float rangeSqr = this.range * this.range;
            if (dir >= rangeSqr) {
                s = this.maxStrength;
            } else {
                s = (float) Math.min(rangeSqr / dir * this.maxStrength, this.maxStrength * 1.5f);
            }
            if (!Float.isFinite(s)) {
                return;
            }
            Vector3d normalizedDirection = new Vector3d();
            if (!PhysicsMath.tryNormalize(
                    directionToTarget.x,
                    directionToTarget.y,
                    directionToTarget.z,
                    s,
                    normalizedDirection
            )) {
                return;
            }
            PhysicsProvider physics = context.getProjectile().physics();
            physics.pushWithMomentum(
                    normalizedDirection.x,
                    normalizedDirection.y,
                    normalizedDirection.z
            );
        }
    }
}
