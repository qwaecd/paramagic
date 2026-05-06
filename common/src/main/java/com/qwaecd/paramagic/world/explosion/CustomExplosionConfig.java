package com.qwaecd.paramagic.world.explosion;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public final class CustomExplosionConfig {
    public static final int DEFAULT_SYNC_BLOCK_THRESHOLD = 4096;
    public static final int DEFAULT_BLOCKS_PER_TICK = 4096;
    public static final int DEFAULT_MAX_DESTROYED_BLOCKS = 65536;
    public static final float DEFAULT_MAX_ENTITY_DAMAGE = 85.0f;
    public static final float DEFAULT_KNOCKBACK_SCALE = 1.0f;
    public static final float DEFAULT_BLOCK_POWER_SCALE = 1.0f;
    public static final float DEFAULT_RESISTANCE_SCALE = 0.3f;

    public final ServerLevel level;
    public final Vec3 center;
    public final float radius;
    @Nullable
    public final Entity source;
    public final boolean destroyBlocks;
    public final boolean damageEntities;
    public final boolean createFire;
    public final int syncBlockThreshold;
    public final int blocksPerTick;
    public final int maxDestroyedBlocks;
    public final float maxEntityDamage;
    public final float knockbackScale;
    public final float blockPowerScale;
    public final float resistanceScale;
    @Nullable
    public final ExplosionBlockCallback blockCallback;
    @Nullable
    public final ExplosionDropCallback dropCallback;
    @Nullable
    public final ExplosionVisualCallback visualCallback;

    private CustomExplosionConfig(Builder builder) {
        this.level = builder.level;
        this.center = builder.center;
        this.radius = builder.radius;
        this.source = builder.source;
        this.destroyBlocks = builder.destroyBlocks;
        this.damageEntities = builder.damageEntities;
        this.createFire = builder.createFire;
        this.syncBlockThreshold = builder.syncBlockThreshold;
        this.blocksPerTick = builder.blocksPerTick;
        this.maxDestroyedBlocks = builder.maxDestroyedBlocks;
        this.maxEntityDamage = builder.maxEntityDamage;
        this.knockbackScale = builder.knockbackScale;
        this.blockPowerScale = builder.blockPowerScale;
        this.resistanceScale = builder.resistanceScale;
        this.blockCallback = builder.blockCallback;
        this.dropCallback = builder.dropCallback;
        this.visualCallback = builder.visualCallback;
    }

    public static Builder builder(ServerLevel level, Vec3 center, float radius) {
        return new Builder(level, center, radius);
    }

    public static final class Builder {
        private final ServerLevel level;
        private final Vec3 center;
        private final float radius;
        @Nullable
        private Entity source;
        private boolean destroyBlocks = true;
        private boolean damageEntities = true;
        private boolean createFire = false;
        private int syncBlockThreshold = DEFAULT_SYNC_BLOCK_THRESHOLD;
        private int blocksPerTick = DEFAULT_BLOCKS_PER_TICK;
        private int maxDestroyedBlocks = DEFAULT_MAX_DESTROYED_BLOCKS;
        private float maxEntityDamage = DEFAULT_MAX_ENTITY_DAMAGE;
        private float knockbackScale = DEFAULT_KNOCKBACK_SCALE;
        private float blockPowerScale = DEFAULT_BLOCK_POWER_SCALE;
        private float resistanceScale = DEFAULT_RESISTANCE_SCALE;
        @Nullable
        private ExplosionBlockCallback blockCallback;
        @Nullable
        private ExplosionDropCallback dropCallback;
        @Nullable
        private ExplosionVisualCallback visualCallback;

        private Builder(ServerLevel level, Vec3 center, float radius) {
            if (level == null) {
                throw new IllegalArgumentException("level cannot be null");
            }
            if (center == null) {
                throw new IllegalArgumentException("center cannot be null");
            }
            if (radius <= 0.0f) {
                throw new IllegalArgumentException("radius must be positive");
            }
            this.level = level;
            this.center = center;
            this.radius = radius;
        }

        public Builder source(@Nullable Entity source) {
            this.source = source;
            return this;
        }

        public Builder destroyBlocks(boolean destroyBlocks) {
            this.destroyBlocks = destroyBlocks;
            return this;
        }

        public Builder damageEntities(boolean damageEntities) {
            this.damageEntities = damageEntities;
            return this;
        }

        public Builder createFire(boolean createFire) {
            this.createFire = createFire;
            return this;
        }

        public Builder syncBlockThreshold(int syncBlockThreshold) {
            this.syncBlockThreshold = Math.max(0, syncBlockThreshold);
            return this;
        }

        public Builder blocksPerTick(int blocksPerTick) {
            this.blocksPerTick = Math.max(1, blocksPerTick);
            return this;
        }

        public Builder maxDestroyedBlocks(int maxDestroyedBlocks) {
            this.maxDestroyedBlocks = Math.max(0, maxDestroyedBlocks);
            return this;
        }

        public Builder maxEntityDamage(float maxEntityDamage) {
            this.maxEntityDamage = Math.max(0.0f, maxEntityDamage);
            return this;
        }

        public Builder knockbackScale(float knockbackScale) {
            this.knockbackScale = Math.max(0.0f, knockbackScale);
            return this;
        }

        public Builder blockPowerScale(float blockPowerScale) {
            this.blockPowerScale = Math.max(0.0f, blockPowerScale);
            return this;
        }

        public Builder resistanceScale(float resistanceScale) {
            this.resistanceScale = Math.max(0.0f, resistanceScale);
            return this;
        }

        public Builder blockCallback(@Nullable ExplosionBlockCallback blockCallback) {
            this.blockCallback = blockCallback;
            return this;
        }

        public Builder dropCallback(@Nullable ExplosionDropCallback dropCallback) {
            this.dropCallback = dropCallback;
            return this;
        }

        public Builder visualCallback(@Nullable ExplosionVisualCallback visualCallback) {
            this.visualCallback = visualCallback;
            return this;
        }

        public CustomExplosionConfig build() {
            return new CustomExplosionConfig(this);
        }
    }
}
