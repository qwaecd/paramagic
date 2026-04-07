package com.qwaecd.paramagic.world.entity.projectile;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.network.IDataSerializable;
import com.qwaecd.paramagic.network.codec.NBTCodec;
import com.qwaecd.paramagic.thaumaturgy.ProjectileEntity;
import com.qwaecd.paramagic.thaumaturgy.kinetics.*;
import com.qwaecd.paramagic.thaumaturgy.kinetics.runtime.ProjectileKineticsAccumulator;
import com.qwaecd.paramagic.thaumaturgy.kinetics.runtime.ProjectileRuntimeModifier;
import com.qwaecd.paramagic.thaumaturgy.kinetics.runtime.ProjectileRuntimeModifierContext;
import com.qwaecd.paramagic.thaumaturgy.kinetics.runtime.ProjectileRuntimeModifierHost;
import com.qwaecd.paramagic.thaumaturgy.operator.AllParaOperators;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOpId;
import com.qwaecd.paramagic.thaumaturgy.operator.ParaOperator;
import com.qwaecd.paramagic.thaumaturgy.operator.content.ModifierOperator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class BaseProjectile extends ThrowableProjectile implements ProjectileEntity, ProjectileVelocityMutable, ProjectileInaccuracyMutable, ProjectilePersistentAccelerationMutable, ProjectileLinearDampingMutable, ProjectileSpeedLimitMutable, ProjectileGravityMutable, ProjectileRuntimeModifierHost {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseProjectile.class);

    protected final ProjectileKineticsState kineticsState = new ProjectileKineticsState();
    protected final ProjectileKineticsAccumulator kineticsAccumulator = new ProjectileKineticsAccumulator();
    protected final List<ProjectileRuntimeModifier> runtimeModifiers = new ArrayList<>();

    protected final List<ParaOperator> recordedOperators = new ArrayList<>();
    protected BaseProjectile(EntityType<? extends ThrowableProjectile> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void recordOperator(ParaOperator operator) {
        this.recordedOperators.add(operator);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide && this.isAlive()) {
            return;
        }
        this.applyKineticsStep();

        Vec3 start = this.position();
        Vec3 end = start.add(this.getDeltaMovement());
        HitResult hitResult = this.findHitResult(start, end);
        if (hitResult.getType() != HitResult.Type.MISS) {
            this.setPos(hitResult.getLocation());
            this.onHit(hitResult);
        } else {
            this.setPos(end);
        }
    }

    private HitResult findHitResult(Vec3 start, Vec3 end) {
        BlockHitResult blockHitResult = this.level().clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        Vec3 clippedEnd = blockHitResult.getType() == HitResult.Type.MISS ? end : blockHitResult.getLocation();
        AABB searchBox = this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(0.35f);
        EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(this.level(), this, start, clippedEnd, searchBox, this::canHitEntity);
        if (entityHitResult == null) {
            return blockHitResult;
        }
        if (blockHitResult.getType() == HitResult.Type.MISS) {
            return entityHitResult;
        }
        double entityDistance = start.distanceToSqr(entityHitResult.getLocation());
        double blockDistance = start.distanceToSqr(blockHitResult.getLocation());
        return entityDistance <= blockDistance ? entityHitResult : blockHitResult;
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

    protected void syncEntityVelocityFromKinetics() {
        Vector3f velocity = this.kineticsState.getVelocity();
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
    protected void defineSynchedData() {
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
            ProjectileKineticsState state = codec.readObject("kineticsState", ProjectileKineticsState::fromCodec);
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
