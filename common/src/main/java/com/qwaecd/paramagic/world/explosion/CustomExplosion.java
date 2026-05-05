package com.qwaecd.paramagic.world.explosion;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public final class CustomExplosion {
    static final int DEFAULT_BLOCK_UPDATE_FLAGS = Block.UPDATE_ALL;
    private static final float OCCLUDED_ENTITY_MULTIPLIER = 0.05f;

    private CustomExplosion() {
    }

    public static void explode(CustomExplosionConfig config) {
        Explosion vanillaContext = createVanillaContext(config);
        if (config.visualCallback != null) {
            config.visualCallback.onExplosionStarted(new ExplosionVisualEvent(config, 0, false));
        }
        if (config.damageEntities) {
            damageEntities(config, vanillaContext);
        }
        if (!config.destroyBlocks || config.maxDestroyedBlocks <= 0) {
            finish(config, 0);
            return;
        }

        List<SphereExplosionScanner.BlockOffset> offsets = SphereExplosionScanner.offsetsFor(config.radius);
        CustomExplosionTask task = new CustomExplosionTask(config, offsets, vanillaContext);
        if (offsets.size() <= config.syncBlockThreshold) {
            task.processUntilFinished();
            return;
        }

        task.tick();
        if (!task.isFinished()) {
            CustomExplosionManager.getInstance().addTask(task);
        }
    }

    static Explosion createVanillaContext(CustomExplosionConfig config) {
        return new Explosion(
                config.level,
                config.source,
                config.center.x,
                config.center.y,
                config.center.z,
                config.radius,
                config.createFire,
                Explosion.BlockInteraction.DESTROY
        );
    }

    static void finish(CustomExplosionConfig config, int destroyedBlocks) {
        if (config.visualCallback != null) {
            config.visualCallback.onExplosionFinished(new ExplosionVisualEvent(config, destroyedBlocks, true));
        }
    }

    static boolean processBlock(CustomExplosionConfig config, Explosion vanillaContext, SphereExplosionScanner.BlockOffset offset) {
        ServerLevel level = config.level;
        BlockPos pos = BlockPos.containing(
                config.center.x + offset.dx,
                config.center.y + offset.dy,
                config.center.z + offset.dz
        );
        if (!level.isInWorldBounds(pos) || !isChunkLoaded(level, pos)) {
            return false;
        }

        BlockState state = level.getBlockState(pos);
        if (!shouldDestroy(config, level, pos, state, offset.distance)) {
            return false;
        }

        float distanceFactor = Mth.clamp(1.0f - offset.distance / config.radius, 0.0f, 1.0f);
        float power = config.radius * config.blockPowerScale * distanceFactor;
        ExplosionBlockEvent event = new ExplosionBlockEvent(level, config.center, pos.immutable(), state, offset.distance, distanceFactor, power);
        if (config.blockCallback != null) {
            config.blockCallback.beforeDestroy(event);
        }
        if (config.dropCallback != null) {
            config.dropCallback.beforeDestroy(event);
        }

        Block block = state.getBlock();
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), DEFAULT_BLOCK_UPDATE_FLAGS);
        block.wasExploded(level, pos, vanillaContext);
        tryCreateFire(config, pos);
        return true;
    }

    private static void tryCreateFire(CustomExplosionConfig config, BlockPos pos) {
        if (!config.createFire || config.level.random.nextInt(3) != 0) {
            return;
        }
        BlockPos below = pos.below();
        if (config.level.getBlockState(pos).isAir() && config.level.getBlockState(below).isSolidRender(config.level, below)) {
            config.level.setBlockAndUpdate(pos, BaseFireBlock.getState(config.level, pos));
        }
    }

    private static boolean shouldDestroy(CustomExplosionConfig config, ServerLevel level, BlockPos pos, BlockState state, float distance) {
        if (state.isAir() || distance > config.radius) {
            return false;
        }
        if (state.getDestroySpeed(level, pos) < 0.0f) {
            return false;
        }

        float distanceFactor = Mth.clamp(1.0f - distance / config.radius, 0.0f, 1.0f);
        float power = config.radius * config.blockPowerScale * distanceFactor;
        float resistance = state.getBlock().getExplosionResistance();
        return power > resistance * config.resistanceScale;
    }

    private static boolean isChunkLoaded(ServerLevel level, BlockPos pos) {
        return level.getChunkSource().hasChunk(pos.getX() >> 4, pos.getZ() >> 4);
    }

    private static void damageEntities(CustomExplosionConfig config, Explosion vanillaContext) {
        float damageRadius = config.radius * 2.0f;
        Vec3 center = config.center;
        AABB bounds = new AABB(
                center.x - damageRadius,
                center.y - damageRadius,
                center.z - damageRadius,
                center.x + damageRadius,
                center.y + damageRadius,
                center.z + damageRadius
        );
        List<Entity> entities = config.level.getEntities(config.source, bounds);
        for (Entity entity : entities) {
            if (entity.ignoreExplosion()) {
                continue;
            }
            double distance = Math.sqrt(entity.distanceToSqr(center));
            if (distance > damageRadius) {
                continue;
            }

            Vec3 direction = new Vec3(entity.getX() - center.x, entity.getEyeY() - center.y, entity.getZ() - center.z);
            double directionLength = direction.length();
            if (directionLength == 0.0) {
                continue;
            }

            float distanceFactor = Mth.clamp(1.0f - (float) (distance / damageRadius), 0.0f, 1.0f);
            float seenPercent = Explosion.getSeenPercent(center, entity);
            boolean exposed = seenPercent > 0.0f;
            float exposureMultiplier = exposed ? 1.0f : OCCLUDED_ENTITY_MULTIPLIER;
            float damage = config.maxEntityDamage * distanceFactor * exposureMultiplier;
            if (damage > 0.0f) {
                entity.hurt(vanillaContext.getDamageSource(), damage);
            }

            double knockback = distanceFactor * exposureMultiplier * config.knockbackScale;
            if (knockback > 0.0) {
                Vec3 movement = direction.scale(1.0 / directionLength).scale(knockback);
                entity.setDeltaMovement(entity.getDeltaMovement().add(movement));
                if (entity instanceof Player player && !player.isSpectator() && (!player.isCreative() || !player.getAbilities().flying)) {
                    vanillaContext.getHitPlayers().put(player, movement);
                }
            }
        }
    }
}
