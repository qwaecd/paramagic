package com.qwaecd.paramagic.spell.builtin.explostion;

import com.qwaecd.paramagic.mixin.accessor.IServerLevelAccessor;
import com.qwaecd.paramagic.spell.core.EndSpellReason;
import com.qwaecd.paramagic.spell.core.store.AllSessionDataKeys;
import com.qwaecd.paramagic.spell.core.store.SessionDataValue;
import com.qwaecd.paramagic.spell.server.ReleaseAwareSpellRuntime;
import com.qwaecd.paramagic.spell.server.ServerSpellContext;
import com.qwaecd.paramagic.world.explosion.*;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.Random;

public class ExplosionSpellRuntime implements ReleaseAwareSpellRuntime {
    public static final float RADIUS = 64.0f;
    static final int CASTING_TICKS = 20 * 6;
    static final int CHANNELING_TICKS = 20 * 16;

    private final LightningSpawner spawner = new LightningSpawner();

    @Nullable
    private ServerLevel levelCache;

    private int elapsedTicks = 0;
    private boolean finished = false;
    private Stage currentStage = Stage.CASTING;

    public static Vector3f getExplosionCenter(Vector3f pos) {
        return new Vector3f(pos.x, pos.y + 12.0f, pos.z);
    }

    @Override
    public void onStart(ServerSpellContext context) {
        ServerLevel level = context.getLevel();
        this.levelCache = level;
        this.currentStage = Stage.CASTING;
        this.elapsedTicks = 0;
        this.finished = false;
        this.setThunder(level);
    }

    @Override
    public void tick(ServerSpellContext context) {
        if (this.finished) {
            return;
        }

        ServerLevel level = context.getLevel();
        if (this.levelCache == null) {
            this.levelCache = level;
        }

        this.elapsedTicks++;
        this.tickLightning(level, context);

        if (this.currentStage == Stage.CASTING && this.elapsedTicks >= CASTING_TICKS) {
            this.currentStage = Stage.CHANNELING;
        }
    }

    @Override
    public void release(ServerSpellContext context) {
        if (this.currentStage == Stage.CHANNELING && this.elapsedTicks >= CASTING_TICKS + CHANNELING_TICKS) {
            this.currentStage = Stage.IMPACT;
            this.execute(context, context.getLevel());
        } else {
            context.getSession().interrupt();
        }
        this.finished = true;
    }

    @Override
    public void interrupt(ServerSpellContext context, EndSpellReason reason) {
        this.finished = true;
    }

    @Override
    public boolean isFinished() {
        return this.finished;
    }

    @Override
    public void dispose(ServerSpellContext context) {
        this.setClear();
    }

    private void tickLightning(ServerLevel level, ServerSpellContext context) {
        this.spawner.tick();
        if (!this.spawner.shouldSpawn()) {
            return;
        }

        Vec3 position = context.getCaster().position();
        this.spawner.setCenter(position);
        this.summonLightning(level);
    }

    private void summonLightning(ServerLevel level) {
        BlockPos spawnPosition = this.spawner.getSpawnPosition(level);
        if (spawnPosition == null) {
            return;
        }
        LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(level);
        if (lightningBolt == null) {
            return;
        }
        lightningBolt.moveTo(Vec3.atBottomCenterOf(spawnPosition));
        level.addFreshEntity(lightningBolt);
    }

    private void setThunder(ServerLevel level) {
        int weatherTime = ServerLevel.THUNDER_DURATION.sample(level.getRandom());
        level.setWeatherParameters(0, weatherTime, true, true);
    }

    private void setClear() {
        this.spawner.setInterrupted();
        if (this.levelCache == null) {
            return;
        }
        int clearTime = ServerLevel.RAIN_DELAY.sample(this.levelCache.getRandom());
        this.levelCache.setWeatherParameters(clearTime, 0, false, false);
    }

    private void execute(ServerSpellContext context, ServerLevel level) {
        Entity caster = level.getEntity(context.getCaster().getEntityNetworkId());
        if (caster == null) {
            return;
        }

        SessionDataValue<Vector3f> value = context.getDataStore().getValue(AllSessionDataKeys.firstPosition);
        if (value == null) {
            return;
        }

        Vector3f pos = value.getValue();
        Vec3 center = new Vec3(getExplosionCenter(pos));
        CustomExplosion.explode(CustomExplosionConfig.builder(level, center, RADIUS)
                .source(caster)
                .createFire(true)
                .dropCallback(ExplosionDropCallbacks::dropContainerContents)
                .maxEntityDamage(16384.0f)
                .knockbackScale(8.0f)
                .build());
    }

    private enum Stage {
        CASTING,
        CHANNELING,
        IMPACT
    }

    static class LightningSpawner {
        private static final float TICK_PER_SECOND = 1.0f / 20.0f;
        private static final int MAX_CHUNK_DISTANCE = 5;
        private static final int MIN_CHUNK_DISTANCE = 2;
        private static final Random RANDOM = new Random();
        private float nextSpawnInterval = RANDOM.nextFloat(1.0f, 5.0f);
        private float currentTime = 0.0f;
        private boolean shouldSpawn = false;
        private boolean interrupted = false;

        @Setter
        private Vec3 center = Vec3.ZERO;

        void tick() {
            if (this.interrupted) {
                return;
            }
            this.currentTime += TICK_PER_SECOND;
            if (this.currentTime >= this.nextSpawnInterval) {
                this.currentTime = 0.0f;
                this.nextSpawnInterval = RANDOM.nextFloat(0.5f, 5.0f);
                this.shouldSpawn = true;
            }
        }

        void setInterrupted() {
            this.interrupted = true;
            this.shouldSpawn = false;
        }

        @Nullable
        BlockPos getSpawnPosition(ServerLevel level) {
            this.shouldSpawn = false;
            try {
                int x = pickPos((int) this.center.x);
                int z = pickPos((int) this.center.z);
                ChunkPos chunkPos = level.getChunk(x >> 4, z >> 4).getPos();
                int i = chunkPos.getMinBlockX();
                int j = chunkPos.getMinBlockZ();
                return ((IServerLevelAccessor) level).findLightningTargetAroundMethod(level.getBlockRandomPos(i, 0, j, 15));
            } catch (Exception ignored) {
                return null;
            }
        }

        static int pickPos(int i) {
            int lowerBound = MIN_CHUNK_DISTANCE * 16 + 1;
            int upperBound = MAX_CHUNK_DISTANCE * 16 - 1;
            int rangeSize = (upperBound - lowerBound + 1) * 2;
            int offsetIndex = RANDOM.nextInt(rangeSize);
            int d = lowerBound + (offsetIndex / 2);
            boolean positive = (offsetIndex % 2 == 0);
            if (positive) {
                return i + d;
            }
            return i - d;
        }

        boolean shouldSpawn() {
            return this.shouldSpawn && !this.interrupted;
        }
    }
}
