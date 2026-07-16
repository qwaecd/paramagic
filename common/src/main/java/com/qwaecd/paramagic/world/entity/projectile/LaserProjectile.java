package com.qwaecd.paramagic.world.entity.projectile;

import com.qwaecd.paramagic.core.particle.emitter.Emitter;
import com.qwaecd.paramagic.core.particle.emitter.impl.PointEmitter;
import com.qwaecd.paramagic.core.particle.emitter.property.type.VelocityModeStates;
import com.qwaecd.paramagic.particle.client.shared.BuiltinSharedGPUEffects;
import com.qwaecd.paramagic.particle.client.shared.SharedGPUEffectRef;
import com.qwaecd.paramagic.particle.client.shared.SharedGPUEffectRegistry;
import com.qwaecd.paramagic.thaumaturgy.ProjectileEntity;
import com.qwaecd.paramagic.thaumaturgy.projectile.property.LifetimeCarrier;
import com.qwaecd.paramagic.world.entity.ModEntityTypes;
import com.qwaecd.paramagic.world.sound.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3f;

import javax.annotation.Nullable;

import java.util.List;

import static com.qwaecd.paramagic.core.particle.emitter.property.key.AllEmitterProperties.*;

public class LaserProjectile extends BaseProjectile implements ProjectileEntity, LifetimeCarrier {
    private static final SharedGPUEffectRef LASER_EFFECT = SharedGPUEffectRegistry.ref(BuiltinSharedGPUEffects.LASER_BEAM);
    private float lifeTime = 5.0f;

    private float HIT_DAMAGE = 5.0f;
    private List<Emitter> sharedBeamEmitters;

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

    @Override
    protected void onCollisionResolved(HitResult hitResult, CollisionResolution resolution) {
        if (this.level().isClientSide) {
            return;
        }
        if (hitResult instanceof EntityHitResult entityHitResult) {
            Entity target = entityHitResult.getEntity();
            target.hurt(this.damageSources().magic(), HIT_DAMAGE);
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
}
