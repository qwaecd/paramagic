package com.qwaecd.paramagic.thaumaturgy.kinetics.runtime;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.Comparator;

public final class ProjectileTargetingAlgorithms {
    private ProjectileTargetingAlgorithms() {}

    @Nullable
    public static LivingEntity findNearestLivingTarget(ProjectileRuntimeModifierContext context, float range) {
        Entity projectileEntity = context.getEntity();
        Entity owner = context.getOwner();
        return context.getLevel().getEntitiesOfClass(
                LivingEntity.class,
                projectileEntity.getBoundingBox().inflate(range),
                entity -> isValidLivingTarget(projectileEntity, owner, entity, range * range)
        ).stream().min(Comparator.comparingDouble(entity -> entity.distanceToSqr(projectileEntity))).orElse(null);
    }

    private static boolean isValidLivingTarget(Entity projectileEntity, @Nullable Entity owner, LivingEntity candidate, double maxDistanceSquared) {
        if (candidate == projectileEntity || candidate == owner) {
            return false;
        }
        if (!candidate.isAlive() || candidate.isSpectator()) {
            return false;
        }
        return candidate.distanceToSqr(projectileEntity) <= maxDistanceSquared;
    }
}
