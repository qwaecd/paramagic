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
import com.qwaecd.paramagic.spell.phase.SpellPhaseType;
import com.qwaecd.paramagic.spell.session.server.ServerSessionView;
import com.qwaecd.paramagic.spell.session.server.SpellExecutor;
import com.qwaecd.paramagic.spell.session.store.AllSessionDataKeys;
import com.qwaecd.paramagic.spell.session.store.SessionDataValue;
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

public class ExplosionExecutor extends SpellExecutor {
    private final LightningSpawner spawner = new LightningSpawner();

    @Nullable
    private ServerLevel levelCache;

    @Override
    public void tick(ServerSessionView session, SpellPhaseType currentPhase, ServerLevel level) {
        if (this.levelCache == null) {
            this.levelCache = level;
        }

        this.spawner.tick();
        if (!this.spawner.shouldSpawn()) {
            return;
        }

        Vec3 position = session.getCaster().position();
        this.spawner.setCenter(position);
        this.summonLighting(level);
    }

    @Override
    public void onPhaseChanged(ServerSessionView session, SpellPhaseType oldPhase, SpellPhaseType currentPhase) {
        ServerLevel level = session.getLevel();
        if (currentPhase == SpellPhaseType.CASTING) {
            this.setThunder(level);
            return;
        }

        if (currentPhase == SpellPhaseType.COOLDOWN) {
            Entity caster = level.getEntity(session.getCaster().getEntityNetworkId());
            this.execute(session, level, caster);
        }
    }

    @Override
    public void onSessionClose() {
        this.setClear();
    }

    @Override
    public void onInterrupt() {
        this.setClear();
    }

    private void summonLighting(ServerLevel level) {
        BlockPos spawnPosition = this.spawner.getSpawnPosition(level);
        if (spawnPosition == null) {
            return;
        }
        LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(level);
        if (lightningBolt != null) {
            lightningBolt.moveTo(Vec3.atBottomCenterOf(spawnPosition));
            level.addFreshEntity(lightningBolt);
        }
    }

    private void setThunder(ServerLevel level) {
        int weatherTime = ServerLevel.THUNDER_DURATION.sample(level.getRandom());
        level.setWeatherParameters(0, weatherTime, true, true);
    }

    private void setClear() {
        if (this.levelCache == null) {
            return;
        }
        int clearTime = ServerLevel.RAIN_DELAY.sample(this.levelCache.getRandom());
        levelCache.setWeatherParameters(clearTime, 0, false, false);
    }

    static class LightningSpawner {
        private static final float tickPerSecond = 1.0f / 20.0f;
        private static final int maxChunkDistance = 5;
        private static final int minChunkDistance = 2;
        private static final Random random = new Random();
        private float nextSpawnInterval = random.nextFloat(1.0f, 5.0f);
        private float currentTime = 0.0f;
        private boolean shouldSpawn = false;

        @Setter
        private Vec3 center = Vec3.ZERO;

        void tick() {
            this.currentTime += tickPerSecond;
            if (this.currentTime >= this.nextSpawnInterval) {
                this.currentTime = 0.0f;
                this.nextSpawnInterval = random.nextFloat(0.5f, 5.0f);
                this.shouldSpawn = true;
            }
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
            int lowerBound = minChunkDistance * 16 + 1;
            int upperBound = maxChunkDistance * 16 - 1;

            int rangeSize = (upperBound - lowerBound + 1) * 2;

            int offsetIndex = random.nextInt(rangeSize);
            int d = lowerBound + (offsetIndex / 2);
            boolean positive = (offsetIndex % 2 == 0);

            return positive ? i + d : i - d;
        }

        boolean shouldSpawn() {
            return this.shouldSpawn;
        }
    }

    private void execute(ServerSessionView session, ServerLevel level, Entity caster) {
        if (caster == null) {
            return;
        }

        SessionDataValue<Vector3f> value = session.getDataStore().getValue(AllSessionDataKeys.firstPosition);
        if (value == null) {
            return;
        }
        Vector3f pos = value.value;
        level.explode(
                caster,
                pos.x,
                pos.y + 12.0f,
                pos.z,
                128.0f,
                false,
                ServerLevel.ExplosionInteraction.BLOCK
        );
        genParticleData(pos, caster, level);
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

        final double distance = 128.0D;
        for (ServerPlayer player : level.players()) {
            if (player.distanceToSqr(position.x, position.y, position.z) < distance * distance) {
                Networking.get().sendToPlayer(player, new S2CEffectSpawn(serverEffect.spawnData));
            }
        }
    }
}
