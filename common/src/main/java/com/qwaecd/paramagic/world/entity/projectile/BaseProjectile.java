package com.qwaecd.paramagic.world.entity.projectile;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.network.DataCodec;
import com.qwaecd.paramagic.network.IDataSerializable;
import com.qwaecd.paramagic.network.codec.NBTCodec;
import com.qwaecd.paramagic.network.serializer.AllEntityDataSerializers;
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
import com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.engine.PhysicsMath;
import com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.engine.PhysicsState;
import com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.runtime.ProjectileRuntimeModifier;
import com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.runtime.ProjectileRuntimeModifierContext;
import com.qwaecd.paramagic.thaumaturgy.projectile.kinetics.runtime.ProjectileRuntimeModifierHost;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.*;
import org.joml.Vector3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

public abstract class BaseProjectile extends ThrowableProjectile implements ProjectileEntity, PhysicsProvider, ProjectileRuntimeModifierHost {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseProjectile.class);
    protected static final EntityDataAccessor<List<ParaOpId>> PROJECTILE_RUNTIME_MODIFIER = SynchedEntityData.defineId(BaseProjectile.class, AllEntityDataSerializers.PROJECTILE_RUNTIME_MODIFIER);
    // 客户端单次逻辑步长（20 TPS）
    private static final double CLIENT_TICK_DELTA = 1.0d / 20.0d;
    // 位置纠偏收敛速度（Hz），越大追得越快
    private static final double CLIENT_POSITION_CORRECTION_HZ = 20.0d;
    // 位置误差超过该阈值时直接瞬移纠正（这里是距离平方）
    private static final double CLIENT_POSITION_SNAP_DISTANCE_SQR = 9.0d;
    // 位置误差小于该阈值时视为已收敛（这里是距离平方）
    private static final double CLIENT_POSITION_CORRECTION_EPSILON_SQR = 1.0E-4D;
    // 速度融合权重，越大越快贴近服务端速度
    private static final double CLIENT_VELOCITY_BLEND_ALPHA = 1.0d;
    // 位置纠偏最少持续 tick 数，避免一帧内结束
    private static final int CLIENT_MIN_POSITION_CORRECTION_TICKS = 2;
    // 速度纠偏默认持续 tick 数
    private static final int CLIENT_DEFAULT_VELOCITY_CORRECTION_TICKS = 3;
    private static final int MAX_COLLISION_ITERATIONS = 16;
    private static final double COLLISION_ADVANCE_EPSILON = 1.0E-4D;

    protected float age = 0.0f;

    protected final PhysicsState kineticsState;
    protected final KineticsAccumulator kineticsAccumulator = new KineticsAccumulator();
    protected final List<ProjectileRuntimeModifier> runtimeModifiers = new ArrayList<>();

    @PlatformScope(PlatformScopeType.COMMON)
    protected final List<ParaOperator> recordedOperators = new ArrayList<>();

    protected float inaccuracy = 0.0f;
    // 服务端下发的目标位置（客户端用于渐进纠偏）
    private Vec3 clientCorrectionTargetPos = Vec3.ZERO;
    // 服务端下发的目标速度（客户端用于速度融合）
    private Vec3 clientCorrectionTargetVel = Vec3.ZERO;
    // 位置纠偏剩余 tick 数
    private int clientPositionCorrectionTicks = 0;
    // 速度纠偏剩余 tick 数
    private int clientVelocityCorrectionTicks = 0;
    // 是否存在待处理的位置纠偏
    private boolean hasClientPositionCorrection = false;
    // 是否存在待处理的速度纠偏
    private boolean hasClientVelocityCorrection = false;
    private boolean hasBeenShotLike = false;
    private boolean leftOwnerLike = false;
    private boolean noPhysicsLike = false;
    private int piercedEntityCount = 0;

    protected BaseProjectile(EntityType<? extends ThrowableProjectile> entityType, Level level) {
        super(entityType, level);
        this.setNoGravity(true);
        this.kineticsState = new PhysicsState();
    }

    protected BaseProjectile(EntityType<? extends ThrowableProjectile> entityType, Level level, double mass) {
        super(entityType, level);
        this.setNoGravity(true);
        this.kineticsState = new PhysicsState(mass);
    }

    @Override
    public void recordOperator(ParaOperator operator) {
        this.recordedOperators.add(operator);
    }

    @PlatformScope(PlatformScopeType.SERVER)
    protected void onLifeEnd() {
        this.discard();
    }

    @Override
    public void tick() {
        this.runBaseLifecycleTick();
        this.age += 1.0f / 20.0f;
        if (this.level().isClientSide) {
            this.applyKineticsStep();
            this.lerpOnClientTick();
            this.finalizePostMoveState();
            return;
        }
        this.updateProjectileStateFlags();
        this.applyKineticsStep();
        Vec3 start = this.position();
        MovementPlan plan = this.resolveMovement(start, this.getDeltaMovement());
        CollisionResolveResult collisionResult = this.processCollisionPipeline(plan);
        if (this.isRemoved()) {
            return;
        }
        this.setPos(collisionResult.finalPosition());
        this.finalizePostMoveState();
    }

    protected void runBaseLifecycleTick() {
        this.baseTick();
    }

    protected void updateProjectileStateFlags() {
        if (!this.hasBeenShotLike) {
            this.gameEvent(GameEvent.PROJECTILE_SHOOT, this.getOwner());
            this.hasBeenShotLike = true;
        }
        if (!this.leftOwnerLike) {
            this.leftOwnerLike = this.checkLeftOwnerLike();
        }
    }

    protected MovementPlan resolveMovement(Vec3 start, Vec3 delta) {
        return new MovementPlan(start, delta, start.add(delta));
    }

    protected CollisionResolveResult processCollisionPipeline(MovementPlan plan) {
        if (this.isNoPhysicsLike() || plan.delta.lengthSqr() < 1.0E-12D) {
            return new CollisionResolveResult(plan.intendedEnd);
        }

        Vec3 cursor = plan.start;
        Vec3 remainingDelta = plan.delta;
        Set<Integer> piercedEntityIds = new HashSet<>();
        int iterations = 0;
        while (iterations++ < MAX_COLLISION_ITERATIONS && remainingDelta.lengthSqr() >= 1.0E-12D) {
            Vec3 segmentEnd = cursor.add(remainingDelta);
            HitResult hitResult = this.detectFirstHit(cursor, segmentEnd, entity -> this.canHitEntity(entity) && !piercedEntityIds.contains(entity.getId()));
            if (hitResult.getType() == HitResult.Type.MISS) {
                cursor = segmentEnd;
                break;
            }

            Vec3 hitLocation = hitResult.getLocation();
            // block
            if (hitResult instanceof BlockHitResult blockHitResult) {
                PortalBlockDecision portalDecision = this.handlePortalOrGatewayHit(blockHitResult);
                if (portalDecision == PortalBlockDecision.HANDLED_STOP) {
                    cursor = hitLocation;
                    break;
                }
                if (portalDecision == PortalBlockDecision.HANDLED_PASS_THROUGH) {
                    cursor = segmentEnd;
                    break;
                }
                if (!this.canHitBlock(blockHitResult)) {
                    Vec3 nextStart = this.advancePastHit(hitLocation, remainingDelta);
                    if (nextStart == null) {
                        cursor = hitLocation;
                        break;
                    }
                    remainingDelta = segmentEnd.subtract(nextStart);
                    cursor = nextStart;
                    continue;
                }
                HitDecision decision = this.sanitizeHitDecision(this.onHitBlockDecision(blockHitResult));
                this.setPos(hitLocation);
                this.onHit(blockHitResult);
                if (this.isRemoved()) {
                    return new CollisionResolveResult(hitLocation);
                }
                if (decision == HitDecision.PASS_THROUGH) {
                    Vec3 nextStart = this.advancePastHit(hitLocation, remainingDelta);
                    if (nextStart == null) {
                        cursor = hitLocation;
                        break;
                    }
                    remainingDelta = segmentEnd.subtract(nextStart);
                    cursor = nextStart;
                    continue;
                }
                cursor = hitLocation;
                break;
            }

            // entity
            if (hitResult instanceof EntityHitResult entityHitResult) {
                HitDecision decision = this.sanitizeHitDecision(this.onHitEntityDecision(entityHitResult));
                this.setPos(hitLocation);
                this.onHit(entityHitResult);
                if (this.isRemoved()) {
                    return new CollisionResolveResult(hitLocation);
                }
                if (decision == HitDecision.PASS_THROUGH && this.canPassThroughEntity(entityHitResult.getEntity())) {
                    piercedEntityIds.add(entityHitResult.getEntity().getId());
                    this.piercedEntityCount++;
                    Vec3 nextStart = this.advancePastHit(hitLocation, remainingDelta);
                    if (nextStart == null) {
                        cursor = hitLocation;
                        break;
                    }
                    remainingDelta = segmentEnd.subtract(nextStart);
                    cursor = nextStart;
                    continue;
                }
                cursor = hitLocation;
                break;
            }

            cursor = hitLocation;
            break;
        }
        return new CollisionResolveResult(cursor);
    }

    protected void lerpOnClientTick() {
        Vec3 localVelocity = this.getDeltaMovement();
        if (this.hasClientVelocityCorrection && this.clientVelocityCorrectionTicks > 0) {
            localVelocity = localVelocity.lerp(this.clientCorrectionTargetVel, CLIENT_VELOCITY_BLEND_ALPHA);
            this.kineticsState.setVelocity(localVelocity.x, localVelocity.y, localVelocity.z);
            this.setDeltaMovement(localVelocity);
            this.clientVelocityCorrectionTicks--;
            if (this.clientVelocityCorrectionTicks <= 0) {
                this.hasClientVelocityCorrection = false;
            }
        } else {
            var v = this.kineticsState.getVelocity();
            localVelocity = new Vec3(v.x, v.y, v.z);
        }

        Vec3 predictedPos = this.position().add(localVelocity);
        if (!this.hasClientPositionCorrection || this.clientPositionCorrectionTicks <= 0) {
            this.setPos(predictedPos);
            return;
        }

        Vec3 error = this.clientCorrectionTargetPos.subtract(predictedPos);
        if (error.lengthSqr() > CLIENT_POSITION_SNAP_DISTANCE_SQR) {
            this.setPos(this.clientCorrectionTargetPos);
            this.clearClientPositionCorrection();
            return;
        }

        double alpha = 1.0d - Math.exp(-CLIENT_POSITION_CORRECTION_HZ * CLIENT_TICK_DELTA);
        this.setPos(predictedPos.add(error.scale(alpha)));
        this.clientPositionCorrectionTicks--;
        if (this.clientPositionCorrectionTicks <= 0 || error.lengthSqr() <= CLIENT_POSITION_CORRECTION_EPSILON_SQR) {
            this.clearClientPositionCorrection();
        }
    }

    @Override
    public void lerpTo(double x, double y, double z, float yRot, float xRot, int lerpSteps, boolean teleport) {
        if (!this.level().isClientSide) {
            super.lerpTo(x, y, z, yRot, xRot, lerpSteps, teleport);
            return;
        }

        Vec3 targetPos = new Vec3(x, y, z);
        this.setRot(yRot, xRot);
        if (teleport || this.position().distanceToSqr(targetPos) > CLIENT_POSITION_SNAP_DISTANCE_SQR) {
            this.setPos(targetPos);
            this.clearClientPositionCorrection();
            return;
        }

        this.clientCorrectionTargetPos = targetPos;
        this.clientPositionCorrectionTicks = Math.max(CLIENT_MIN_POSITION_CORRECTION_TICKS, lerpSteps);
        this.hasClientPositionCorrection = true;
    }

    @Override
    public void lerpMotion(double x, double y, double z) {
        if (!this.level().isClientSide) {
            super.lerpMotion(x, y, z);
            return;
        }

        this.clientCorrectionTargetVel = new Vec3(x, y, z);
        this.clientVelocityCorrectionTicks = CLIENT_DEFAULT_VELOCITY_CORRECTION_TICKS;
        this.hasClientVelocityCorrection = true;
        if (this.getDeltaMovement().lengthSqr() < 1.0E-8D) {
            this.kineticsState.setVelocity(x, y, z);
            this.setDeltaMovement(x, y, z);
        }
    }

    private void clearClientPositionCorrection() {
        this.hasClientPositionCorrection = false;
        this.clientPositionCorrectionTicks = 0;
    }

    protected void finalizePostMoveState() {
        this.updateRotation();
        this.checkInsideBlocks();
    }

    @Override
    public void shoot() {
        Vector3d v = this.kineticsState.getVelocity();
        BaseProjectile.shoot(this, this.random, v.x, v.y, v.z, (float) v.length(), this.getInaccuracy());
        this.syncEntityVelocityFromKinetics();
        this.level().addFreshEntity(this);
        this.syncRecordedOperators();
    }

    protected void syncRecordedOperators() {
        if (this.level().isClientSide) {
            return;
        }
        List<ParaOpId> list = this.recordedOperators.stream()
                .map(ParaOperator::getId)
                .collect(ArrayList::new, List::add, List::addAll);
        this.entityData.set(PROJECTILE_RUNTIME_MODIFIER, list, true);
    }

    protected void syncVelocityFromEntity() {
        Vec3 movement = this.getDeltaMovement();
        if (!PhysicsMath.isFinite(movement.x, movement.y, movement.z)) {
            this.kineticsState.setVelocity(0.0d, 0.0d, 0.0d);
            this.setDeltaMovement(Vec3.ZERO);
            return;
        }
        this.kineticsState.setVelocity(movement.x, movement.y, movement.z);
    }

    public static void shoot(
            PhysicsProvider e,
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
        e.setVelocity(vec3.x, vec3.y, vec3.z);
        if (e instanceof Entity entity) {
            double d0 = vec3.horizontalDistance();
            //noinspection SuspiciousNameCombination
            entity.setYRot((float) (Mth.atan2(vec3.x, vec3.z) * (180.0d / Math.PI)));
            entity.setXRot((float) (Mth.atan2(vec3.y, d0) * (180.0d / Math.PI)));
            entity.yRotO = entity.getYRot();
            entity.xRotO = entity.getXRot();
        }
    }

    protected HitResult detectFirstHit(Vec3 start, Vec3 end) {
        return this.detectFirstHit(start, end, this::canHitEntity);
    }

    private HitResult detectFirstHit(Vec3 start, Vec3 end, Predicate<Entity> entityPredicate) {
        BlockHitResult blockHitResult = this.level().clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        Vec3 clippedEnd = blockHitResult.getType() == HitResult.Type.MISS ? end : blockHitResult.getLocation();
        AABB searchBox = this.getBoundingBox().expandTowards(end.subtract(start)).inflate(0.35f);
        EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(this.level(), this, start, clippedEnd, searchBox, entityPredicate);
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

    protected HitDecision onHitEntityDecision(EntityHitResult hitResult) {
        return HitDecision.STOP;
    }

    protected HitDecision onHitBlockDecision(BlockHitResult hitResult) {
        return HitDecision.STOP;
    }

    protected PortalBlockDecision handlePortalOrGatewayHit(BlockHitResult hitResult) {
        BlockPos blockPos = hitResult.getBlockPos();
        BlockState blockState = this.level().getBlockState(blockPos);
        if (blockState.is(Blocks.NETHER_PORTAL)) {
            this.handleInsidePortal(blockPos);
            return PortalBlockDecision.HANDLED_PASS_THROUGH;
        }
        if (!blockState.is(Blocks.END_GATEWAY)) {
            return PortalBlockDecision.NOT_HANDLED;
        }
        BlockEntity blockEntity = this.level().getBlockEntity(blockPos);
        if (blockEntity instanceof TheEndGatewayBlockEntity gatewayBlockEntity && TheEndGatewayBlockEntity.canEntityTeleport(this)) {
            TheEndGatewayBlockEntity.teleportEntity(this.level(), blockPos, blockState, this, gatewayBlockEntity);
        }
        return PortalBlockDecision.HANDLED_PASS_THROUGH;
    }

    protected boolean canHitBlock(BlockHitResult hitResult) {
        return true;
    }

    protected int getMaxPierceCount() {
        return 0;
    }

    protected boolean canPierceEntity(Entity entity) {
        return true;
    }

    protected void setNoPhysicsLike(boolean noPhysicsLike) {
        this.noPhysicsLike = noPhysicsLike;
    }

    protected boolean isNoPhysicsLike() {
        return this.noPhysicsLike;
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        if (!target.canBeHitByProjectile()) {
            return false;
        }
        Entity owner = this.getOwner();
        return owner == null || this.leftOwnerLike || !owner.isPassengerOfSameVehicle(target);
    }

    private boolean checkLeftOwnerLike() {
        Entity owner = this.getOwner();
        if (owner == null) {
            return true;
        }
        for (Entity candidate : this.level().getEntities(
                this,
                this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D),
                entity -> !entity.isSpectator() && entity.isPickable())) {
            if (candidate.getRootVehicle() == owner.getRootVehicle()) {
                return false;
            }
        }
        return true;
    }

    private HitDecision sanitizeHitDecision(HitDecision decision) {
        if (decision == HitDecision.BOUNCE || decision == HitDecision.IGNORE) {
            // TODO: 实现完整的反弹以及忽略策略
            LOGGER.warn("Hit decision {} is disabled in BaseProjectile first stage, fallback to STOP for entity {}", decision, this.getId());
            return HitDecision.STOP;
        }
        return decision;
    }

    private Vec3 advancePastHit(Vec3 hitLocation, Vec3 remainingDelta) {
        double lengthSqr = remainingDelta.lengthSqr();
        if (lengthSqr < 1.0E-12D) {
            return null;
        }
        Vec3 direction = remainingDelta.scale(1.0D / Math.sqrt(lengthSqr));
        return hitLocation.add(direction.scale(COLLISION_ADVANCE_EPSILON));
    }

    private boolean canPassThroughEntity(Entity entity) {
        if (!this.canPierceEntity(entity)) {
            return false;
        }
        int maxPierceCount = this.getMaxPierceCount();
        if (maxPierceCount < 0) {
            return true;
        }
        return this.piercedEntityCount < maxPierceCount;
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
        if (!PhysicsMath.isFinite(velocity)) {
            this.kineticsState.setVelocity(0.0d, 0.0d, 0.0d);
            this.setDeltaMovement(Vec3.ZERO);
            return;
        }
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
        this.markVelocitySyncDirty();
    }

    @Override
    public Vector3d getVelocity(Vector3d dest) {
        return this.kineticsState.getVelocity(dest);
    }

    @Override
    public void addVelocity(double x, double y, double z) {
        this.kineticsState.addVelocity(x, y, z);
        this.syncEntityVelocityFromKinetics();
        this.markVelocitySyncDirty();
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
        this.entityData.define(PROJECTILE_RUNTIME_MODIFIER, new ArrayList<>());
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (this.level().isClientSide && PROJECTILE_RUNTIME_MODIFIER.equals(key)) {
            List<ParaOpId> paraOpIds = this.entityData.get(PROJECTILE_RUNTIME_MODIFIER);
            if (paraOpIds.isEmpty()) {
                return;
            }
            this.recordedOperators.clear();
            for (ParaOpId id : paraOpIds) {
                ParaOperator operator = AllParaOperators.createOperator(id);
                this.recordedOperators.add(operator);
            }
            this.rebuildAllOperators();
        }
    }

    public static void writeRecordedOperators(DataCodec codec, Collection<ParaOpId> value) {
        ParaOpId[] paraOpIds = value.stream()
                .filter(Objects::nonNull)
                .toArray(ParaOpId[]::new);
        codec.writeObjectArray("recordedOperators", paraOpIds);
    }

    public static ParaOpId[] readRecordedOperators(DataCodec codec) {
        IDataSerializable[] array = codec.readObjectArray("recordedOperators", ParaOpId::fromCodec);
        return DataCodec.castObjectArray(array, ParaOpId[]::new);
    }

    @PlatformScope(PlatformScopeType.SERVER)
    public void markVelocitySyncDirty() {
        this.hasImpulse = true;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        CompoundTag tag = new CompoundTag();
        tag.putFloat("age", this.age);
        tag.putBoolean("hasBeenShotLike", this.hasBeenShotLike);
        tag.putBoolean("leftOwnerLike", this.leftOwnerLike);
        tag.putBoolean("noPhysicsLike", this.noPhysicsLike);
        tag.putInt("piercedEntityCount", this.piercedEntityCount);

        NBTCodec codec = new NBTCodec(tag);
//        ParaOpId[] paraOpIds = this.recordedOperators.stream()
//                .filter(Objects::nonNull)
//                .map(ParaOperator::getId)
//                .toArray(ParaOpId[]::new);
//        codec.writeObjectArray("recordedOperators", paraOpIds);
        List<ParaOpId> list = this.recordedOperators.stream()
                .map(ParaOperator::getId)
                .toList();
        writeRecordedOperators(codec, list);
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
        this.age = tag.getFloat("age");
        this.hasBeenShotLike = tag.getBoolean("hasBeenShotLike");
        this.leftOwnerLike = tag.getBoolean("leftOwnerLike");
        this.noPhysicsLike = tag.getBoolean("noPhysicsLike");
        this.piercedEntityCount = tag.getInt("piercedEntityCount");

        NBTCodec codec = new NBTCodec(tag);
        try {
//            IDataSerializable[] array = codec.readObjectArray("recordedOperators", ParaOpId::fromCodec);
            ParaOpId[] paraOperators = readRecordedOperators(codec);
            this.recordedOperators.clear();
            for (var id : paraOperators) {
                ParaOperator operator = AllParaOperators.createOperator(id);
                if (operator != null) {
                    this.recordedOperators.add(operator);
                }
            }
            PhysicsState state = codec.readObject("kineticsState", PhysicsState::fromCodec);
            this.kineticsState.set(state);
        } catch (ArrayIndexOutOfBoundsException e) {
            LOGGER.warn("Failed to read recorded operators for projectile {}. The data might be corrupted or from an older version.", this.getId(), e);
        }
        this.rebuildAllOperators();
    }

    private void rebuildAllOperators() {
        for (ParaOperator operator : this.recordedOperators) {
            if (operator instanceof ModifierOperator modifier) {
                modifier.rebuild(this);
            }
        }
    }
}
