package com.qwaecd.paramagic.world.entity.projectile;

import com.qwaecd.paramagic.thaumaturgy.ProjectileEntity;
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
        this.shoot(position.x, position.y, position.z, (float) velocity.length(), this.inaccuracy);
    }

    @Override
    public void setInaccuracy(float inaccuracy) {
        this.inaccuracy = inaccuracy;
    }

    @Override
    protected void defineSynchedData() {

    }
}
