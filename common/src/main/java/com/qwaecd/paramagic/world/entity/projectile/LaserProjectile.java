package com.qwaecd.paramagic.world.entity.projectile;

import com.qwaecd.paramagic.core.particle.emitter.impl.LineEmitter;
import com.qwaecd.paramagic.particle.client.shared.BuiltinSharedGPUEffects;
import com.qwaecd.paramagic.particle.client.shared.SharedGPUEffectRef;
import com.qwaecd.paramagic.particle.client.shared.SharedGPUEffectRegistry;
import com.qwaecd.paramagic.thaumaturgy.ProjectileEntity;
import com.qwaecd.paramagic.thaumaturgy.kinetics.ProjectileGravityMutable;
import com.qwaecd.paramagic.thaumaturgy.kinetics.ProjectileInaccuracyMutable;
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
import com.qwaecd.paramagic.world.entity.ModEntityTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static com.qwaecd.paramagic.core.particle.emitter.property.key.AllEmitterProperties.BASE_VELOCITY;
import static com.qwaecd.paramagic.core.particle.emitter.property.key.AllEmitterProperties.BLOOM_INTENSITY;
import static com.qwaecd.paramagic.core.particle.emitter.property.key.AllEmitterProperties.COLOR;
import static com.qwaecd.paramagic.core.particle.emitter.property.key.AllEmitterProperties.END_POSITION;
import static com.qwaecd.paramagic.core.particle.emitter.property.key.AllEmitterProperties.LIFE_TIME_RANGE;
import static com.qwaecd.paramagic.core.particle.emitter.property.key.AllEmitterProperties.SIZE_RANGE;

public class LaserProjectile extends ThrowableProjectile implements ProjectileEntity, ProjectileVelocityMutable, ProjectileInaccuracyMutable, ProjectilePersistentAccelerationMutable, ProjectileLinearDampingMutable, ProjectileSpeedLimitMutable, ProjectileGravityMutable, ProjectileRuntimeModifierHost {
    private static final float CLIENT_EMITTER_DELTA_TIME = 1.0f / 20.0f;
    private static final SharedGPUEffectRef LASER_EFFECT = SharedGPUEffectRegistry.ref(BuiltinSharedGPUEffects.LASER_BEAM);
    private static final float BEAM_LENGTH = 6.5f;
    private static final int MAX_LIFE_TICKS = 20 * 5;

    private float HIT_DAMAGE = 5.0f;

    protected final ProjectileKineticsState kineticsState = new ProjectileKineticsState();
    protected final ProjectileKineticsAccumulator kineticsAccumulator = new ProjectileKineticsAccumulator();
    protected final List<ProjectileRuntimeModifier> runtimeModifiers = new ArrayList<>();
    private LineEmitter sharedBeamEmitter;

    public LaserProjectile(EntityType<? extends LaserProjectile> entityType, Level level) {
        super(entityType, level);
        this.setNoGravity(true);
        this.kineticsState.setGravityScale(0.0f);
        this.kineticsState.setMaxSpeed(64.0f);
    }

    public LaserProjectile(Level level) {
        this(ModEntityTypes.LASER_PROJECTILE, level);
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
        if (hitResult != null && hitResult.getType() != HitResult.Type.MISS) {
            this.setPos(hitResult.getLocation());
            this.onHit(hitResult);
        } else {
            this.setPos(end);
        }

        if (this.tickCount >= MAX_LIFE_TICKS) {
            this.discard();
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (hitResult instanceof EntityHitResult entityHitResult) {
            Entity target = entityHitResult.getEntity();
            target.hurt(this.damageSources().magic(), HIT_DAMAGE);
        }
        this.discard();
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

    public void renderBeamEffect(float partialTick) {
        Vec3 position = this.getPosition(partialTick);
        Vec3 velocity = this.getDeltaMovement();
        if (velocity.lengthSqr() <= 1.0e-8) {
            return;
        }

        Vector3f start = new Vector3f((float) position.x, (float) position.y, (float) position.z);
        Vector3f direction = new Vector3f((float) velocity.x, (float) velocity.y, (float) velocity.z).normalize();
        Vector3f end = new Vector3f(direction).mul(BEAM_LENGTH).add(start);

        LineEmitter emitter = this.getOrCreateSharedBeamEmitter();
        emitter.moveTo(start);
        emitter.getProperty(END_POSITION).modify(v -> v.set(end));
        emitter.getProperty(BASE_VELOCITY).modify(v -> v.set(0.0f, 0.0f, 0.0f));
        LASER_EFFECT.submitFromEmitter(emitter, CLIENT_EMITTER_DELTA_TIME);
    }

    private LineEmitter getOrCreateSharedBeamEmitter() {
        if (this.sharedBeamEmitter != null) {
            return this.sharedBeamEmitter;
        }

        LineEmitter emitter = new LineEmitter(new Vector3f(), 80.0f);
        emitter.getProperty(COLOR).modify(v -> v.set(0.9f, 0.35f, 1.95f, 1.0f));
        emitter.getProperty(LIFE_TIME_RANGE).modify(v -> v.set(0.5f, 0.9f));
        emitter.getProperty(SIZE_RANGE).modify(v -> v.set(1.4f, 2.8f));
        emitter.getProperty(BLOOM_INTENSITY).set(1.8f);
        this.sharedBeamEmitter = emitter;
        return emitter;
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
    public void shoot() {
        Vec3 position = this.position();
        Vector3f velocity = this.kineticsState.getVelocity();
        float speed = velocity.length();
        if (speed > 1.0e-6f) {
            this.shoot(velocity.x, velocity.y, velocity.z, speed, this.kineticsState.getInaccuracy());
            Vec3 deltaMovement = this.getDeltaMovement();
            this.kineticsState.setVelocity((float) deltaMovement.x, (float) deltaMovement.y, (float) deltaMovement.z);
        } else {
            this.syncEntityVelocityFromKinetics();
        }
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
    protected void defineSynchedData() {
    }

}
