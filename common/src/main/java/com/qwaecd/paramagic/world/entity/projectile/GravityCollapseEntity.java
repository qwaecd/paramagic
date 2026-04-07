package com.qwaecd.paramagic.world.entity.projectile;

import com.qwaecd.paramagic.core.render.geometricmask.GeometricEffectCaster;
import com.qwaecd.paramagic.platform.annotation.PlatformScope;
import com.qwaecd.paramagic.platform.annotation.PlatformScopeType;
import com.qwaecd.paramagic.thaumaturgy.kinetics.ProjectileVelocityMutable;
import com.qwaecd.paramagic.world.entity.EntityEffectHelper;
import com.qwaecd.paramagic.world.entity.ModEntityTypes;
import com.qwaecd.paramagic.world.entity.SpellAnchorEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import javax.annotation.Nullable;

public class GravityCollapseEntity extends BaseProjectile {
    private float life = 6.0f;

    @PlatformScope(PlatformScopeType.CLIENT)
    private final DistortionHolder distortionHolder = new DistortionHolder();

    public GravityCollapseEntity(EntityType<? extends ThrowableProjectile> entityType, Level level) {
        super(entityType, level);
        this.setNoGravity(true);
        this.kineticsState.setGravityScale(0.0f);
    }

    public GravityCollapseEntity(Level level) {
        this(ModEntityTypes.GRAVITY_COLLAPSE_ENTITY, level);
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
        final double attractionRadius = 12.0d;
        final double attractionRadiusSquared = attractionRadius * attractionRadius;
        final double attractionStrength = 1.0d;

        Entity owner = this.getOwner();
        Vec3 center = this.position();
        AABB area = this.getBoundingBox().inflate(attractionRadius);
        for (Entity entity : this.level().getEntities(this, area, entity -> {
                boolean b1 = entity != owner && entity.distanceToSqr(center) <= attractionRadiusSquared;
                boolean b2 = !(entity instanceof SpellAnchorEntity);
                return b1 && b2;
            }
        )) {
            Vec3 toCenter = center.subtract(entity.position());
            double distanceSquared = toCenter.lengthSqr();
            if (distanceSquared <= 1.0e-6d) {
                continue;
            }

            double distance = Math.sqrt(distanceSquared);
            double pullScale = attractionStrength * (1.0d - (distance / attractionRadius));
            if (pullScale <= 0.0d) {
                continue;
            }
            Vec3 pull = toCenter.normalize().scale(pullScale);
            if (entity instanceof ProjectileVelocityMutable projectile) {
                projectile.addVelocity((float) pull.x, (float) pull.y, (float) pull.z);
            } else {
                entity.push(pull.x, pull.y, pull.z);
            }
        }

        this.life -= 1.0f / 20.0f;
        if (this.life < 0.0f) {
            this.discard();
        }
    }

    @Override
    public void setPos(double x, double y, double z) {
        super.setPos(x, y, z);
    }

    @Override
    public void shoot() {
        this.syncEntityVelocityFromKinetics();
        Vec3 velocity = this.getDeltaMovement();
        this.shoot(velocity.x, velocity.y, velocity.z, (float) velocity.length(), this.kineticsState.getInaccuracy());
        this.level().addFreshEntity(this);
    }

    @Override
    public void onClientRemoval() {
        super.onClientRemoval();
        EntityEffectHelper.removeGravityCollapseEffect(this);
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

    @PlatformScope(PlatformScopeType.CLIENT)
    static final class DistortionHolder {
        @Nullable
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
