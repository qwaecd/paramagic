package com.qwaecd.paramagic.world.entity.projectile;

import com.qwaecd.paramagic.core.particle.emitter.Emitter;
import com.qwaecd.paramagic.core.particle.emitter.impl.PointEmitter;
import com.qwaecd.paramagic.particle.client.shared.BuiltinSharedGPUEffects;
import com.qwaecd.paramagic.particle.client.shared.SharedGPUEffectRef;
import com.qwaecd.paramagic.particle.client.shared.SharedGPUEffectRegistry;
import com.qwaecd.paramagic.thaumaturgy.ProjectileEntity;
import com.qwaecd.paramagic.thaumaturgy.projectile.property.LifetimeCarrier;
import com.qwaecd.paramagic.world.entity.ModEntityTypes;
import com.qwaecd.paramagic.world.sound.ModSounds;
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
    private Emitter sharedBeamEmitter;

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
    protected void onHit(@Nonnull HitResult hitResult) {
        super.onHit(hitResult);
        if (hitResult instanceof EntityHitResult entityHitResult) {
            Entity target = entityHitResult.getEntity();
            target.hurt(this.damageSources().magic(), HIT_DAMAGE);
        }
        this.onLifeEnd();
    }

    public void renderBeamEffect(float partialTick, float deltaTime) {
        Vector3f position = this.getPosition(partialTick).toVector3f();
        Vec3 velocity = this.getDeltaMovement();
        if (velocity.lengthSqr() <= 1.0e-8) {
            return;
        }
        Emitter emitter = this.getOrCreateSharedBeamEmitter();
        emitter.moveTo(position);
        emitter.modifyProp(BASE_VELOCITY, v -> v.set(0.5f));
        LASER_EFFECT.submitFromEmitter(emitter, deltaTime);
    }

    private Emitter getOrCreateSharedBeamEmitter() {
        if (this.sharedBeamEmitter != null) {
            return this.sharedBeamEmitter;
        }

        Emitter emitter = new PointEmitter(new Vector3f(), 160.0f);
        emitter.modifyProp(COLOR, v -> v.set(0.9f, 0.35f, 1.95f, 1.0f));
        emitter.modifyProp(LIFE_TIME_RANGE, v -> v.set(0.5f, 0.9f));
        emitter.modifyProp(SIZE_RANGE, v -> v.set(1.4f, 2.8f));
        emitter.getProperty(BLOOM_INTENSITY).set(1.8f);
        emitter.getProperty(VELOCITY_SPREAD).set(180.0f);
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
        this.syncRecordedOperators();
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
