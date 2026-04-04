package com.qwaecd.paramagic.world.entity.projectile;

import com.qwaecd.paramagic.thaumaturgy.kinetics.ProjectileInaccuracyMutable;
import com.qwaecd.paramagic.thaumaturgy.ProjectileEntity;
import com.qwaecd.paramagic.thaumaturgy.kinetics.ProjectileGravityMutable;
import com.qwaecd.paramagic.thaumaturgy.kinetics.ProjectileKineticsState;
import com.qwaecd.paramagic.thaumaturgy.kinetics.ProjectileKineticsUpdater;
import com.qwaecd.paramagic.thaumaturgy.kinetics.ProjectileLinearDampingMutable;
import com.qwaecd.paramagic.thaumaturgy.kinetics.ProjectilePersistentAccelerationMutable;
import com.qwaecd.paramagic.thaumaturgy.kinetics.ProjectileSpeedLimitMutable;
import com.qwaecd.paramagic.thaumaturgy.kinetics.ProjectileVelocityMutable;
import com.qwaecd.paramagic.thaumaturgy.kinetics.runtime.ProjectileKineticsAccumulator;
import com.qwaecd.paramagic.thaumaturgy.kinetics.runtime.ProjectileRuntimeModifier;
import com.qwaecd.paramagic.thaumaturgy.kinetics.runtime.ProjectileRuntimeModifierContext;
import com.qwaecd.paramagic.thaumaturgy.kinetics.runtime.ProjectileRuntimeModifierHost;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public abstract class ArrowLikeProjectileEntity extends AbstractArrow implements ProjectileEntity, ProjectileVelocityMutable, ProjectileInaccuracyMutable, ProjectilePersistentAccelerationMutable, ProjectileLinearDampingMutable, ProjectileSpeedLimitMutable, ProjectileGravityMutable, ProjectileRuntimeModifierHost {
    protected final ProjectileKineticsState kineticsState = new ProjectileKineticsState();
    protected final ProjectileKineticsAccumulator kineticsAccumulator = new ProjectileKineticsAccumulator();
    protected final List<ProjectileRuntimeModifier> runtimeModifiers = new ArrayList<>();

    protected ArrowLikeProjectileEntity(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
        this.setNoGravity(true);
    }

    protected ProjectileKineticsState kineticsState() {
        return this.kineticsState;
    }

    @Override
    public void setPosition(float x, float y, float z) {
        this.setPos(x, y, z);
    }

    @Override
    public void setVelocity(float x, float y, float z) {
        this.kineticsState.setVelocity(x, y, z);
        this.syncEntityVelocityFromKinetics();
    }

    @Override
    public Vector3f getVelocity() {
        return this.kineticsState.getVelocity();
    }

    @Override
    public void addVelocity(float x, float y, float z) {
        this.kineticsState.addVelocity(x, y, z);
        this.syncEntityVelocityFromKinetics();
    }

    @Override
    public void shoot() {
        this.syncEntityVelocityFromKinetics();
        Vec3 position = this.position();
        Vec3 velocity = this.getDeltaMovement();
        this.shoot(velocity.x, velocity.y, velocity.z, (float) velocity.length(), this.kineticsState.getInaccuracy());
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
        this.kineticsState.setInaccuracy(inaccuracy);
    }

    @Override
    public float getInaccuracy() {
        return this.kineticsState.getInaccuracy();
    }

    @Override
    public Vector3f getPersistentAcceleration() {
        return this.kineticsState.getPersistentAcceleration();
    }

    @Override
    public void setPersistentAcceleration(float x, float y, float z) {
        this.kineticsState.setPersistentAcceleration(x, y, z);
    }

    @Override
    public void addPersistentAcceleration(float x, float y, float z) {
        this.kineticsState.addPersistentAcceleration(x, y, z);
    }

    @Override
    public void clearPersistentAcceleration() {
        this.kineticsState.clearPersistentAcceleration();
    }

    @Override
    public float getLinearDamping() {
        return this.kineticsState.getLinearDamping();
    }

    @Override
    public void setLinearDamping(float linearDamping) {
        this.kineticsState.setLinearDamping(linearDamping);
    }

    @Override
    public float getMaxSpeed() {
        return this.kineticsState.getMaxSpeed();
    }

    @Override
    public void setMaxSpeed(float maxSpeed) {
        this.kineticsState.setMaxSpeed(maxSpeed);
    }

    @Override
    public float getGravityScale() {
        return this.kineticsState.getGravityScale();
    }

    @Override
    public void setGravityScale(float gravityScale) {
        this.kineticsState.setGravityScale(gravityScale);
    }

    @Override
    public void addRuntimeModifier(ProjectileRuntimeModifier modifier) {
        this.runtimeModifiers.add(modifier);
    }

    @Override
    public void tick() {
        this.applyKineticsStep();
        super.tick();
        this.syncKineticsFromEntityVelocity();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    protected void syncEntityVelocityFromKinetics() {
        Vector3f velocity = this.kineticsState.getVelocity();
        this.setDeltaMovement(velocity.x, velocity.y, velocity.z);
    }

    protected void syncKineticsFromEntityVelocity() {
        Vec3 velocity = this.getDeltaMovement();
        this.kineticsState.setVelocity((float) velocity.x, (float) velocity.y, (float) velocity.z);
    }

    protected void applyKineticsStep() {
        ProjectileRuntimeModifierContext context = new ProjectileRuntimeModifierContext(
                this,
                this,
                this.level(),
                this.position(),
                this.kineticsState.getVelocity(),
                this.getOwner(),
                this.tickCount
        );
        this.kineticsAccumulator.clear();
        for (ProjectileRuntimeModifier modifier : this.runtimeModifiers) {
            modifier.applyTick(context, this.kineticsAccumulator);
        }
        ProjectileKineticsUpdater.step(this.kineticsState, this.kineticsAccumulator);
        this.syncEntityVelocityFromKinetics();
    }
}
