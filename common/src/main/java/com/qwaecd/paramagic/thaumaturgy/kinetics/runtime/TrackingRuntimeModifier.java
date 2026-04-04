package com.qwaecd.paramagic.thaumaturgy.kinetics.runtime;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public final class TrackingRuntimeModifier implements ProjectileRuntimeModifier {
    private final float range;
    private final float strength;

    public TrackingRuntimeModifier(float range, float strength) {
        this.range = range;
        this.strength = strength;
    }

    @Override
    public void applyTick(ProjectileRuntimeModifierContext context, ProjectileKineticsAccumulator accumulator) {
        LivingEntity target = ProjectileTargetingAlgorithms.findNearestLivingTarget(context, this.range);
        if (target == null) {
            return;
        }

        Vec3 directionToTarget = target.getEyePosition().subtract(context.getPosition());
        if (directionToTarget.lengthSqr() <= 1.0e-8) {
            return;
        }

        Vec3 normalizedDirection = directionToTarget.normalize().scale(this.strength);
        accumulator.addTransientAcceleration(
                (float) normalizedDirection.x,
                (float) normalizedDirection.y,
                (float) normalizedDirection.z
        );
    }
}
