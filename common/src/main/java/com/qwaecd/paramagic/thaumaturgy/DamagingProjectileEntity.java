package com.qwaecd.paramagic.thaumaturgy;

import com.qwaecd.paramagic.thaumaturgy.projectile.damage.ProjectileDamage;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

import javax.annotation.Nonnull;

public interface DamagingProjectileEntity extends ProjectileEntity {
    /**
     * Returns the current direct-hit damage configuration.
     * <p>
     * 返回当前直接命中伤害配置。
     */
    @Nonnull
    ProjectileDamage getProjectileDamage();

    /**
     * Replaces the damage type and amount while preserving the current owner-hit setting.
     * <p>
     * 替换伤害类型与数值，同时保留当前是否允许命中施法者的设置。
     */
    default void setDamage(ResourceKey<DamageType> type, float amount) {
        this.setDamage(type, amount, this.getProjectileDamage().canHitOwner());
    }

    /**
     * Replaces every direct-hit damage property at once.
     * <p>
     * 一次替换所有直接命中伤害属性。
     */
    default void setDamage(ResourceKey<DamageType> type, float amount, boolean canHitOwner) {
        this.setProjectileDamage(new ProjectileDamage(type, amount, canHitOwner));
    }

    /**
     * Replaces only the damage type.
     * <p>
     * 仅替换伤害类型。
     */
    default void setDamageType(ResourceKey<DamageType> type) {
        ProjectileDamage damage = this.getProjectileDamage();
        this.setDamage(type, damage.amount(), damage.canHitOwner());
    }

    /**
     * Replaces only the damage amount.
     * <p>
     * 仅替换伤害数值。
     */
    default void setDamageAmount(float amount) {
        ProjectileDamage damage = this.getProjectileDamage();
        this.setDamage(damage.type(), amount, damage.canHitOwner());
    }

    /**
     * Changes whether this projectile may hit its owner.
     * <p>
     * 修改该投射物是否允许命中施法者。
     */
    default void setCanHitOwner(boolean canHitOwner) {
        ProjectileDamage damage = this.getProjectileDamage();
        this.setDamage(damage.type(), damage.amount(), canHitOwner);
    }

    /**
     * Replaces the complete immutable damage configuration.
     * <p>
     * 替换完整的不可变伤害配置。
     */
    void setProjectileDamage(@Nonnull ProjectileDamage damage);
}
