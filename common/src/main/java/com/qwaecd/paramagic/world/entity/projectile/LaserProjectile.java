package com.qwaecd.paramagic.world.entity.projectile;

import com.qwaecd.paramagic.core.particle.emitter.impl.LineEmitter;
import com.qwaecd.paramagic.particle.client.shared.BuiltinSharedGPUEffects;
import com.qwaecd.paramagic.particle.client.shared.SharedGPUEffectRef;
import com.qwaecd.paramagic.particle.client.shared.SharedGPUEffectRegistry;
import com.qwaecd.paramagic.thaumaturgy.ProjectileEntity;
import com.qwaecd.paramagic.thaumaturgy.projectile.property.LifetimeCarrier;
import com.qwaecd.paramagic.world.entity.ModEntityTypes;
import com.qwaecd.paramagic.world.sound.ModSounds;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3f;

import javax.annotation.Nonnull;

import static com.qwaecd.paramagic.core.particle.emitter.property.key.AllEmitterProperties.*;

public class LaserProjectile extends BaseProjectile implements ProjectileEntity, LifetimeCarrier {
    private static final SharedGPUEffectRef LASER_EFFECT = SharedGPUEffectRegistry.ref(BuiltinSharedGPUEffects.LASER_BEAM);
    private static final float BEAM_LENGTH = 6.5f;
    private float lifeTime = 5.0f;

    private float HIT_DAMAGE = 5.0f;
    private LineEmitter sharedBeamEmitter;

    public LaserProjectile(EntityType<? extends LaserProjectile> entityType, Level level) {
        super(entityType, level, 0.2f);
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
    protected void onHit(@Nonnull HitResult hitResult) {
        super.onHit(hitResult);
        if (hitResult instanceof EntityHitResult entityHitResult) {
            Entity target = entityHitResult.getEntity();
            target.hurt(this.damageSources().magic(), HIT_DAMAGE);
        }
        this.onLifeEnd();
    }

    public void renderBeamEffect(float partialTick, float deltaTime) {
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
        LASER_EFFECT.submitFromEmitter(emitter, deltaTime);
    }

    private LineEmitter getOrCreateSharedBeamEmitter() {
        if (this.sharedBeamEmitter != null) {
            return this.sharedBeamEmitter;
        }

        LineEmitter emitter = new LineEmitter(new Vector3f(), 320.0f);
        emitter.getProperty(COLOR).modify(v -> v.set(0.9f, 0.35f, 1.95f, 1.0f));
        emitter.getProperty(LIFE_TIME_RANGE).modify(v -> v.set(0.5f, 0.9f));
        emitter.getProperty(SIZE_RANGE).modify(v -> v.set(1.4f, 2.8f));
        emitter.getProperty(BLOOM_INTENSITY).set(1.8f);
        this.sharedBeamEmitter = emitter;
        return emitter;
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
        level().playSound(
                null,
                position.x,
                position.y,
                position.z,
                ModSounds.LASER,
                SoundSource.PLAYERS,
                4.0F,
                1.0F / (level().getRandom().nextFloat() * 0.4F + 1.2F)
        );
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
