package com.qwaecd.paramagic.world.entity.projectile;

import com.qwaecd.paramagic.core.particle.ParticleSystem;
import com.qwaecd.paramagic.core.particle.builder.PhysicsParamBuilder;
import com.qwaecd.paramagic.core.particle.data.EffectPhysicsParameter;
import com.qwaecd.paramagic.core.particle.effect.GPUParticleEffect;
import com.qwaecd.paramagic.core.particle.emitter.EmitterType;
import com.qwaecd.paramagic.core.particle.emitter.ParticleBurst;
import com.qwaecd.paramagic.core.particle.emitter.impl.CircleEmitter;
import com.qwaecd.paramagic.core.particle.emitter.impl.SphereEmitter;
import com.qwaecd.paramagic.core.particle.emitter.property.key.AllEmitterProperties;
import com.qwaecd.paramagic.core.particle.emitter.property.type.ParticleFacingModeStates;
import com.qwaecd.paramagic.core.particle.emitter.property.type.ParticlePrimitiveTypeStates;
import com.qwaecd.paramagic.core.particle.emitter.property.type.ParticleShapeFlags;
import com.qwaecd.paramagic.core.particle.emitter.property.type.VelocityModeStates;
import com.qwaecd.paramagic.core.render.geometricmask.GeometricEffectCaster;
import com.qwaecd.paramagic.network.Networking;
import com.qwaecd.paramagic.network.packet.effect.S2CEffectSpawn;
import com.qwaecd.paramagic.network.particle.anchor.AnchorSpec;
import com.qwaecd.paramagic.network.particle.emitter.EmitterConfig;
import com.qwaecd.paramagic.network.particle.emitter.EmitterPropertyConfig;
import com.qwaecd.paramagic.particle.EffectSpawnBuilder;
import com.qwaecd.paramagic.particle.server.ServerEffect;
import com.qwaecd.paramagic.particle.server.ServerEffectManager;
import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.thaumaturgy.ProjectileEntity;
import com.qwaecd.paramagic.thaumaturgy.projectile.property.LifetimeCarrier;
import com.qwaecd.paramagic.world.entity.EntityEffectHelper;
import com.qwaecd.paramagic.world.entity.ModEntityTypes;
import com.qwaecd.paramagic.world.entity.SpellAnchorEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static com.qwaecd.paramagic.core.particle.emitter.property.key.AllEmitterProperties.*;

public class GravityCollapseEntity extends BaseProjectile implements ProjectileEntity, LifetimeCarrier {
    private float lifeTime = 6.0f;

    private final DistortionHolder distortionHolder = new DistortionHolder();
    private GPUParticleEffect effect;

    public GravityCollapseEntity(EntityType<? extends ThrowableProjectile> entityType, Level level) {
        super(entityType, level, 40.0f);
        this.kineticsState.setGravityScale(0.0f);
    }

    public GravityCollapseEntity(Level level) {
        this(ModEntityTypes.GRAVITY_COLLAPSE_ENTITY, level);
    }

    @Override
    protected boolean isNoPhysics() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            this.tickOnServer();
        }
        if (this.level().isClientSide) {
            if (!this.hasDistortionEffect()) {
                EntityEffectHelper.spawnGravityCollapseEffect(this);
            }
        }
    }

    private void tickOnServer() {
        if (this.age > this.lifeTime) {
            this.onLifeEnd();
            return;
        }

        final double attractionRadius = 12.0d;
        final double attractionRadiusSquared = attractionRadius * attractionRadius;
        final double attractionStrength = 0.1d;

        Entity owner = this.getOwner();
        Vec3 center = this.position();
        AABB area = this.getBoundingBox().inflate(attractionRadius);
        List<Entity> entityList = this.level().getEntities(this, area, entity -> {
                    boolean b1 = entity != owner && entity.distanceToSqr(center) <= attractionRadiusSquared;
                    boolean b2 = !(entity instanceof SpellAnchorEntity);
                    return b1 && b2;
                }
        );
        for (Entity entity : entityList) {
            Vec3 toCenter = center.subtract(entity.position());
            double distanceSquared = toCenter.lengthSqr();
            if (distanceSquared <= 1.0e-6d) {
                continue;
            }

            Vec3 pull = toCenter.normalize().scale(attractionStrength);
            if (entity instanceof ProjectileEntity projectile) {
                double a = 4.0d;
                pull = pull.scale(a);
                projectile.physics().pushWithMomentum(pull.x, pull.y, pull.z, true);
            } else {
                entity.push(pull.x, pull.y, pull.z);
            }
        }
    }

    @Override
    protected void onLifeEnd() {
        super.onLifeEnd();
        this.spawnDeathEffect();
        RandomSource random = this.level().random;
        this.level().playSound(
                null,
                this.getX(), this.getY(), this.getZ(),
                SoundEvents.GENERIC_EXPLODE,
                SoundSource.BLOCKS,
                4.0f,
                (1.0F + (random.nextFloat() - random.nextFloat()) * 0.2F) * 0.7F
        );
    }

    public void renderEffect(float partialTicks, float deltaTime, boolean paused) {
        Vector3f pos = this.getPosition(partialTicks).toVector3f();
        Vector3f axis;
        Vector3f v = this.getDeltaMovement().toVector3f();
        if (v.lengthSquared() > 1.0e-6f) {
            axis = v.normalize();
        } else {
            axis = new Vector3f(0.0f, 1.0f, 0.0f);
        }
        GPUParticleEffect gpuEffect = this.getOrCreateEffect();
        gpuEffect.forEachEmitter(emitter -> emitter.modifyProp(NORMAL, normal -> normal.set(axis)));
        if (paused) {
            return;
        }
        final float degreesPerSecond = 90.0f;
        gpuEffect.getTransform()
                .rotate((float) Math.toRadians(degreesPerSecond * deltaTime), axis)
                .setPosition(pos);
    }

    @Nonnull
    private GPUParticleEffect getOrCreateEffect() {
        if (this.effect != null) {
            return this.effect;
        }
        SphereEmitter sphereEmitter = new SphereEmitter(new Vector3f(), 800.0f);
        sphereEmitter.modifyProp(COLOR, v -> v.set(1.2f, 0.5f, 0.8f, 1.0f));
        sphereEmitter.modifyProp(LIFE_TIME_RANGE, v -> v.set(0.1f, 0.4f));
        sphereEmitter.modifyProp(SIZE_RANGE, v -> v.set(0.06f, 0.3f));
        sphereEmitter.trySet(BLOOM_INTENSITY, 0.3f);
        sphereEmitter.trySet(SPHERE_RADIUS, this.getDistortionRadius() + 1.5f);
        sphereEmitter.trySet(VELOCITY_MODE, VelocityModeStates.RANDOM);
        sphereEmitter.modifyProp(BASE_VELOCITY, v -> v.set(0.8f));
        sphereEmitter.trySet(PARTICLE_PRIMITIVE_TYPE, ParticlePrimitiveTypeStates.TRIANGLE);
        sphereEmitter.trySet(PARTICLE_FACING_MODE, ParticleFacingModeStates.NORMAL_FACING);
        sphereEmitter.trySet(PARTICLE_SHAPE_FLAGS, ParticleShapeFlags.JITTERED);

        CircleEmitter accretionDiskEmitter = new CircleEmitter(new Vector3f(), 620.0f);
        accretionDiskEmitter.modifyProp(COLOR, v -> v.set(4.35f, 0.72f, 0.85f, 1.0f));
        accretionDiskEmitter.modifyProp(LIFE_TIME_RANGE, v -> v.set(0.13f, 0.3f));
        accretionDiskEmitter.modifyProp(SIZE_RANGE, v -> v.set(0.05f, 0.1f));
        accretionDiskEmitter.trySet(BLOOM_INTENSITY, 0.2f);
        accretionDiskEmitter.modifyProp(NORMAL, v -> v.set(0.0f, 1.0f, 0.0f));
        accretionDiskEmitter.modifyProp(INNER_OUTER_RADIUS, v -> v.set(
                Math.max(0.3f, this.getDistortionRadius() + 0.2f),
                this.getDistortionRadius() + 3.2f
        ));
        accretionDiskEmitter.trySet(VELOCITY_MODE, VelocityModeStates.RANDOM);
        accretionDiskEmitter.modifyProp(BASE_VELOCITY, v -> v.set(0.65f));
        accretionDiskEmitter.trySet(PARTICLE_PRIMITIVE_TYPE, ParticlePrimitiveTypeStates.QUAD);
        accretionDiskEmitter.trySet(PARTICLE_FACING_MODE, ParticleFacingModeStates.NORMAL_FACING);

        EffectPhysicsParameter physicsParameter = new PhysicsParamBuilder()
                .centerForcePos(0.0f, 0.0f, 0.0f)
                .primaryForceParam(320.0f, -1.2f)
                .primaryForceEnabled(true)
                .build();
        this.effect = new GPUParticleEffect(List.of(sphereEmitter, accretionDiskEmitter), 100_000, 0.0f, physicsParameter);
        ParticleSystem.getInstance().spawnEffect(this.effect);
        return this.effect;
    }

    private void spawnDeathEffect() {
        Vector3f position = this.position().toVector3f();
        EffectSpawnBuilder builder = new EffectSpawnBuilder();
        builder.setAnchorSpec(AnchorSpec.forStaticPosition(position))
                .setMaxParticles(10000);

        PhysicsParamBuilder paramBuilder = new PhysicsParamBuilder();
        builder.setEffectPhysicsParameter(paramBuilder.build());

        ParticleBurst[] bursts = new ParticleBurst[] {
                new ParticleBurst(0.0f, 3000)
        };
        EmitterPropertyConfig propConfig = new EmitterPropertyConfig.Builder()
                .addProperty(BLOOM_INTENSITY, 2.0f)
                .addProperty(LIFE_TIME_RANGE, new Vector2f(0.2f, 1.0f))
                .addProperty(SIZE_RANGE, new Vector2f(0.6f, 2.0f))
                .addProperty(EMIT_FROM_VOLUME, true)
                .addProperty(SPHERE_RADIUS, this.getDistortionRadius())
                .addProperty(VELOCITY_MODE, VelocityModeStates.RANDOM)
                .addProperty(BASE_VELOCITY, new Vector3f(0, 2.0f, 0))
                .addProperty(COLOR, new Vector4f(1.2f, 0.5f, 0.8f, 1.0f))
                .addProperty(POSITION, new Vector3f(position))
                .addProperty(PARTICLE_PRIMITIVE_TYPE, ParticlePrimitiveTypeStates.TRIANGLE)
                .addProperty(PARTICLE_SHAPE_FLAGS, ParticleShapeFlags.JITTERED)
                .build();
        EmitterConfig config = new EmitterConfig(
                EmitterType.SPHERE,
                0.0f,
                position,
                propConfig,
                bursts
        );
        builder.addEmitterConfig(config);

        ServerEffect effect = ServerEffectManager.getInstance().createEffect(builder);
        if (effect == null) {
            return;
        }
        final double distance = 128.0D;
        for (ServerPlayer player : ((ServerLevel) this.level()).players()) {
            if (player.distanceToSqr(position.x, position.y, position.z) < distance * distance) {
                Networking.get().sendToPlayer(player, new S2CEffectSpawn(effect.spawnData));
            }
        }
    }

    @Override
    public void onClientRemoval() {
        super.onClientRemoval();
        EntityEffectHelper.removeGravityCollapseEffect(this);
        if (this.effect != null) {
            ParticleSystem.getInstance().removeEffect(this.effect);
        }
    }

    public boolean hasDistortionEffect() {
        return this.distortionHolder.hasDistortionEffect();
    }

    @Nullable
    public GeometricEffectCaster getDistortionCaster() {
        return this.distortionHolder.distortionCaster;
    }

    public void setDistortionPosition(float x, float y, float z) {
        this.distortionHolder.setPosition(x, y, z);
    }

    public float getDistortionRadius() {
        return this.distortionHolder.getRadius();
    }

    public void setDistortionCaster(GeometricEffectCaster distortionCaster) {
        this.distortionHolder.setDistortionCaster(distortionCaster);
    }

    @Override
    public float getLifetime() {
        return this.lifeTime;
    }

    @Override
    public void setLifetime(float lifetime) {
        this.lifeTime = lifetime;
    }

    @PlatformScope(PlatformScopeType.COMMON)
    static final class DistortionHolder {
        @Nullable
        @PlatformScope(PlatformScopeType.CLIENT)
        GeometricEffectCaster distortionCaster = null;
        private float radius = 3.0f;

        public boolean hasDistortionEffect() {
            return this.distortionCaster != null;
        }

        public void setPosition(float x, float y, float z) {
            if (this.distortionCaster != null) {
                this.distortionCaster.getTransform().setPosition(x, y, z);
            }
        }

        public void setDistortionCaster(@Nullable GeometricEffectCaster distortionCaster) {
            this.distortionCaster = distortionCaster;
            if (distortionCaster != null) {
                this.radius = distortionCaster.getTransform().getScale(new Vector3f()).y;
            }
        }

        public float getRadius() {
            return this.radius;
        }

        public void setRadius(float radius) {
            this.radius = radius;
            if (this.distortionCaster != null) {
                distortionCaster.getTransform().setScale(radius);
            }
        }
    }
}
