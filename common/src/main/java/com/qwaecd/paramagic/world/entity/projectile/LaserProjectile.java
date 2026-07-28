package com.qwaecd.paramagic.world.entity.projectile;

import com.qwaecd.paramagic.core.particle.emitter.Emitter;
import com.qwaecd.paramagic.core.particle.emitter.EmitterBase;
import com.qwaecd.paramagic.core.particle.emitter.ParticleBurst;
import com.qwaecd.paramagic.core.particle.emitter.impl.CircleEmitter;
import com.qwaecd.paramagic.core.particle.emitter.impl.PointEmitter;
import com.qwaecd.paramagic.core.particle.emitter.property.type.VelocityModeStates;
import com.qwaecd.paramagic.particle.client.shared.BuiltinSharedGPUEffects;
import com.qwaecd.paramagic.particle.client.shared.SharedGPUEffectRef;
import com.qwaecd.paramagic.particle.client.shared.SharedGPUEffectRegistry;
import com.qwaecd.paramagic.thaumaturgy.ProjectileEntity;
import com.qwaecd.paramagic.thaumaturgy.DamagingProjectileEntity;
import com.qwaecd.paramagic.thaumaturgy.projectile.property.LifetimeCarrier;
import com.qwaecd.paramagic.thaumaturgy.projectile.damage.ModDamageTypes;
import com.qwaecd.paramagic.thaumaturgy.projectile.damage.ProjectileDamage;
import com.qwaecd.paramagic.world.entity.ModEntityTypes;
import com.qwaecd.paramagic.world.sound.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.List;

import static com.qwaecd.paramagic.core.particle.emitter.property.key.AllEmitterProperties.*;

public class LaserProjectile extends BaseProjectile implements ProjectileEntity, DamagingProjectileEntity, LifetimeCarrier {
    private static final SharedGPUEffectRef LASER_EFFECT = SharedGPUEffectRegistry.ref(BuiltinSharedGPUEffects.LASER_BEAM);
    private float lifeTime = 5.0f;

    private static final int RING_PARTICLE_COUNT = 48;
    private static final int FLASH_PARTICLE_COUNT = 16;
    private static final float IMPACT_FRAME_DELTA = 1.0f / 20.0f;

    private ProjectileDamage projectileDamage = new ProjectileDamage(ModDamageTypes.ARCANE_PROJECTILE, 0.0f, false);
    private List<Emitter> sharedBeamEmitters;
    private List<EmitterBase> sharedImpactEmitters;

    public LaserProjectile(EntityType<? extends LaserProjectile> entityType, Level level) {
        super(entityType, level, 8.0f);
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
        if (this.level().isClientSide) {
            return;
        }
        if (this.age > this.lifeTime) {
            this.onLifeEnd();
        }
    }

    @Nullable
    @Override
    protected SoundEvent getShootSound() {
        return ModSounds.LASER;
    }

    public void renderBeamEffect(float partialTick, float deltaTime) {
        Vector3f position = this.getPosition(partialTick).toVector3f();
        Vec3 velocity = this.getDeltaMovement();
        if (velocity.lengthSqr() <= 1.0e-8) {
            return;
        }
        List<Emitter> emitters = this.getOrCreateSharedBeamEmitter();
        for (Emitter emi : emitters) {
            emi.moveTo(position);
            LASER_EFFECT.submitFromEmitter(emi, deltaTime);
        }
    }

    private List<Emitter> getOrCreateSharedBeamEmitter() {
        if (this.sharedBeamEmitters != null) {
            return this.sharedBeamEmitters;
        }

        Emitter emitter1 = new PointEmitter(new Vector3f(), 160.0f);
        emitter1.modifyProp(BASE_VELOCITY, v -> v.set(0.5f));
        emitter1.modifyProp(COLOR, v -> v.set(0.9f, 0.35f, 1.95f, 1.0f));
        emitter1.modifyProp(LIFE_TIME_RANGE, v -> v.set(0.5f, 0.9f));
        emitter1.modifyProp(SIZE_RANGE, v -> v.set(0.0014f, 0.0028f));
        emitter1.trySet(BLOOM_INTENSITY, 1.8f);
        emitter1.trySet(VELOCITY_SPREAD, 180.0f);
        emitter1.trySet(VELOCITY_MODE, VelocityModeStates.CONE);

        Emitter emitter2 = new PointEmitter(new Vector3f(), 200.0f);
        emitter2.modifyProp(BASE_VELOCITY, v -> v.set(0.01f));
        emitter2.modifyProp(COLOR, v -> v.set(1.95f, 0.35f, 0.95f, 1.0f));
        emitter2.modifyProp(LIFE_TIME_RANGE, v -> v.set(0.5f, 2.9f));
        emitter2.modifyProp(SIZE_RANGE, v -> v.set(0.0024f, 0.0048f));
        emitter2.trySet(BLOOM_INTENSITY, 1.5f);
        emitter2.trySet(VELOCITY_SPREAD, 15.0f);
        emitter2.trySet(VELOCITY_MODE, VelocityModeStates.RANDOM);
        this.sharedBeamEmitters = List.of(emitter1, emitter2);
        return sharedBeamEmitters;
    }

    @Override
    protected void onCollisionResolved(HitResult hitResult, CollisionResolution resolution) {
        if (!this.level().isClientSide) {
            return;
        }
        Vec3 hitLocation = hitResult.getLocation();
        Vector3f normal = this.resolveImpactNormal(hitResult);
        Vector3f position = hitLocation.toVector3f();
        List<EmitterBase> emitters = this.getOrCreateSharedImpactEmitters();
        for (EmitterBase emi : emitters) {
            emi.moveTo(position);
            if (emi instanceof CircleEmitter circle) {
                circle.setProperty(NORMAL, normal);
            } else if (emi instanceof PointEmitter point) {
                point.setProperty(BASE_VELOCITY, new Vector3f(normal).mul(1.8f));
            }
            emi.clearBursts();
            emi.addBurst(new ParticleBurst(0.0f, emi instanceof CircleEmitter ? RING_PARTICLE_COUNT : FLASH_PARTICLE_COUNT));
            LASER_EFFECT.submitFromEmitter(emi, IMPACT_FRAME_DELTA);
        }
    }

    private Vector3f resolveImpactNormal(HitResult hitResult) {
        Vec3 normal;
        if (hitResult instanceof BlockHitResult blockHit) {
            normal = Vec3.atLowerCornerOf(blockHit.getDirection().getNormal());
        } else if (hitResult instanceof EntityHitResult entityHit) {
            Vec3 center = entityHit.getEntity().position();
            Vec3 dir = hitResult.getLocation().subtract(center);
            if (dir.lengthSqr() < 1.0E-8D) {
                dir = this.getDeltaMovement().scale(-1.0D);
            }
            normal = dir.normalize();
        } else {
            normal = this.getDeltaMovement().scale(-1.0D);
        }
        Vector3f n = normal.lengthSqr() < 1.0E-8D
                ? new Vector3f(0.0f, 1.0f, 0.0f)
                : new Vector3f((float) normal.x, (float) normal.y, (float) normal.z);
        return n.normalize();
    }

    private List<EmitterBase> getOrCreateSharedImpactEmitters() {
        if (this.sharedImpactEmitters != null) {
            return this.sharedImpactEmitters;
        }

        CircleEmitter ring = new CircleEmitter(new Vector3f(), 0.0f);
        ring.modifyProp(COLOR, v -> v.set(0.9f, 0.35f, 1.95f, 1.0f));
        ring.modifyProp(LIFE_TIME_RANGE, v -> v.set(0.3f, 0.6f));
        ring.modifyProp(SIZE_RANGE, v -> v.set(0.01f, 0.02f));
        ring.trySet(BLOOM_INTENSITY, 1.8f);
        ring.trySet(INNER_OUTER_RADIUS, new Vector2f(0.02f, 0.08f));
        ring.trySet(VELOCITY_MODE, VelocityModeStates.RADIAL_FROM_CENTER);
        ring.setProperty(BASE_VELOCITY, new Vector3f(2.0f, 0.0f, 0.0f));

        PointEmitter flash = new PointEmitter(new Vector3f(), 0.0f);
        flash.modifyProp(COLOR, v -> v.set(1.95f, 0.6f, 1.1f, 1.0f));
        flash.modifyProp(LIFE_TIME_RANGE, v -> v.set(0.15f, 0.35f));
        flash.modifyProp(SIZE_RANGE, v -> v.set(0.006f, 0.010f));
        flash.trySet(BLOOM_INTENSITY, 2.2f);
        flash.trySet(VELOCITY_SPREAD, 90.0f);
        flash.trySet(VELOCITY_MODE, VelocityModeStates.CONE);

        this.sharedImpactEmitters = List.of(ring, flash);
        return this.sharedImpactEmitters;
    }

    @Override
    public void shoot() {
        Vec3 position = this.position();
        Vector3d velocity = this.kineticsState.getVelocity();
        double speed = velocity.length();
        if (speed > 1.0e-6f) {
            BaseProjectile.shoot(this, this.random, velocity.x, velocity.y, velocity.z, (float) speed, this.getInaccuracy());
            this.syncEntityVelocityFromKinetics();
        } else {
            this.syncEntityVelocityFromKinetics();
        }
        this.level().addFreshEntity(this);
        this.syncRecordedOperators();
    }

    @Override
    public float getLifetime() {
        return this.lifeTime;
    }

    @Override
    public void setLifetime(float lifetime) {
        this.lifeTime = lifetime;
    }

    @Override
    @Nonnull
    public ProjectileDamage getProjectileDamage() {
        return this.projectileDamage;
    }

    @Override
    public void setProjectileDamage(@Nonnull ProjectileDamage damage) {
        this.projectileDamage = damage;
    }
}
