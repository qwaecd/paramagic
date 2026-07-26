package com.qwaecd.paramagic.thaumaturgy.projectile.damage;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.projectile.Projectile;

public final class ProjectileDamageResolver {
    private ProjectileDamageResolver() {
    }

    public static boolean damage(Projectile projectile, Entity target, ProjectileDamage damage) {
        Holder<DamageType> damageType = projectile.level().registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(damage.type());
        DamageSource source = new DamageSource(damageType, projectile, projectile.getOwner());
        return target.hurt(source, damage.amount());
    }
}
