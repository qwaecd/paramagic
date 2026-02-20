package com.qwaecd.paramagic.world.entity.projectile;

import com.qwaecd.paramagic.thaumaturgy.ProjectileEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class ArrowLikeProjectileEntity extends AbstractArrow implements ProjectileEntity {
    protected float inaccuracy = 0.0f;

    protected ArrowLikeProjectileEntity(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void setPosition(float x, float y, float z) {
        this.setPos(x, y, z);
    }

    @Override
    public void setVelocity(float x, float y, float z) {
        this.setDeltaMovement(x, y, z);
    }

    @Override
    public void shoot() {
        Vec3 position = this.position();
        Vec3 velocity = this.getDeltaMovement();
        this.shoot(velocity.x, velocity.y, velocity.z, (float) velocity.length(), this.inaccuracy);
        this.level().addFreshEntity(this);

        level().playSound(
                null,
                position.x,
                position.y,
                position.z,
                SoundEvents.ARROW_SHOOT,
                SoundSource.PLAYERS,
                1.0F,
                1.0F / (level().getRandom().nextFloat() * 0.4F + 1.2F)
        );
    }

    @Override
    public void setInaccuracy(float inaccuracy) {
        this.inaccuracy = inaccuracy;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }
}
