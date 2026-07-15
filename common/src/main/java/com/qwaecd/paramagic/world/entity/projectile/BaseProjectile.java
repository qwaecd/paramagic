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
import com.qwaecd.paramagic.world.sound.SoundHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.*;
import org.joml.Vector3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

public abstract class BaseProjectile extends ThrowableProjectile implements ProjectileEntity, PhysicsProvider, ProjectileRuntimeModifierHost {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseProjectile.class);

    protected static final EntityDataAccessor<List<ParaOpId>> PROJECTILE_RUNTIME_MODIFIER = SynchedEntityData.defineId(BaseProjectile.class, AllEntityDataSerializers.PROJECTILE_RUNTIME_MODIFIER);
    private static final EntityDataAccessor<Integer> MAX_ENTITY_PIERCE_COUNT = SynchedEntityData.defineId(BaseProjectile.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> MAX_BOUNCE_COUNT = SynchedEntityData.defineId(BaseProjectile.class, EntityDataSerializers.INT);

    private static final int MAX_COLLISION_ITERATIONS = 16;
    private static final double COLLISION_ADVANCE_EPSILON = 1.0E-2D;
    private static final double ENTITY_COLLISION_INFLATION = 0.3D;

    protected float age = 0.0f;

    protected final PhysicsState kineticsState;
    protected final KineticsAccumulator kineticsAccumulator = new KineticsAccumulator();
    protected final List<ProjectileRuntimeModifier> runtimeModifiers = new ArrayList<>();

    /**
     * 仅修改该字段会导致双端不同步，应当使用 entityData 来进行同步修改
     */
    @PlatformScope(PlatformScopeType.COMMON)
    protected final List<ParaOperator> recordedOperators = new ArrayList<>();

    protected float inaccuracy = 0.0f;
    protected int maxEntityPierceCount = 0;
    protected int maxBounceCount = 0;


    private boolean hasBeenShot = false;
    private boolean leftOwnerLike = false;
    private boolean ignoresWorldCollision = false;
    private int piercedEntityCount = 0;
    private int bounceCount = 0;

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

    @PlatformScope(PlatformScopeType.SERVER)
    protected void onLifeEnd() {
        this.discard();
    }

    @Override
    public void tick() {
        this.runBaseLifecycleTick();
        this.age += 1.0f / 20.0f;

        if (this.level().isClientSide) {
            if (!this.hasBeenShot) {
                this.syncVelocityFromEntity();
                this.playShootSound();
            }
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
        if (!this.hasBeenShot) {
            this.gameEvent(GameEvent.PROJECTILE_SHOOT, this.getOwner());
            this.hasBeenShot = true;
        }
        if (!this.leftOwnerLike) {
            this.leftOwnerLike = this.checkLeftOwnerLike();
        }
    }

    protected MovementPlan resolveMovement(Vec3 start, Vec3 delta) {
        // Tick-driven movement: x += v (v is blocks/tick).
        return new MovementPlan(start, delta);
    }

    protected CollisionResolveResult processCollisionPipeline(MovementPlan plan) {
        if (this.ignoresWorldCollision() || plan.delta().lengthSqr() < 1.0E-12D) {
            return new CollisionResolveResult(plan.end());
        }

        Vec3 cursor = plan.start();
        Vec3 remainingDelta = plan.delta();
        // This set only prevents the same entity from being resolved twice in
        // one movement segment. It is deliberately reset every tick so a
        // projectile may resolve that target again on later ticks.
        Set<Integer> resolvedEntityIds = new HashSet<>();
        int iterations = 0;
        while (iterations++ < MAX_COLLISION_ITERATIONS && remainingDelta.lengthSqr() >= 1.0E-12D) {
            Vec3 segmentEnd = cursor.add(remainingDelta);
            HitResult hitResult = this.detectFirstHit(cursor, segmentEnd, entity -> this.canHitEntity(entity) && !resolvedEntityIds.contains(entity.getId()));
            if (hitResult.getType() == HitResult.Type.MISS) {
                cursor = segmentEnd;
                break;
            }

            Vec3 hitLocation = hitResult.getLocation();
            // block
            if (hitResult instanceof BlockHitResult blockHitResult) {
                if (this.isPortalOrGateway(blockHitResult)) {
                    if (this.handlePortalOrGatewayHit(blockHitResult)) {
                        return new CollisionResolveResult(this.position());
                    }
                    Vec3 nextStart = this.advancePastHit(hitLocation, remainingDelta);
                    if (nextStart == null) {
                        cursor = hitLocation;
                        break;
                    }
                    remainingDelta = segmentEnd.subtract(nextStart);
                    cursor = nextStart;
                    continue;
                }
                HitDecision decision = this.onHitBlockDecision(blockHitResult);
                if (decision == HitDecision.IGNORE) {
                    Vec3 nextStart = this.advancePastHit(hitLocation, remainingDelta);
                    if (nextStart == null) {
                        cursor = hitLocation;
                        break;
                    }
                    remainingDelta = segmentEnd.subtract(nextStart);
                    cursor = nextStart;
                    continue;
                }
                BounceResult bounceResult = decision == HitDecision.BOUNCE
                        ? this.calculateBounce(
                                Vec3.atLowerCornerOf(blockHitResult.getDirection().getNormal()),
                                this.getDeltaMovement(),
                                segmentEnd.subtract(hitLocation),
                                this.getBounceRestitution(blockHitResult)
                        )
                        : null;
                CollisionResolution resolution = this.resolveCollision(decision, decision == HitDecision.PASS_THROUGH, bounceResult);
                this.setPos(hitLocation);
                this.onHit(blockHitResult);
                this.onCollisionResolved(blockHitResult, resolution);
                if (this.isRemoved()) {
                    return new CollisionResolveResult(hitLocation);
                }
                if (resolution.action() == HitDecision.PASS_THROUGH) {
                    Vec3 nextStart = this.advancePastHit(hitLocation, remainingDelta);
                    if (nextStart == null) {
                        cursor = hitLocation;
                        break;
                    }
                    remainingDelta = segmentEnd.subtract(nextStart);
                    cursor = nextStart;
                    continue;
                }
                if (resolution.action() == HitDecision.BOUNCE) {
                    BounceContinuation continuation = this.applyBounce(hitLocation, bounceResult);
                    if (continuation != null) {
                        cursor = continuation.nextStart();
                        remainingDelta = continuation.remainingDelta();
                        continue;
                    }
                }
                this.finishStoppedCollision(blockHitResult, resolution);
                cursor = hitLocation;
                break;
            }

            // entity
            if (hitResult instanceof EntityHitResult entityHitResult) {
                HitDecision decision = this.onHitEntityDecision(entityHitResult);
                if (decision == HitDecision.IGNORE) {
                    resolvedEntityIds.add(entityHitResult.getEntity().getId());
                    continue;
                }
                boolean piercesEntity = (decision == HitDecision.PASS_THROUGH || decision == HitDecision.BOUNCE)
                        && this.canPassThroughEntity(entityHitResult.getEntity());
                Vec3 bounceNormal = !piercesEntity && decision == HitDecision.BOUNCE
                        ? this.getEntityBounceNormal(entityHitResult.getEntity(), cursor, segmentEnd, hitLocation)
                        : null;
                BounceResult bounceResult = bounceNormal == null ? null : this.calculateBounce(
                        bounceNormal,
                        this.getDeltaMovement(),
                        segmentEnd.subtract(hitLocation),
                        this.getEntityBounceRestitution(entityHitResult)
                );
                CollisionResolution resolution = this.resolveCollision(decision, piercesEntity, bounceResult);
                this.setPos(hitLocation);
                this.onHit(entityHitResult);
                this.onCollisionResolved(entityHitResult, resolution);
                if (this.isRemoved()) {
                    return new CollisionResolveResult(hitLocation);
                }
                if (resolution.action() == HitDecision.PASS_THROUGH) {
                    resolvedEntityIds.add(entityHitResult.getEntity().getId());
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
                if (resolution.action() == HitDecision.BOUNCE) {
                    resolvedEntityIds.add(entityHitResult.getEntity().getId());
                    BounceContinuation continuation = this.applyBounce(hitLocation, bounceResult);
                    if (continuation != null) {
                        cursor = continuation.nextStart();
                        remainingDelta = continuation.remainingDelta();
                        continue;
                    }
                }
                this.finishStoppedCollision(entityHitResult, resolution);
                cursor = hitLocation;
                break;
            }

            cursor = hitLocation;
            break;
        }
        return new CollisionResolveResult(cursor);
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

    @PlatformScope(PlatformScopeType.CLIENT)
    protected void playShootSound() {
        SoundHelper.playLocalSound(
                this.level(),
                this.getX(), this.getY(), this.getZ(),
                this.getShootSound(),
                SoundSource.PLAYERS,
                1.0f,
                1.0F / (this.random.nextFloat() * 0.2F + 0.9F)
        );
    }

    @Nullable
    protected SoundEvent getShootSound() {
        return null;
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

    @Override
    public void lerpMotion(double x, double y, double z) {
        super.lerpMotion(x, y, z);
        if (this.level().isClientSide) {
            this.syncVelocityFromEntity();
        }
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

    @Override
    protected final void onHit(HitResult result) {
        super.onHit(result);
    }

    protected void onCollisionResolved(HitResult hitResult, CollisionResolution resolution) {
    }

    protected boolean shouldDiscardAfterCollision(HitResult hitResult, CollisionResolution resolution) {
        return resolution.action() == HitDecision.STOP;
    }

    /**
     * Resolves entity-hit movement on both logical sides. Implementations must
     * be deterministic and free of gameplay side effects.
     * If {@link HitDecision#BOUNCE} is selected for an entity that is still
     * eligible for piercing, piercing takes priority over bounce processing.
     * {@link HitDecision#IGNORE} skips this entity for the rest of the tick
     * without invoking hit callbacks.
     */
    protected HitDecision onHitEntityDecision(EntityHitResult hitResult) {
        if (this.getMaxEntityPierceCount() != 0 && this.canPierceEntity(hitResult.getEntity())) {
            return HitDecision.PASS_THROUGH;
        }
        return this.canBounce() ? HitDecision.BOUNCE : HitDecision.STOP;
    }

    /**
     * Resolves block-hit movement on both logical sides. Implementations must
     * be deterministic and free of gameplay side effects. {@link HitDecision#BOUNCE}
     * is supported for block collisions.
     */
    protected HitDecision onHitBlockDecision(BlockHitResult hitResult) {
        return this.canBounce() ? HitDecision.BOUNCE : HitDecision.STOP;
    }

    /**
     * Returns the total number of bounces allowed for this projectile. A value
     * less than or equal to zero disables bouncing.
     */
    @Override
    public int getMaxBounceCount() {
        return this.maxBounceCount;
    }

    @Override
    public void setMaxBounceCount(int count) {
        this.maxBounceCount = Math.max(0, count);
        if (!this.level().isClientSide) {
            this.entityData.set(MAX_BOUNCE_COUNT, this.maxBounceCount);
        }
    }

    /**
     * Returns the speed multiplier applied after this block bounce. Values
     * less than or equal to zero stop the projectile after the bounce.
     */
    protected double getBounceRestitution(BlockHitResult hitResult) {
        return 0.8d;
    }

    /**
     * Returns the speed multiplier applied after this entity bounce. Values
     * less than or equal to zero stop the projectile after the bounce.
     */
    protected double getEntityBounceRestitution(EntityHitResult hitResult) {
        return 0.6d;
    }

    private boolean handlePortalOrGatewayHit(BlockHitResult hitResult) {
        BlockPos blockPos = hitResult.getBlockPos();
        BlockState blockState = this.level().getBlockState(blockPos);
        if (blockState.is(Blocks.NETHER_PORTAL)) {
            this.handleInsidePortal(blockPos);
            return false;
        }
        BlockEntity blockEntity = this.level().getBlockEntity(blockPos);
        if (blockEntity instanceof TheEndGatewayBlockEntity gatewayBlockEntity && TheEndGatewayBlockEntity.canEntityTeleport(this)) {
            TheEndGatewayBlockEntity.teleportEntity(this.level(), blockPos, blockState, this, gatewayBlockEntity);
            return true;
        }
        return false;
    }

    private boolean isPortalOrGateway(BlockHitResult hitResult) {
        BlockState blockState = this.level().getBlockState(hitResult.getBlockPos());
        return blockState.is(Blocks.NETHER_PORTAL) || blockState.is(Blocks.END_GATEWAY);
    }

    /**
     * Returns the total number of entities this projectile may pass through.
     * Zero disables entity piercing; a negative value permits unlimited
     * piercing for the projectile's lifetime.
     */
    @Override
    public int getMaxEntityPierceCount() {
        return this.maxEntityPierceCount;
    }

    @Override
    public void setMaxEntityPierceCount(int count) {
        this.maxEntityPierceCount = count;
        if (!this.level().isClientSide) {
            this.entityData.set(MAX_ENTITY_PIERCE_COUNT, count);
        }
    }

    /**
     * Determines whether this entity may consume one entity-pierce allowance.
     */
    protected boolean canPierceEntity(Entity entity) {
        return true;
    }

    protected void setIgnoresWorldCollision(boolean ignoresWorldCollision) {
        this.ignoresWorldCollision = ignoresWorldCollision;
    }

    protected boolean ignoresWorldCollision() {
        return this.ignoresWorldCollision;
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

    private Vec3 advancePastHit(Vec3 hitLocation, Vec3 remainingDelta) {
        double lengthSqr = remainingDelta.lengthSqr();
        if (lengthSqr <= COLLISION_ADVANCE_EPSILON * COLLISION_ADVANCE_EPSILON) {
            return null;
        }
        Vec3 direction = remainingDelta.scale(1.0D / Math.sqrt(lengthSqr));
        return hitLocation.add(direction.scale(COLLISION_ADVANCE_EPSILON));
    }

    private boolean canPassThroughEntity(Entity entity) {
        if (!this.canPierceEntity(entity)) {
            return false;
        }
        int maxPierceCount = this.getMaxEntityPierceCount();
        if (maxPierceCount < 0) {
            return true;
        }
        return this.piercedEntityCount < maxPierceCount;
    }

    private CollisionResolution resolveCollision(HitDecision decision, boolean passesThrough, @Nullable BounceResult bounceResult) {
        if (passesThrough) {
            return new CollisionResolution(HitDecision.PASS_THROUGH);
        }
        if (decision == HitDecision.BOUNCE && bounceResult != null) {
            return new CollisionResolution(HitDecision.BOUNCE);
        }
        return new CollisionResolution(HitDecision.STOP);
    }

    @Nullable
    private BounceResult calculateBounce(Vec3 normal, Vec3 incomingVelocity, Vec3 remainingDelta, double restitution) {
        if (!this.canBounce() || normal.lengthSqr() < 1.0E-12D) {
            return null;
        }
        normal = normal.normalize();
        if (incomingVelocity.dot(normal) >= 0.0D) {
            return null;
        }
        if (!Double.isFinite(restitution)) {
            return null;
        }
        restitution = Math.max(0.0D, restitution);
        Vec3 reflectedVelocity = this.reflect(incomingVelocity, normal).scale(restitution);
        Vec3 reflectedRemainingDelta = this.reflect(remainingDelta, normal).scale(restitution);
        if (reflectedRemainingDelta.lengthSqr() <= COLLISION_ADVANCE_EPSILON * COLLISION_ADVANCE_EPSILON) {
            return null;
        }
        return new BounceResult(reflectedVelocity, reflectedRemainingDelta);
    }

    @Nullable
    private BounceContinuation applyBounce(Vec3 hitLocation, @Nullable BounceResult bounceResult) {
        if (bounceResult == null) {
            return null;
        }
        Vec3 velocity = bounceResult.reflectedVelocity();
        this.setVelocity(velocity.x, velocity.y, velocity.z, !this.level().isClientSide);
        this.bounceCount++;
        Vec3 nextStart = this.advancePastHit(hitLocation, bounceResult.reflectedRemainingDelta());
        if (nextStart == null) {
            return null;
        }
        return new BounceContinuation(nextStart, bounceResult.reflectedRemainingDelta().subtract(nextStart.subtract(hitLocation)));
    }

    private void finishStoppedCollision(HitResult hitResult, CollisionResolution resolution) {
        this.setVelocity(0.0D, 0.0D, 0.0D, !this.level().isClientSide);
        if (!this.level().isClientSide && this.shouldDiscardAfterCollision(hitResult, resolution)) {
            this.discard();
        }
    }

    private Vec3 getEntityBounceNormal(Entity target, Vec3 start, Vec3 end, Vec3 hitLocation) {
        AABB bounds = target.getBoundingBox().inflate(ENTITY_COLLISION_INFLATION);
        Vec3 delta = end.subtract(start);
        AxisEntry xEntry = this.findAxisEntry(start.x, delta.x, bounds.minX, bounds.maxX, new Vec3(-1.0D, 0.0D, 0.0D), new Vec3(1.0D, 0.0D, 0.0D));
        AxisEntry yEntry = this.findAxisEntry(start.y, delta.y, bounds.minY, bounds.maxY, new Vec3(0.0D, -1.0D, 0.0D), new Vec3(0.0D, 1.0D, 0.0D));
        AxisEntry zEntry = this.findAxisEntry(start.z, delta.z, bounds.minZ, bounds.maxZ, new Vec3(0.0D, 0.0D, -1.0D), new Vec3(0.0D, 0.0D, 1.0D));
        AxisEntry entry = this.latestEntry(xEntry, yEntry, zEntry);
        if (entry != null && entry.time() >= 0.0D && entry.time() <= 1.0D) {
            return entry.normal();
        }
        return this.nearestAabbFaceNormal(bounds, hitLocation);
    }

    @Nullable
    private AxisEntry findAxisEntry(double origin, double delta, double min, double max, Vec3 minNormal, Vec3 maxNormal) {
        if (Math.abs(delta) < 1.0E-12D) {
            return null;
        }
        double minTime = (min - origin) / delta;
        double maxTime = (max - origin) / delta;
        return minTime <= maxTime ? new AxisEntry(minTime, minNormal) : new AxisEntry(maxTime, maxNormal);
    }

    @Nullable
    private AxisEntry latestEntry(@Nullable AxisEntry... entries) {
        if (entries == null) {
            return null;
        }
        AxisEntry latest = null;
        for (AxisEntry entry : entries) {
            if (entry != null && (latest == null || entry.time() > latest.time())) {
                latest = entry;
            }
        }
        return latest;
    }

    private Vec3 nearestAabbFaceNormal(AABB bounds, Vec3 location) {
        double xMinDistance = Math.abs(location.x - bounds.minX);
        double xMaxDistance = Math.abs(bounds.maxX - location.x);
        double yMinDistance = Math.abs(location.y - bounds.minY);
        double yMaxDistance = Math.abs(bounds.maxY - location.y);
        double zMinDistance = Math.abs(location.z - bounds.minZ);
        double zMaxDistance = Math.abs(bounds.maxZ - location.z);
        double nearest = xMinDistance;
        Vec3 normal = new Vec3(-1.0D, 0.0D, 0.0D);
        if (xMaxDistance < nearest) {
            nearest = xMaxDistance;
            normal = new Vec3(1.0D, 0.0D, 0.0D);
        }
        if (yMinDistance < nearest) {
            nearest = yMinDistance;
            normal = new Vec3(0.0D, -1.0D, 0.0D);
        }
        if (yMaxDistance < nearest) {
            nearest = yMaxDistance;
            normal = new Vec3(0.0D, 1.0D, 0.0D);
        }
        if (zMinDistance < nearest) {
            nearest = zMinDistance;
            normal = new Vec3(0.0D, 0.0D, -1.0D);
        }
        if (zMaxDistance < nearest) {
            normal = new Vec3(0.0D, 0.0D, 1.0D);
        }
        return normal;
    }

    private boolean canBounce() {
        int maxBounceCount = this.getMaxBounceCount();
        return maxBounceCount > 0 && this.bounceCount < maxBounceCount;
    }

    private Vec3 reflect(Vec3 vector, Vec3 normal) {
        return vector.subtract(normal.scale(2.0D * vector.dot(normal)));
    }

    private record AxisEntry(double time, Vec3 normal) {
    }

    private record BounceResult(Vec3 reflectedVelocity, Vec3 reflectedRemainingDelta) {
    }

    private record BounceContinuation(Vec3 nextStart, Vec3 remainingDelta) {
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

    @Override
    protected void defineSynchedData() {
        this.entityData.define(PROJECTILE_RUNTIME_MODIFIER, new ArrayList<>());
        this.entityData.define(MAX_ENTITY_PIERCE_COUNT, 0);
        this.entityData.define(MAX_BOUNCE_COUNT, 0);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (MAX_ENTITY_PIERCE_COUNT.equals(key)) {
            this.maxEntityPierceCount = this.entityData.get(MAX_ENTITY_PIERCE_COUNT);
        } else if (MAX_BOUNCE_COUNT.equals(key)) {
            this.maxBounceCount = this.entityData.get(MAX_BOUNCE_COUNT);
        }
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
        tag.putBoolean("hasBeenShot", this.hasBeenShot);
        tag.putBoolean("leftOwnerLike", this.leftOwnerLike);
        tag.putBoolean("ignoresWorldCollision", this.ignoresWorldCollision);
        tag.putInt("maxEntityPierceCount", this.maxEntityPierceCount);
        tag.putInt("maxBounceCount", this.maxBounceCount);
        tag.putInt("piercedEntityCount", this.piercedEntityCount);
        tag.putInt("bounceCount", this.bounceCount);

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
        this.hasBeenShot = tag.getBoolean("hasBeenShot");
        this.leftOwnerLike = tag.getBoolean("leftOwnerLike");
        this.ignoresWorldCollision = tag.getBoolean("ignoresWorldCollision");
        this.setMaxEntityPierceCount(tag.getInt("maxEntityPierceCount"));
        this.setMaxBounceCount(tag.getInt("maxBounceCount"));
        this.piercedEntityCount = tag.getInt("piercedEntityCount");
        this.bounceCount = tag.getInt("bounceCount");

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
