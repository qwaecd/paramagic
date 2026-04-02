package com.qwaecd.paramagic.world.entity.projectile;

import com.qwaecd.paramagic.core.particle.emitter.impl.PointEmitter;
import com.qwaecd.paramagic.particle.client.shared.BuiltinSharedGPUEffects;
import com.qwaecd.paramagic.particle.client.shared.SharedGPUEffectRef;
import com.qwaecd.paramagic.particle.client.shared.SharedGPUEffectRegistry;
import com.qwaecd.paramagic.thaumaturgy.ProjectileEntity;
import com.qwaecd.paramagic.world.entity.ModEntityTypes;
import org.joml.Vector3f;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;

import java.util.Random;

import static com.qwaecd.paramagic.core.particle.emitter.property.key.AllEmitterProperties.*;

public class MagicArrowProjectile extends ArrowLikeProjectileEntity implements ProjectileEntity {
    private final Random rand = new Random();
    private static final float CLIENT_EMITTER_DELTA_TIME = 1.0f / 20.0f;
    private static final SharedGPUEffectRef TRAIL_EFFECT = SharedGPUEffectRegistry.ref(BuiltinSharedGPUEffects.MAGIC_ARROW_TRAIL);

    private PointEmitter sharedTrailEmitter;

    public MagicArrowProjectile(EntityType<? extends ArrowLikeProjectileEntity> type, Level level) {
        super(type, level);
    }

    public MagicArrowProjectile(Level level) {
        super(ModEntityTypes.MAGIC_ARROW_PROJECTILE, level);
    }

    @Override
    @Nonnull
    protected ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.inGroundTime >= 20) {
            this.discard();
        }
        if (!this.level().isClientSide || !this.isAlive()) {
            return;
        }
        Vec3 velocity = this.getDeltaMovement();
        PointEmitter emitter = this.getOrCreateSharedTrailEmitter();
        Vec3 position = this.position();

        emitter.moveTo(new Vector3f((float) position.x, (float) position.y, (float) position.z));
        emitter.getProperty(BASE_VELOCITY).modify(v -> v.set(
                (float) (-velocity.x * 2.6f),
                (float) (-velocity.y * 2.6f),
                (float) (-velocity.z * 2.6f)
        ));

        TRAIL_EFFECT.submitFromEmitter(emitter, CLIENT_EMITTER_DELTA_TIME);
    }

    private PointEmitter getOrCreateSharedTrailEmitter() {
        if (this.sharedTrailEmitter != null) {
            return this.sharedTrailEmitter;
        }

        PointEmitter emitter = new PointEmitter(new Vector3f(), 100.0f);
        emitter.getProperty(VELOCITY_SPREAD).set(60.0f);
        emitter.getProperty(COLOR).modify(v -> v.set(0.6f, 0.4f, 0.8f, 1.0f));
        emitter.getProperty(LIFE_TIME_RANGE).modify(v -> v.set(0.2f, 0.45f));
        emitter.getProperty(SIZE_RANGE).modify(v -> v.set(2.8f, 4.2f));
        emitter.getProperty(BLOOM_INTENSITY).set(1.7f);

        this.sharedTrailEmitter = emitter;
        return emitter;
    }
}
