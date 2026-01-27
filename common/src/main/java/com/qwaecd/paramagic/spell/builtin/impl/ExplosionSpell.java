package com.qwaecd.paramagic.spell.builtin.impl;

import com.qwaecd.paramagic.Paramagic;
import com.qwaecd.paramagic.core.particle.builder.PhysicsParamBuilder;
import com.qwaecd.paramagic.core.particle.emitter.EmitterType;
import com.qwaecd.paramagic.core.particle.emitter.ParticleBurst;
import com.qwaecd.paramagic.core.particle.emitter.property.key.AllEmitterProperties;
import com.qwaecd.paramagic.core.particle.emitter.property.type.VelocityModeStates;
import com.qwaecd.paramagic.feature.effect.explosion.ExplosionAssets;
import com.qwaecd.paramagic.mixin.accessor.IServerLevelAccessor;
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
import com.qwaecd.paramagic.spell.SpellIdentifier;
import com.qwaecd.paramagic.spell.builder.SpellDefBuilder;
import com.qwaecd.paramagic.spell.builtin.BuiltinSpell;
import com.qwaecd.paramagic.spell.config.SpellMetaConfig;
import com.qwaecd.paramagic.spell.core.SpellDefinition;
import com.qwaecd.paramagic.spell.logic.ExecutionContext;
import com.qwaecd.paramagic.spell.phase.SpellPhaseType;
import com.qwaecd.paramagic.spell.session.server.ServerSession;
import com.qwaecd.paramagic.spell.session.server.ServerSessionListener;
import com.qwaecd.paramagic.spell.session.server.ServerSessionView;
import com.qwaecd.paramagic.spell.session.store.AllSessionDataKeys;
import com.qwaecd.paramagic.spell.session.store.SessionDataValue;
import com.qwaecd.paramagic.spell.view.position.PositionRuleSpec;
import com.qwaecd.paramagic.spell.view.position.PositionRuleType;
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
import java.util.Objects;
import java.util.Random;

@PlatformScope(PlatformScopeType.COMMON)
public class ExplosionSpell implements BuiltinSpell {
    public static final SpellIdentifier SPELL_ID = new SpellIdentifier(Paramagic.MOD_ID, "explosion");
    public static final SpellDefinition explosionSpellDefinition;

    static {
        SpellMetaConfig meta = new SpellMetaConfig(SpellPhaseType.COOLDOWN);
        explosionSpellDefinition = SpellDefBuilder.withSpellId(SPELL_ID)
                .withMeta(meta)
                .phase(SpellPhaseType.IDLE, 0.1f)
                .phaseWithAssets(SpellPhaseType.CASTING, 3.0f).circleAssets(ExplosionAssets.create())
                .positionRule(PositionRuleSpec.fixedAtCasterFeet())
                .transformConfig(new Vector3f(1.0f), new Vector3f())
                .endAsset()
                .phaseWithAssets(SpellPhaseType.CHANNELING, 8.0f).circleAssets(ExplosionAssets.create())
                .positionRule(PositionRuleType.IN_FRONT_OF_CASTER, new Vector3f(0.5f), false, new Vector3f())
                .transformConfig(new Vector3f(0.15f), new Vector3f())
                .endAsset()
                .phase(SpellPhaseType.COOLDOWN, 0.0f)
                .build();
    }

    public ExplosionSpell() {
    }

    @Override
    public SpellIdentifier getSpellId() {
        return SPELL_ID;
    }

    @Override
    public SpellDefinition definition() {
        return explosionSpellDefinition;
    }

    @Override
    @PlatformScope(PlatformScopeType.SERVER)
    public void execute(ExecutionContext context) {
        ServerLevel level = context.level;
        Entity casterEntity = level.getEntity(context.caster.getEntityNetworkId());
        if (casterEntity == null) {
            return;
        }

        ServerSession session = context.getServerSession();
        SessionDataValue<Vector3f> value = session.getDataStore().getValue(AllSessionDataKeys.firstPosition);
        if (value == null) {
            return;
        }
        Vector3f pos = value.value;
        level.explode(
                casterEntity,
                pos.x,
                pos.y + 12.0f,
                pos.z,
                128.0f,
                false,
                ServerLevel.ExplosionInteraction.BLOCK
        );
        genParticleData(pos, casterEntity, level);
    }

    @Override
    public void onCreateSession(ServerSession session) {
        SessionListener sessionListener = new SessionListener();
        session.registerListener(sessionListener);

    }

    private static class LightningSpawner {
        private static final int maxChunkDistance = 5;
        private static final int minChunkDistance = 2;
        private static final Random random = new Random();
        private float nextSpawnInterval = random.nextFloat(1.0f, 5.0f);
        private float currentTime = 0.0f;
        private boolean shouldSpawn = true;

        @Setter
        private Vec3 center = Vec3.ZERO;

        void tick(float deltaTime) {
            currentTime += deltaTime;
            if (currentTime >= nextSpawnInterval) {
                currentTime = 0.0f;
                nextSpawnInterval = random.nextFloat(0.5f, 5.0f);
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

    private static class SessionListener implements ServerSessionListener {
        private ServerSessionView view;
        private final LightningSpawner spawner = new LightningSpawner();

        @Override
        public void bind(ServerSessionView session) {
            this.view = session;
        }

        @Override
        public void onPhaseChanged(SpellPhaseType oldPhase, SpellPhaseType currentPhase) {
            if (currentPhase == SpellPhaseType.CASTING && oldPhase == SpellPhaseType.IDLE) {
                this.setThunder(this.v().getLevel());
            }
        }

        @Override
        public void onTick(SpellPhaseType currentPhase, float deltaTime) {
            this.spawner.tick(deltaTime);
            if (!this.spawner.shouldSpawn()) {
                return;
            }
            ServerSessionView v = this.v();
            ServerLevel level = v.getLevel();
            this.spawner.setCenter(v.getCaster().position());
            this.summonLighting(level);
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

        private void setClear(ServerLevel level) {
            int clearTime = ServerLevel.RAIN_DELAY.sample(level.getRandom());
            level.setWeatherParameters(clearTime, 0, false, false);
        }

        @Override
        public void onSpellInterrupted() {
            this.setClear(this.v().getLevel());
        }

        @Override
        public void onSpellCompleted() {
            this.setClear(this.v().getLevel());
        }

        private ServerSessionView v() {
            return Objects.requireNonNull(this.view, "SessionView is not bound");
        }
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
