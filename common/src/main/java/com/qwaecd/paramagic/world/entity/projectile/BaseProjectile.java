package com.qwaecd.paramagic.world.entity.projectile;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.network.IDataSerializable;
import com.qwaecd.paramagic.network.codec.NBTCodec;
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
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import org.joml.Vector3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class BaseProjectile extends ThrowableProjectile implements ProjectileEntity, PhysicsProvider, ProjectileRuntimeModifierHost {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseProjectile.class);

    protected final PhysicsState kineticsState;
    protected final KineticsAccumulator kineticsAccumulator = new KineticsAccumulator();
    protected final List<ProjectileRuntimeModifier> runtimeModifiers = new ArrayList<>();

    protected final List<ParaOperator> recordedOperators = new ArrayList<>();

    protected float inaccuracy = 0.0f;

    protected BaseProjectile(EntityType<? extends ThrowableProjectile> entityType, Level level) {
        super(entityType, level);
        this.kineticsState = new PhysicsState();
    }

    protected BaseProjectile(EntityType<? extends ThrowableProjectile> entityType, Level level, double mass) {
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

    @Override
    public void shoot() {
        this.syncEntityVelocityFromKinetics();
        Vec3 velocity = this.getDeltaMovement();
        BaseProjectile.shoot(this, this.random, velocity.x, velocity.y, velocity.z, (float) velocity.length(), this.getInaccuracy());
        this.level().addFreshEntity(this);
    }

    public static void shoot(
            Entity e,
            RandomSource random,
            double x, double y, double z,
            float velocity,
            float inaccuracy
    ) {
        Vec3 axis = new Vec3(x, y, z);
        if (axis.lengthSqr() < 1.0E-12D) {
            axis = new Vec3(0.0D, 0.0D, 1.0D);
        } else {
            axis = axis.normalize();
        }

        // inaccuracy is treated as the cone apex angle in degrees:
        // 180 -> hemisphere, 360 -> full sphere.
        double halfAngleRadians = Math.toRadians(Mth.clamp(inaccuracy, 0.0F, 360.0F) * 0.5D);
        double cosThetaMin = Math.cos(halfAngleRadians);
        double cosTheta = Mth.lerp(random.nextDouble(), cosThetaMin, 1.0D);
        double sinTheta = Math.sqrt(Math.max(0.0D, 1.0D - cosTheta * cosTheta));
        double phi = random.nextDouble() * Math.PI * 2.0D;

        Vec3 helper = Math.abs(axis.y) < 0.999D ? new Vec3(0.0D, 1.0D, 0.0D) : new Vec3(1.0D, 0.0D, 0.0D);
        Vec3 tangent1 = helper.cross(axis).normalize();
        Vec3 tangent2 = axis.cross(tangent1);
        Vec3 direction = tangent1.scale(sinTheta * Math.cos(phi))
                .add(tangent2.scale(sinTheta * Math.sin(phi)))
                .add(axis.scale(cosTheta))
                .normalize();

        Vec3 vec3 = direction.scale(velocity);
        e.setDeltaMovement(vec3);
        double d0 = vec3.horizontalDistance();
        //noinspection SuspiciousNameCombination
        e.setYRot((float)(Mth.atan2(vec3.x, vec3.z) * (double)(180F / (float)Math.PI)));
        e.setXRot((float)(Mth.atan2(vec3.y, d0) * (double)(180F / (float)Math.PI)));
        e.yRotO = e.getYRot();
        e.xRotO = e.getXRot();
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
        this.kineticsAccumulator.reset();
        for (ProjectileRuntimeModifier modifier : this.runtimeModifiers) {
            modifier.applyTick(context, this.kineticsAccumulator);
        }
        PhysicsEngine.update(this.kineticsState, this.kineticsAccumulator, 1.0d / 20.0d);
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
    public void setVelocity(double x, double y, double z) {
        this.kineticsState.setVelocity(x, y, z);
        this.syncEntityVelocityFromKinetics();
    }

    @Override
    public Vector3d getVelocity(Vector3d dest) {
        return this.kineticsState.getVelocity(dest);
    }

    @Override
    public void addVelocity(double x, double y, double z) {
        this.kineticsState.addVelocity(x, y, z);
        this.syncEntityVelocityFromKinetics();
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
