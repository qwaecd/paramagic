package com.qwaecd.paramagic.world.entity.projectile;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.network.IDataSerializable;
import com.qwaecd.paramagic.network.codec.NBTCodec;
import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.thaumaturgy.ProjectileEntity;
import com.qwaecd.paramagic.thaumaturgy.operator.AllParaOperators;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOperator;
import com.qwaecd.paramagic.thaumaturgy.operator.modifier.ModifierOperator;
import com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.PhysicsProvider;
import com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.engine.KineticsAccumulator;
import com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.engine.PhysicsEngine;
import com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.engine.PhysicsState;
import com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.runtime.ProjectileRuntimeModifier;
import com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.runtime.ProjectileRuntimeModifierContext;
import com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.runtime.ProjectileRuntimeModifierHost;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class ArrowLikeProjectileEntity extends AbstractArrow implements ProjectileEntity, PhysicsProvider, ProjectileRuntimeModifierHost {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArrowLikeProjectileEntity.class);

    protected final PhysicsState kineticsState;
    protected final KineticsAccumulator kineticsAccumulator = new KineticsAccumulator();
    protected final List<ProjectileRuntimeModifier> runtimeModifiers = new ArrayList<>();

    protected final List<ParaOperator> recordedOperators = new ArrayList<>();

    protected float inaccuracy = 0.0f;

    protected ArrowLikeProjectileEntity(EntityType<? extends AbstractArrow> entityType, Level level) {
        this(entityType, level, 1.0f);
    }

    protected ArrowLikeProjectileEntity(EntityType<? extends AbstractArrow> entityType, Level level, double mass) {
        super(entityType, level);
        this.kineticsState = new PhysicsState(mass);
    }

    @Override
    public void recordOperator(ParaOperator operator) {
        this.recordedOperators.add(operator);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            return;
        }
        this.applyKineticsStep();
    }

    @Override
    public void shoot() {
        this.syncEntityVelocityFromKinetics();
        Vector3d v = this.kineticsState.getVelocity();
        BaseProjectile.shoot(this, this.random, v.x, v.y, v.z, (float) v.length(), this.getInaccuracy());
        this.syncEntityVelocityFromKinetics();
        this.level().addFreshEntity(this);

        Vec3 position = this.position();
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
        this.kineticsAccumulator.reset();
        for (ProjectileRuntimeModifier modifier : this.runtimeModifiers) {
            modifier.applyTick(context, this.kineticsAccumulator);
        }
        PhysicsEngine.update(this.kineticsState, this.kineticsAccumulator);
        this.syncEntityVelocityFromKinetics();
    }

    protected void syncEntityVelocityFromKinetics() {
        Vector3d velocity = this.kineticsState.getVelocity();
        this.setDeltaMovement(velocity.x, velocity.y, velocity.z);
    }

    @Override
    public void addRuntimeModifier(ProjectileRuntimeModifier modifier) {
        this.runtimeModifiers.add(modifier);
    }

    @Override
    public void setPosition(float x, float y, float z) {
        this.setPos(x, y, z);
    }

    @Override
    public double getMass() {
        return this.kineticsState.getMass();
    }

    @Override
    public double getInvMass() {
        return this.kineticsState.getInvMass();
    }

    @Override
    public void setVelocity(double x, double y, double z, boolean syncToClient) {
        this.kineticsState.setVelocity(x, y, z);
        this.syncEntityVelocityFromKinetics();
        if (syncToClient) {
            this.markVelocitySyncDirty();
        }
    }

    @Override
    public Vector3d getVelocity(Vector3d dest) {
        return this.kineticsState.getVelocity(dest);
    }

    @Override
    public void addVelocity(double x, double y, double z, boolean syncToClient) {
        this.kineticsState.addVelocity(x, y, z);
        this.syncEntityVelocityFromKinetics();
        if (syncToClient) {
            this.markVelocitySyncDirty();
        }
    }

    @Override
    public void setInaccuracy(float inaccuracy) {
        this.inaccuracy = inaccuracy;
    }

    @Override
    public float getInaccuracy() {
        return this.inaccuracy;
    }

    @Override
    public void setDragCoefficient(double coefficient) {
        this.kineticsState.setDragCoefficient(coefficient);
    }

    public double getDragCoefficient() {
        return this.kineticsState.getDragCoefficient();
    }

    @Override
    public double getMaxSpeed() {
        return this.kineticsState.getMaxSpeed();
    }

    @Override
    public void setMaxSpeed(double maxSpeed) {
        this.kineticsState.setMaxSpeed(maxSpeed);
    }

    @Override
    public double getGravityScale() {
        return this.kineticsState.getGravityScale();
    }

    @Override
    public void setGravityScale(double gravityScale) {
        this.kineticsState.setGravityScale(gravityScale);
    }

    @Override
    public PhysicsProvider physics() {
        return this;
    }

    @PlatformScope(PlatformScopeType.SERVER)
    public void markVelocitySyncDirty() {
        this.hasImpulse = true;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        CompoundTag tag = new CompoundTag();
        NBTCodec codec = new NBTCodec(tag);
        ParaOpId[] paraOpIds = this.recordedOperators.stream()
                .filter(Objects::nonNull)
                .map(ParaOperator::getId)
                .toArray(ParaOpId[]::new);
        codec.writeObjectArray("recordedOperators", paraOpIds);
        codec.writeObject("kineticsState", this.kineticsState);
        compound.put(Paramagic.MOD_ID, tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        CompoundTag tag = compound.getCompound(Paramagic.MOD_ID);
        if (tag.isEmpty()) {
            return;
        }
        NBTCodec codec = new NBTCodec(tag);
        try {
            IDataSerializable[] array = codec.readObjectArray("recordedOperators", ParaOpId::fromCodec);
            this.recordedOperators.clear();
            for (var id : array) {
                ParaOperator operator = AllParaOperators.createOperator((ParaOpId) id);
                if (operator != null) {
                    this.recordedOperators.add(operator);
                }
            }
            PhysicsState state = codec.readObject("kineticsState", PhysicsState::fromCodec);
            this.kineticsState.set(state);
        } catch (ArrayIndexOutOfBoundsException e) {
            LOGGER.warn("Failed to read recorded operators for projectile {}. The data might be corrupted or from an older version.", this.getId(), e);
        }
        for (ParaOperator operator : this.recordedOperators) {
            if (operator instanceof ModifierOperator modifier) {
                modifier.rebuild(this);
            }
        }
    }
}
