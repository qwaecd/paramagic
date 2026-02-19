package com.qwaecd.paramagic.world.entity.projectile;

import com.qwaecd.paramagic.thaumaturgy.ProjectileEntity;
import com.qwaecd.paramagic.world.entity.ModEntityTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public class MagicArrowProjectile extends ArrowLikeProjectileEntity implements ProjectileEntity {

    public MagicArrowProjectile(EntityType<? extends ArrowLikeProjectileEntity> type, Level level) {
        super(type, level);
    }

    public MagicArrowProjectile(Level level) {
        super(ModEntityTypes.MAGIC_ARROW_PROJECTILE, level);
    }

    @Override
    @Nonnull
    protected ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }
}
