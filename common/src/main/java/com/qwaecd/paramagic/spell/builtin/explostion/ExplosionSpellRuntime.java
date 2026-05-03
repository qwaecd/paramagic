package com.qwaecd.paramagic.spell.builtin.explostion;

import com.qwaecd.paramagic.core.particle.builder.PhysicsParamBuilder;
import com.qwaecd.paramagic.core.particle.emitter.EmitterType;
import com.qwaecd.paramagic.core.particle.emitter.ParticleBurst;
import com.qwaecd.paramagic.core.particle.emitter.property.key.AllEmitterProperties;
import com.qwaecd.paramagic.core.particle.emitter.property.type.VelocityModeStates;
import com.qwaecd.paramagic.mixin.accessor.IServerLevelAccessor;
import com.qwaecd.paramagic.network.Networking;
import com.qwaecd.paramagic.network.packet.effect.S2CEffectSpawn;
import com.qwaecd.paramagic.network.particle.anchor.AnchorSpec;
import com.qwaecd.paramagic.network.particle.emitter.EmitterConfig;
import com.qwaecd.paramagic.network.particle.emitter.EmitterPropertyConfig;
import com.qwaecd.paramagic.particle.EffectSpawnBuilder;
import com.qwaecd.paramagic.particle.server.ServerEffect;
import com.qwaecd.paramagic.particle.server.ServerEffectManager;
import com.qwaecd.paramagic.spell.core.EndSpellReason;
import com.qwaecd.paramagic.spell.core.store.AllSessionDataKeys;
import com.qwaecd.paramagic.spell.core.store.SessionDataValue;
import com.qwaecd.paramagic.spell.server.ServerSpellContext;
import com.qwaecd.paramagic.spell.server.SpellRuntime;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import javax.annotation.Nullable;
import java.util.Random;

public class ExplosionSpellRuntime implements SpellRuntime {
    static final int CASTING_TICKS = 20 * 6;
    static final int CHANNELING_TICKS = 20 * 16;

    private final LightningSpawner spawner = new LightningSpawner();

    @Nullable
    private ServerLevel levelCache;

    private int elapsedTicks = 0;
    private boolean finished = false;
    private Stage currentStage = Stage.CASTING;

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

        if (this.currentStage == Stage.CHANNELING && this.elapsedTicks >= CASTING_TICKS + CHANNELING_TICKS) {
            this.currentStage = Stage.IMPACT;
            this.execute(context, level);
            this.setClear();
            this.finished = true;
        }
    }

    @Override
    public void interrupt(ServerSpellContext context, EndSpellReason reason) {
        this.setClear();
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
        level.explode(
                caster,
                pos.x,
                pos.y + 12.0f,
                pos.z,
                128.0f,
                false,
                ServerLevel.ExplosionInteraction.BLOCK
        );
        this.genParticleData(pos, caster, level);
    }

    private void genParticleData(Vector3f pos, Entity casterEntity, ServerLevel level) {
        Vector3f position = new Vector3f(pos.x, pos.y + 12.0f, pos.z);
        if (!(casterEntity instanceof ServerPlayer)) {
            return;
        }
        EffectSpawnBuilder builder = new EffectSpawnBuilder();
        PhysicsParamBuilder physicsBuilder = new PhysicsParamBuilder();
        physicsBuilder
                .centerForcePos(position)
                .primaryForceEnabled(true)
                .primaryForceParam(-20.0f, -2.0f)
                .dragCoefficient(0.8f)
                .linearForce(0.0f, -0.98f, 0.0f)
                .linearForceEnabled(true);

        builder
                .setEffectPhysicsParameter(physicsBuilder.build())
                .setMaxParticles(9_0000)
                .setMaxLifetime(5.0f)
                .setAnchorSpec(AnchorSpec.forStaticPosition(position));
        {
            ParticleBurst[] bursts = new ParticleBurst[] {
                    new ParticleBurst(0.0f, 3_0000)
            };
            EmitterPropertyConfig propConfig = new EmitterPropertyConfig.Builder()
                    .addProperty(AllEmitterProperties.BLOOM_INTENSITY, 1.2f)
                    .addProperty(AllEmitterProperties.LIFE_TIME_RANGE, new Vector2f(0.3f, 5.0f))
                    .addProperty(AllEmitterProperties.SIZE_RANGE, new Vector2f(0.1f, 3.0f))
                    .addProperty(AllEmitterProperties.EMIT_FROM_VOLUME, true)
                    .addProperty(AllEmitterProperties.SPHERE_RADIUS, 1.0f)
                    .addProperty(AllEmitterProperties.VELOCITY_MODE, VelocityModeStates.RANDOM)
                    .addProperty(AllEmitterProperties.BASE_VELOCITY, new Vector3f(0, 2.0f, 0))
                    .addProperty(AllEmitterProperties.POSITION, new Vector3f(position.x, position.y + 0.5f, position.z))
                    .build();

            EmitterConfig config = new EmitterConfig(
                    EmitterType.SPHERE,
                    0.0f,
                    position,
                    propConfig,
                    bursts
            );
            builder.addEmitterConfig(config);
        }
        {
            ParticleBurst[] bursts = new ParticleBurst[] {
                    new ParticleBurst(0.0f, 1_5000),
                    new ParticleBurst(0.5f, 1_5000)
            };
            EmitterPropertyConfig propConfig = new EmitterPropertyConfig.Builder()
                    .addProperty(AllEmitterProperties.BLOOM_INTENSITY, 1.2f)
                    .addProperty(AllEmitterProperties.COLOR, new Vector4f(2.0f, 0.5f, 1.0f, 1.0f))
                    .addProperty(AllEmitterProperties.LIFE_TIME_RANGE, new Vector2f(0.3f, 5.0f))
                    .addProperty(AllEmitterProperties.SIZE_RANGE, new Vector2f(0.1f, 3.0f))
                    .addProperty(AllEmitterProperties.VELOCITY_MODE, VelocityModeStates.RANDOM)
                    .addProperty(AllEmitterProperties.BASE_VELOCITY, new Vector3f(0, 2.0f, 0))
                    .addProperty(AllEmitterProperties.POSITION, new Vector3f(position.x, position.y - 5.0f, position.z))
                    .addProperty(AllEmitterProperties.END_POSITION, new Vector3f(position.x, position.y + 5.0f, position.z))
                    .build();

            EmitterConfig config = new EmitterConfig(
                    EmitterType.LINE,
                    0.0f,
                    position,
                    propConfig,
                    bursts
            );
            builder.addEmitterConfig(config);
        }
        {
            ParticleBurst[] bursts = new ParticleBurst[] {
                    new ParticleBurst(0.0f, 3_0000)
            };
            EmitterPropertyConfig propConfig = new EmitterPropertyConfig.Builder()
                    .addProperty(AllEmitterProperties.INNER_OUTER_RADIUS, new Vector2f(3.0f, 5.0f))
                    .addProperty(AllEmitterProperties.BLOOM_INTENSITY, 1.2f)
                    .addProperty(AllEmitterProperties.COLOR, new Vector4f(3.0f, 1.5f, 1.4f, 1.0f))
                    .addProperty(AllEmitterProperties.LIFE_TIME_RANGE, new Vector2f(0.3f, 5.0f))
                    .addProperty(AllEmitterProperties.SIZE_RANGE, new Vector2f(0.1f, 2.0f))
                    .addProperty(AllEmitterProperties.VELOCITY_MODE, VelocityModeStates.RADIAL_FROM_CENTER)
                    .addProperty(AllEmitterProperties.BASE_VELOCITY, new Vector3f(0, 10.0f, 0))
                    .build();

            EmitterConfig config = new EmitterConfig(
                    EmitterType.CIRCLE,
                    0.0f,
                    position,
                    propConfig,
                    bursts
            );
            builder.addEmitterConfig(config);
        }
        ServerEffect serverEffect = ServerEffectManager.getInstance().createEffect(builder);
        if (serverEffect == null) {
            return;
        }

        double distance = 128.0D;
        for (ServerPlayer player : level.players()) {
            if (player.distanceToSqr(position.x, position.y, position.z) < distance * distance) {
                Networking.get().sendToPlayer(player, new S2CEffectSpawn(serverEffect.spawnData));
            }
        }
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
